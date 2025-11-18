package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioService extends ICrudGenericoService<Usuario, Long>{
    UsuarioDTO login(UsuarioDTO.CredencialesDto credentialsDto);
    UsuarioDTO register(UsuarioDTO.UsuarioCrearDto userDto);
    List<Usuario> findByRol(String rolNombre); // Este devuelve entidades Usuario
    List<Persona> getLideresDisponibles(Long excludeGrupoId);
    Optional<Usuario> findOneByUser(String user);
    UsuarioDTO updateUserAndRole(Long id, UsuarioDTO dto);
    UsuarioDTO mapToUsuarioDTO(Usuario usuario); // Método público para mapear a DTO completo

    // Nuevos métodos para devolver DTOs directamente
    List<UsuarioDTO> findAllDTOs();
    UsuarioDTO findDTOById(Long id);
    List<UsuarioDTO> findByRolDTOs(String rolNombre);
}