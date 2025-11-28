package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import java.util.Optional;
import java.util.List;

public interface IUsuarioRepository extends ICrudGenericoRepository<Usuario, Long>{
    Optional<Usuario> findOneByUser(String user);

    @Query(value = "SELECT u.* FROM upeu_usuario u " +
            "JOIN upeu_usuario_rol ur ON u.id_usuario = ur.usuario_id " +
            "JOIN upeu_roles r ON ur.rol_id = r.id_rol " +
            "WHERE r.nombre = :rolNombre",
            nativeQuery = true)
    List<Usuario> findByRol(@Param("rolNombre") String rolNombre);

    @Query(value = "SELECT r.nombre FROM upeu_roles r " +
            "JOIN upeu_usuario_rol ur ON r.id_rol = ur.rol_id " +
            "JOIN upeu_usuario u ON ur.usuario_id = u.id_usuario " +
            "WHERE u.user = :username",
            nativeQuery = true)
    List<String> findRolesByUsername(@Param("username") String username);
}
