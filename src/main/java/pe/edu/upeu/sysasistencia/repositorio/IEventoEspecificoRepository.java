package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.dtos.RecurrenceRequestDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;
import java.time.LocalDate;
import java.util.List;

public interface IEventoEspecificoRepository extends ICrudGenericoRepository<EventoEspecifico, Long> {

    List<EventoEspecifico> findByEventoGeneralIdEventoGeneral(Long eventoGeneralId);

    List<EventoEspecifico> findByFecha(LocalDate fecha);

    @Query("SELECT e FROM EventoEspecifico e WHERE e.eventoGeneral.idEventoGeneral = :eventoId AND e.fecha BETWEEN :inicio AND :fin")
    List<EventoEspecifico> findByEventoAndRangoFechas(
            @Param("eventoId") Long eventoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    List<EventoEspecifico> findByEstado(EventoEspecifico.EstadoSesion estado);
}