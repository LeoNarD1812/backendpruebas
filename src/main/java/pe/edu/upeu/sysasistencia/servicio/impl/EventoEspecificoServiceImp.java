package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.RecurrenceRequestDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IEventoEspecificoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IEventoGeneralRepository;
import pe.edu.upeu.sysasistencia.servicio.IEventoEspecificoService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoEspecificoServiceImp extends CrudGenericoServiceImp<EventoEspecifico, Long>
        implements IEventoEspecificoService {

    private final IEventoEspecificoRepository repo;
    private final IEventoGeneralRepository eventoGeneralRepository;

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

        // NO actualizar eventoGeneral directamente, solo si es necesario
        // existingEvento.setEventoGeneral(eventoEspecifico.getEventoGeneral());

        return repo.save(existingEvento);
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

        // CORRECCIÓN 3: El repositorio ahora es accesible
        EventoGeneral eventoGeneral = eventoGeneralRepository.findById(dto.getIdEventoGeneral())
                .orElseThrow(() -> new EntityNotFoundException("Evento General no encontrado"));

        // CORRECCIÓN 2: Convertir la hora String a LocalTime antes del bucle para eficiencia
        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

        while (!current.isAfter(dto.getFechaFinRecurrencia())) {
            // DayOfWeek enum returns 1 for Monday, 7 for Sunday
            int dayOfWeekValue = current.getDayOfWeek().getValue();

            // Check if the current day is one of the selected recurrence days
            if (dto.getDiasSemana() != null && dto.getDiasSemana().contains(dayOfWeekValue)) {
                EventoEspecifico newEvent = new EventoEspecifico();
                newEvent.setEventoGeneral(eventoGeneral);

                // HEREDAR lugar y descripción del evento general
                newEvent.setLugar(eventoGeneral.getLugar()); // ← NUEVO
                newEvent.setDescripcion(eventoGeneral.getDescripcion()); // ← NUEVO

                // Opción: Dejar el nombre de la sesión más limpio, o añadir la fecha para distinguirlas
                newEvent.setNombreSesion(dto.getNombreSesion() + " (" + current.getDayOfWeek() + ")");
                newEvent.setFecha(current);

                // CORRECCIÓN 2: Usar los objetos LocalTime ya parseados
                newEvent.setHoraInicio(horaInicio);
                newEvent.setHoraFin(horaFin);
                newEvent.setToleranciaMinutos(dto.getToleranciaMinutos());
                // Asumiendo que el estado se establece por defecto en la entidad o es nulo

                // El 'repo' que está inyectado como 'IEventoEspecificoRepository' es correcto.
                createdEvents.add(repo.save(newEvent));
            }

            current = current.plusDays(1);
        }
        return createdEvents;
    }
}