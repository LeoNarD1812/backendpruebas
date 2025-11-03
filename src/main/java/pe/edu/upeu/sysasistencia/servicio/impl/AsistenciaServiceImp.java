package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.AsistenciaRegistroDTO;
import pe.edu.upeu.sysasistencia.dtos.ReporteAsistenciaDTO;
import pe.edu.upeu.sysasistencia.modelo.Asistencia;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.repositorio.IAsistenciaRepository;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IEventoEspecificoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoParticipanteRepository;
import pe.edu.upeu.sysasistencia.servicio.IAsistenciaService;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        // Validar evento
        EventoEspecifico evento = eventoRepo.findById(dto.getEventoEspecificoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Validar persona
        Persona persona = personaService.findById(dto.getPersonaId());

        // Verificar si ya registró asistencia
        var existente = repo.findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(
                dto.getEventoEspecificoId(), dto.getPersonaId()
        );
        if (existente.isPresent()) {
            throw new RuntimeException("La asistencia ya fue registrada");
        }

        // Calcular estado (PRESENTE, TARDE, AUSENTE)
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime horaEvento = evento.getHoraInicio();
        LocalTime horaLimite = horaEvento.plusMinutes(evento.getToleranciaMinutos());
        LocalTime horaActual = ahora.toLocalTime();

        Asistencia.EstadoAsistencia estado;
        if (horaActual.isBefore(horaLimite) || horaActual.equals(horaLimite)) {
            estado = Asistencia.EstadoAsistencia.PRESENTE;
        } else {
            estado = Asistencia.EstadoAsistencia.TARDE;
        }

        // Crear asistencia
        Asistencia asistencia = Asistencia.builder()
                .eventoEspecifico(evento)
                .persona(persona)
                .fechaHoraRegistro(ahora)
                .estado(estado)
                .observacion(dto.getObservacion())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .build();

        log.info("Asistencia registrada: Persona {} - Evento {} - Estado: {}",
                dto.getPersonaId(), dto.getEventoEspecificoId(), estado);

        return repo.save(asistencia);
    }

    @Override
    public List<ReporteAsistenciaDTO> generarReporteAsistencia(Long eventoGeneralId) {
        // Obtener todos los participantes del evento
        var participantes = participanteRepo.findByGrupoGeneral(eventoGeneralId);

        // Contar sesiones totales del evento
        var sesiones = eventoRepo.findByEventoGeneralIdEventoGeneral(eventoGeneralId);
        int totalSesiones = sesiones.size();

        return participantes.stream().map(p -> {
            Long personaId = p.getPersona().getIdPersona();

            ReporteAsistenciaDTO dto = new ReporteAsistenciaDTO();
            dto.setPersonaId(personaId);
            dto.setNombreCompleto(p.getPersona().getNombreCompleto());
            dto.setCodigoEstudiante(p.getPersona().getCodigoEstudiante());
            dto.setTotalSesiones(totalSesiones);

            // Contar asistencias por estado
            dto.setAsistenciasPresente(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.PRESENTE));
            dto.setAsistenciasTarde(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.TARDE));
            dto.setAsistenciasAusente(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.AUSENTE));
            dto.setAsistenciasJustificado(repo.countByPersonaEventoAndEstado(
                    personaId, eventoGeneralId, Asistencia.EstadoAsistencia.JUSTIFICADO));

            // Calcular porcentaje
            int totalAsistencias = dto.getAsistenciasPresente() + dto.getAsistenciasTarde();
            double porcentaje = totalSesiones > 0 ?
                    (totalAsistencias * 100.0) / totalSesiones : 0.0;
            dto.setPorcentajeAsistencia(Math.round(porcentaje * 100.0) / 100.0);

            return dto;
        }).collect(Collectors.toList());
    }
}