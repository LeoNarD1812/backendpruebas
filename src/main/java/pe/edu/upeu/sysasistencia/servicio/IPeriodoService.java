package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.modelo.Periodo;
import java.util.Optional;
import java.util.List;

public interface IPeriodoService extends ICrudGenericoService<Periodo, Long> {
    Optional<Periodo> findByNombre(String nombre);
    List<Periodo> findByEstado(String estado);
    Periodo getPeriodoActivo();
}