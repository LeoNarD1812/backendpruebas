package pe.edu.upeu.sysasistencia.servicio.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.AsistenciaRegistroDTO;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteAsistenciaDTO;
import pe.edu.upeu.sysasistencia.dtos.QRAsistenciaDTO;
import pe.edu.upeu.sysasistencia.dtos.ReporteAsistenciaDTO;
import pe.edu.upeu.sysasistencia.modelo.*;
import pe.edu.upeu.sysasistencia.repositorio.*;
import pe.edu.upeu.sysasistencia.servicio.IAsistenciaService;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AsistenciaServiceImp extends CrudGenericoServiceImp<Asistencia, Long>
        implements IAsistenciaService {

    private final IAsistenciaRepository repo;
    private final IEventoEspecificoRepository eventoRepo;
    private final IPersonaService personaService;
    private final IGrupoParticipanteRepository participanteRepo;
    private final IGrupoPequenoRepository grupoPequenoRepo;
    private final QRCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    @Override
    protected ICrudGenericoRepository<Asistencia, Long> getRepo() {
        return repo;
    }

    @Override
    public List<Asistencia> findByEventoEspecifico(Long eventoEspecificoId) {
        return repo.findByEventoEspecificoIdEventoEspecifico(eventoEspecificoId);
    }

    @Override
    public List<Asistencia> findByPersona(Long personaId) {
        return repo.findByPersonaIdPersona(personaId);
    }

    @Override
    public Asistencia registrarAsistencia(AsistenciaRegistroDTO dto) {
        log.info("📱 QR: Registrando asistencia - Sesión={}, Persona={}",
                dto.getEventoEspecificoId(), dto.getPersonaId());

        EventoEspecifico evento = eventoRepo.findById(dto.getEventoEspecificoId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // Usar ZonedDateTime para validaciones de tiempo consistentes
        ZoneId serverZone = ZoneId.systemDefault();
        ZonedDateTime ahora = ZonedDateTime.now(serverZone);
        ZonedDateTime fechaSesion = evento.getFecha().atStartOfDay(serverZone);

        if (!ahora.toLocalDate().equals(fechaSesion.toLocalDate())) {
            throw new RuntimeException(
                    "Esta sesión es para el " + evento.getFecha() +
                            ". No puedes registrar asistencia fuera de fecha"
            );
        }

        ZonedDateTime ventanaInicio = evento.getHoraInicio().atDate(evento.getFecha()).atZone(serverZone).minusMinutes(30);
        ZonedDateTime ventanaFin = evento.getHoraFin().atDate(evento.getFecha()).atZone(serverZone).plusMinutes(10);

        if (ahora.isBefore(ventanaInicio)) {
            throw new RuntimeException(
                    "Es muy temprano. La sesión inicia a las " + evento.getHoraInicio()
            );
        }

        if (ahora.isAfter(ventanaFin)) {
            throw new RuntimeException(
                    "La sesión ya finalizó. No puedes registrar asistencia"
            );
        }

        Persona persona = personaService.findById(dto.getPersonaId());

        Long eventoGeneralId = evento.getEventoGeneral().getIdEventoGeneral();
        boolean perteneceAlEvento = participanteRepo.existeEnEvento(
                dto.getPersonaId(),
                eventoGeneralId
        );

        if (!perteneceAlEvento) {
            throw new RuntimeException(
                    "No estás inscrito en ningún grupo de este evento"
            );
        }

        var existente = repo.findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(
                dto.getEventoEspecificoId(), dto.getPersonaId()
        );

        if (existente.isPresent()) {
            throw new RuntimeException(
                    "Ya registraste tu asistencia para esta sesión a las " +
                            existente.get().getFechaHoraRegistro().toLocalTime()
            );
        }

        ZonedDateTime horaLimite = evento.getHoraInicio()
                .plusMinutes(evento.getToleranciaMinutos()).atDate(evento.getFecha()).atZone(serverZone);

        Asistencia.EstadoAsistencia estado = ahora.isBefore(horaLimite) || ahora.isEqual(horaLimite)
                ? Asistencia.EstadoAsistencia.PRESENTE
                : Asistencia.EstadoAsistencia.TARDE;

        Asistencia asistencia = Asistencia.builder()
                .eventoEspecifico(evento)
                .persona(persona)
                .fechaHoraRegistro(ahora.toLocalDateTime())
                .estado(estado)
                .observacion(dto.getObservacion())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .build();

        Asistencia guardada = repo.save(asistencia);

        log.info("✅ Asistencia QR registrada: {} - Estado: {} - Hora: {}",
                persona.getNombreCompleto(), estado, ahora.toLocalTime());

        return guardada;
    }

    public QRResponseDTO generarQRParaSesion(Long eventoEspecificoId, Long liderId) {
        log.info("🔲 Generando QR para sesión {} - Líder {}",
                eventoEspecificoId, liderId);

        EventoEspecifico evento = eventoRepo.findById(eventoEspecificoId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Long eventoGeneralId = evento.getEventoGeneral().getIdEventoGeneral();

        boolean tieneGrupos = grupoPequenoRepo
                .findByLiderIdPersona(liderId)
                .stream()
                .anyMatch(g -> g.getGrupoGeneral().getEventoGeneral()
                        .getIdEventoGeneral().equals(eventoGeneralId));

        if (!tieneGrupos) {
            throw new RuntimeException(
                    "No tienes grupos asignados en este evento"
            );
        }

        try {
            var existente = repo.findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(eventoEspecificoId, liderId);
            if (existente.isEmpty()) {
                Persona lider = personaService.findById(liderId);
                Asistencia asistenciaLider = Asistencia.builder()
                        .eventoEspecifico(evento)
                        .persona(lider)
                        .fechaHoraRegistro(LocalDateTime.now())
                        .estado(Asistencia.EstadoAsistencia.PRESENTE)
                        .observacion("Asistencia automática por generar QR")
                        .build();
                repo.save(asistenciaLider);
                log.info("✅ Asistencia automática registrada para el líder: {}", lider.getNombreCompleto());
            }
        } catch (Exception e) {
            log.error("❌ Error al registrar asistencia automática del líder: {}", e.getMessage());
        }

        // --- INICIO: LÓGICA DE ZONA HORARIA ---
        ZoneId serverZone = ZoneId.systemDefault();
        ZonedDateTime horaFinConZona = evento.getHoraFin().atDate(evento.getFecha()).atZone(serverZone);
        // --- FIN: LÓGICA DE ZONA HORARIA ---

        QRAsistenciaDTO qrData = new QRAsistenciaDTO();
        qrData.setEventoEspecificoId(eventoEspecificoId);
        qrData.setEventoNombre(evento.getEventoGeneral().getNombre());
        qrData.setSesionNombre(evento.getNombreSesion());
        qrData.setFecha(evento.getFecha());
        qrData.setHoraInicio(evento.getHoraInicio());
        qrData.setHoraFin(evento.getHoraFin());
        qrData.setToleranciaMinutos(evento.getToleranciaMinutos());
        qrData.setLugar(evento.getLugar());
        qrData.setTimestamp(System.currentTimeMillis());
        qrData.setHoraFinISO(horaFinConZona); // AÑADIDO

        try {
            String jsonData = objectMapper.writeValueAsString(qrData);
            String qrBase64 = qrCodeService.generarQRBase64(jsonData);

            QRResponseDTO response = new QRResponseDTO();
            response.setQrImageBase64(qrBase64);
            response.setQrData(qrData);
            response.setMensaje("QR generado exitosamente");

            log.info("✅ QR generado: {} - {}",
                    evento.getNombreSesion(), evento.getFecha());

            return response;

        } catch (IOException | WriterException e) {
            log.error("❌ Error generando QR: {}", e.getMessage());
            throw new RuntimeException("Error al generar código QR: " + e.getMessage());
        }
    }

    public List<ParticipanteAsistenciaDTO> obtenerListaParaLlamado(
            Long eventoEspecificoId,
            Long liderId
    ) {
        log.info("📋 Líder {} solicitando lista para sesión {}",
                liderId, eventoEspecificoId);

        EventoEspecifico evento = eventoRepo.findById(eventoEspecificoId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Long eventoGeneralId = evento.getEventoGeneral().getIdEventoGeneral();

        List<GrupoPequeno> gruposDelLider = grupoPequenoRepo
                .findByLiderIdPersona(liderId)
                .stream()
                .filter(g -> g.getGrupoGeneral().getEventoGeneral()
                        .getIdEventoGeneral().equals(eventoGeneralId))
                .collect(Collectors.toList());

        if (gruposDelLider.isEmpty()) {
            throw new RuntimeException(
                    "No tienes ningún grupo asignado en este evento"
            );
        }

        List<ParticipanteAsistenciaDTO> lista = new ArrayList<>();

        for (GrupoPequeno grupo : gruposDelLider) {
            List<GrupoParticipante> participantes =
                    participanteRepo.findByGrupoPequenoIdGrupoPequeno(
                                    grupo.getIdGrupoPequeno()
                            )
                            .stream()
                            .filter(p -> p.getEstado() == GrupoParticipante.EstadoParticipante.ACTIVO)
                            .collect(Collectors.toList());

            for (GrupoParticipante p : participantes) {
                Persona persona = p.getPersona();

                var asistencia = repo.findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(
                        eventoEspecificoId,
                        persona.getIdPersona()
                );

                ParticipanteAsistenciaDTO dto = new ParticipanteAsistenciaDTO();
                dto.setPersonaId(persona.getIdPersona());
                dto.setNombreCompleto(persona.getNombreCompleto());
                dto.setCodigoEstudiante(persona.getCodigoEstudiante());
                dto.setDocumento(persona.getDocumento());
                dto.setGrupoPequenoNombre(grupo.getNombre());

                if (asistencia.isPresent()) {
                    dto.setTieneAsistencia(true);
                    dto.setEstadoAsistencia(asistencia.get().getEstado().name());
                    dto.setHoraRegistro(asistencia.get().getFechaHoraRegistro());
                    dto.setObservacion(asistencia.get().getObservacion());
                } else {
                    dto.setTieneAsistencia(false);
                    dto.setEstadoAsistencia("PENDIENTE");
                }

                lista.add(dto);
            }
        }

        log.info("✅ Lista: {} participantes", lista.size());
        return lista;
    }

    public Asistencia marcarAsistenciaPorLider(
            Long eventoEspecificoId,
            Long personaId,
            Long liderId,
            Asistencia.EstadoAsistencia estado,
            String observacion
    ) {
        log.info("✍️ Líder {} marcando {} para persona {}",
                liderId, estado, personaId);

        EventoEspecifico evento = eventoRepo.findById(eventoEspecificoId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Long eventoGeneralId = evento.getEventoGeneral().getIdEventoGeneral();

        List<GrupoPequeno> gruposDelLider = grupoPequenoRepo
                .findByLiderIdPersona(liderId)
                .stream()
                .filter(g -> g.getGrupoGeneral().getEventoGeneral()
                        .getIdEventoGeneral().equals(eventoGeneralId))
                .collect(Collectors.toList());

        boolean tienePermiso = gruposDelLider.stream().anyMatch(grupo ->
                participanteRepo.findByGrupoPequenoIdGrupoPequeno(grupo.getIdGrupoPequeno())
                        .stream()
                        .anyMatch(p -> p.getPersona().getIdPersona().equals(personaId) &&
                                p.getEstado() == GrupoParticipante.EstadoParticipante.ACTIVO)
        );

        if (!tienePermiso) {
            throw new RuntimeException(
                    "Esta persona no pertenece a tu grupo"
            );
        }

        var existente = repo.findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(
                eventoEspecificoId, personaId
        );

        Asistencia asistencia;

        if (existente.isPresent()) {
            asistencia = existente.get();
            asistencia.setEstado(estado);
            asistencia.setObservacion(observacion + " (Actualizado por líder)");
        } else {
            Persona persona = personaService.findById(personaId);
            asistencia = Asistencia.builder()
                    .eventoEspecifico(evento)
                    .persona(persona)
                    .fechaHoraRegistro(LocalDateTime.now())
                    .estado(estado)
                    .observacion(observacion + " (Registrado por líder)")
                    .build();
        }

        return repo.save(asistencia);
    }

    @Override
    public List<ReporteAsistenciaDTO> generarReporteAsistencia(Long eventoGeneralId) {
        var participantes = participanteRepo.findByGrupoGeneral(eventoGeneralId);
        var sesiones = eventoRepo.findByEventoGeneralIdEventoGeneral(eventoGeneralId);
        int totalSesiones = sesiones.size();

        return participantes.stream().map(p -> {
            Long personaId = p.getPersona().getIdPersona();

            ReporteAsistenciaDTO dto = new ReporteAsistenciaDTO();
            dto.setPersonaId(personaId);
            dto.setNombreCompleto(p.getPersona().getNombreCompleto());
            dto.setCodigoEstudiante(p.getPersona().getCodigoEstudiante());
            dto.setTotalSesiones(totalSesiones);

            dto.setAsistenciasPresente(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.PRESENTE));
            dto.setAsistenciasTarde(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.TARDE));
            dto.setAsistenciasAusente(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.AUSENTE));
            dto.setAsistenciasJustificado(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.JUSTIFICADO));

            int totalAsistencias = dto.getAsistenciasPresente() + dto.getAsistenciasTarde();
            double porcentaje = totalSesiones > 0 ?
                    (totalAsistencias * 100.0) / totalSesiones : 0.0;
            dto.setPorcentajeAsistencia(Math.round(porcentaje * 100.0) / 100.0);

            return dto;
        }).collect(Collectors.toList());
    }

    @lombok.Data
    public static class QRResponseDTO {
        private String qrImageBase64;
        private QRAsistenciaDTO qrData;
        private String mensaje;
    }
}
