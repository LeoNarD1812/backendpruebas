package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.RecurrenceRequestDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import pe.edu.upeu.sysasistencia.modelo.Matricula;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.repositorio.*;
import pe.edu.upeu.sysasistencia.servicio.IEventoEspecificoService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoEspecificoServiceImp extends CrudGenericoServiceImp<EventoEspecifico, Long>
        implements IEventoEspecificoService {

    private final IEventoEspecificoRepository repo;
    private final IEventoGeneralRepository eventoGeneralRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IPersonaRepository personaRepository;
    private final IMatriculaRepository matriculaRepository;

    @Override
    protected ICrudGenericoRepository<EventoEspecifico, Long> getRepo() {
        return repo;
    }

    @Override
    public List<EventoEspecifico> findByEventoGeneral(Long eventoGeneralId) {
        return repo.findByEventoGeneralIdEventoGeneral(eventoGeneralId);
    }
    @Override
    public EventoEspecifico update(Long id, EventoEspecifico eventoEspecifico) {
        // Primero obtener la entidad existente
        EventoEspecifico existingEvento = findById(id);

        // Actualizar solo los campos necesarios, no la referencia completa
        existingEvento.setNombreSesion(eventoEspecifico.getNombreSesion());
        existingEvento.setFecha(eventoEspecifico.getFecha());
        existingEvento.setHoraInicio(eventoEspecifico.getHoraInicio());
        existingEvento.setHoraFin(eventoEspecifico.getHoraFin());
        existingEvento.setLugar(eventoEspecifico.getLugar());
        existingEvento.setDescripcion(eventoEspecifico.getDescripcion());
        existingEvento.setToleranciaMinutos(eventoEspecifico.getToleranciaMinutos());
        existingEvento.setEstado(eventoEspecifico.getEstado());

        return repo.save(existingEvento);
    }

    @Override
    public List<EventoEspecifico> findMySesiones() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findOneByUser(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Optional<Matricula> matriculaReciente = personaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .flatMap(persona -> matriculaRepository.findByPersonaIdPersona(persona.getIdPersona())
                        .stream()
                        .max(Comparator.comparing(m -> m.getPeriodo().getFechaInicio())));

        if (matriculaReciente.isPresent()) {
            Long programaId = matriculaReciente.get().getProgramaEstudio().getIdPrograma();
            return repo.findByProgramaEstudioId(programaId);
        }

        // Si no es un estudiante con matrícula (ej. solo un invitado), devuelve una lista vacía.
        return new ArrayList<>();
    }

    @Override
    public List<EventoEspecifico> findByFecha(LocalDate fecha) {
        return repo.findByFecha(fecha);
    }

    @Override
    public List<EventoEspecifico> findByEventoYRangoFechas(Long eventoId, LocalDate inicio, LocalDate fin) {
        return repo.findByEventoAndRangoFechas(eventoId, inicio, fin);
    }
    @Override
    public List<EventoEspecifico> createRecurrence(RecurrenceRequestDTO dto) {
        List<EventoEspecifico> createdEvents = new ArrayList<>();
        LocalDate current = dto.getFechaInicioRecurrencia();

        EventoGeneral eventoGeneral = eventoGeneralRepository.findById(dto.getIdEventoGeneral())
                .orElseThrow(() -> new EntityNotFoundException("Evento General no encontrado"));

        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

        while (!current.isAfter(dto.getFechaFinRecurrencia())) {
            int dayOfWeekValue = current.getDayOfWeek().getValue();

            if (dto.getDiasSemana() != null && dto.getDiasSemana().contains(dayOfWeekValue)) {
                EventoEspecifico newEvent = new EventoEspecifico();
                newEvent.setEventoGeneral(eventoGeneral);
                newEvent.setLugar(eventoGeneral.getLugar());
                newEvent.setDescripcion(eventoGeneral.getDescripcion());
                newEvent.setNombreSesion(dto.getNombreSesion() + " (" + current.getDayOfWeek() + ")");
                newEvent.setFecha(current);
                newEvent.setHoraInicio(horaInicio);
                newEvent.setHoraFin(horaFin);
                newEvent.setToleranciaMinutos(dto.getToleranciaMinutos());
                createdEvents.add(repo.save(newEvent));
            }

            current = current.plusDays(1);
        }
        return createdEvents;
    }
}
