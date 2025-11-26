package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.modelo.Persona;
import java.util.Optional;
import java.util.List;

public interface IPersonaService extends ICrudGenericoService<Persona, Long>{
    Optional<Persona> findByCodigoEstudiante(String codigo);
    Optional<Persona> findByDocumento(String documento);
    Optional<Persona> findByUsuarioId(Long usuarioId);
    List<Persona> findLideresByFacultadAndPrograma(Long facultadId, Long programaId, Long excludeGrupoId);
}
