package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.dtos.RecurrenceRequestDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;
import java.time.LocalDate;
import java.util.List;

public interface IEventoEspecificoService extends ICrudGenericoService<EventoEspecifico, Long> {
    List<EventoEspecifico> findByEventoGeneral(Long eventoGeneralId);
    List<EventoEspecifico> findByFecha(LocalDate fecha);
    List<EventoEspecifico> findByEventoYRangoFechas(Long eventoId, LocalDate inicio, LocalDate fin);
    List<EventoEspecifico> createRecurrence(RecurrenceRequestDTO dto);
    EventoEspecifico update(Long id, EventoEspecifico eventoEspecifico);
}