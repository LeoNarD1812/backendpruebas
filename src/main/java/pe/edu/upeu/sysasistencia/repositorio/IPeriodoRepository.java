package pe.edu.upeu.sysasistencia.repositorio;

import pe.edu.upeu.sysasistencia.modelo.Periodo;
import java.util.Optional;
import java.util.List;

public interface IPeriodoRepository extends ICrudGenericoRepository<Periodo, Long> {
    Optional<Periodo> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Periodo> findByEstado(String estado);
    Optional<Periodo> findFirstByEstadoOrderByFechaInicioDesc(String estado);
}