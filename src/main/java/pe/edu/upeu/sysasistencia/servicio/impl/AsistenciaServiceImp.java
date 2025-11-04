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

    /**
     * ✅ INTEGRANTE: Registrar asistencia escaneando QR
     * VALIDA FECHA DE LA SESIÓN
     */
    @Override
    public Asistencia registrarAsistencia(AsistenciaRegistroDTO dto) {
        log.info("📱 QR: Registrando asistencia - Sesión={}, Persona={}",
                dto.getEventoEspecificoId(), dto.getPersonaId());

        // 1. Validar sesión
        EventoEspecifico evento = eventoRepo.findById(dto.getEventoEspecificoId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // 2. ✅ VALIDAR FECHA: Solo se puede registrar el día de la sesión
        LocalDate hoy = LocalDate.now();
        if (!evento.getFecha().equals(hoy)) {
            throw new RuntimeException(
                    "Esta sesión es para el " + evento.getFecha() +
                            ". No puedes registrar asistencia fuera de fecha"
            );
        }

        // 3. ✅ VALIDAR HORARIO: No permitir antes de 30 min previos ni después de 2 horas
        LocalTime ahora = LocalTime.now();
        LocalTime ventanaInicio = evento.getHoraInicio().minusMinutes(30);
        LocalTime ventanaFin = evento.getHoraFin().plusHours(2);

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

        // 4. Validar persona
        Persona persona = personaService.findById(dto.getPersonaId());

        // 5. Validar pertenencia al evento
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

        // 6. Verificar duplicado
        var existente = repo.findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(
                dto.getEventoEspecificoId(), dto.getPersonaId()
        );

        if (existente.isPresent()) {
            throw new RuntimeException(
                    "Ya registraste tu asistencia para esta sesión a las " +
                            existente.get().getFechaHoraRegistro().toLocalTime()
            );
        }

        // 7. Calcular estado según hora
        LocalTime horaLimite = evento.getHoraInicio()
                .plusMinutes(evento.getToleranciaMinutos());

        Asistencia.EstadoAsistencia estado;
        if (ahora.isBefore(horaLimite) || ahora.equals(horaLimite)) {
            estado = Asistencia.EstadoAsistencia.PRESENTE;
        } else {
            estado = Asistencia.EstadoAsistencia.TARDE;
        }

        // 8. Crear asistencia
        Asistencia asistencia = Asistencia.builder()
                .eventoEspecifico(evento)
                .persona(persona)
                .fechaHoraRegistro(LocalDateTime.now())
                .estado(estado)
                .observacion(dto.getObservacion())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .build();

        Asistencia guardada = repo.save(asistencia);

        log.info("✅ Asistencia QR registrada: {} - Estado: {} - Hora: {}",
                persona.getNombreCompleto(), estado, ahora);

        return guardada;
    }

    /**
     * ✅ LÍDER: Generar QR para una sesión
     * Retorna imagen Base64 del QR
     */
    public QRResponseDTO generarQRParaSesion(Long eventoEspecificoId, Long liderId) {
        log.info("🔲 Generando QR para sesión {} - Líder {}",
                eventoEspecificoId, liderId);

        // 1. Validar sesión
        EventoEspecifico evento = eventoRepo.findById(eventoEspecificoId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // 2. Validar que el líder tiene grupos en este evento
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

        // 3. Crear datos del QR
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

        try {
            // 4. Convertir a JSON
            String jsonData = objectMapper.writeValueAsString(qrData);

            // 5. Generar imagen QR en Base64
            String qrBase64 = qrCodeService.generarQRBase64(jsonData);

            // 6. Crear respuesta
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

    /**
     * ✅ LÍDER: Obtener lista de participantes para llamado
     */
    public List<ParticipanteAsistenciaDTO> obtenerListaParaLlamado(
            Long eventoEspecificoId,
            Long liderId
    ) {
        log.info("📋 Líder {} solicitando lista para sesión {}",
                liderId, eventoEspecificoId);

        EventoEspecifico evento = eventoRepo.findById(eventoEspecificoId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Long eventoGeneralId = evento.getEventoGeneral().getIdEventoGeneral();

        // Obtener grupos del líder en este evento
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

    /**
     * ✅ LÍDER: Marcar asistencia manualmente
     */
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

        // Validar permiso
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

        // Buscar o crear asistencia
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

    // DTO para respuesta de QR
    @lombok.Data
    public static class QRResponseDTO {
        private String qrImageBase64; // "data:image/png;base64,..."
        private QRAsistenciaDTO qrData;
        private String mensaje;
    }
}