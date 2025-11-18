package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.modelo.UsuarioRol;
import java.util.List;

public interface IUsuarioRolService {
    List<UsuarioRol> findOneByUsuarioUser(String user);
    UsuarioRol save(UsuarioRol ur);
    List<UsuarioRol> findByUsuarioId(Long usuarioId); // Nuevo método
    void delete(UsuarioRol usuarioRol); // Nuevo método para eliminar por entidad
    void deleteById(Long id); // Añadido para poder eliminar por ID si es necesario
}