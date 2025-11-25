package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.dtos.FacultadDTO;
import pe.edu.upeu.sysasistencia.modelo.Facultad;
import java.util.Optional;

public interface IFacultadService extends ICrudGenericoService<Facultad, Long>{
    Optional<Facultad> findByNombre(String nombre);
    Facultad create(FacultadDTO dto);
}
