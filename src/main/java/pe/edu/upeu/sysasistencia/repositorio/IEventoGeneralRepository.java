package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import java.time.LocalDate;
import java.util.List;

public interface IEventoGeneralRepository extends ICrudGenericoRepository<EventoGeneral, Long> {

    List<EventoGeneral> findByProgramaIdPrograma(Long programaId);

    List<EventoGeneral> findByEstado(EventoGeneral.EstadoEvento estado);

    @Query("SELECT e FROM EventoGeneral e WHERE e.fechaInicio <= :fecha AND e.fechaFin >= :fecha")
    List<EventoGeneral> findEventosActivos(@Param("fecha") LocalDate fecha);

    // ✅ ACTUALIZADO: Ahora busca por periodoId en lugar de cicloAcademico
    List<EventoGeneral> findByPeriodoIdPeriodoAndProgramaIdPrograma(Long periodoId, Long programaId);

    // ✅ NUEVO: Buscar por periodo
    List<EventoGeneral> findByPeriodoIdPeriodo(Long periodoId);
}