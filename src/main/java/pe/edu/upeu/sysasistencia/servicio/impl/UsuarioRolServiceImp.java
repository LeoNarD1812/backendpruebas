package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.modelo.UsuarioRol;
import pe.edu.upeu.sysasistencia.modelo.UsuarioRolPK;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRolRepository;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioRolService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRolServiceImp implements IUsuarioRolService {
    private final IUsuarioRolRepository repo;

    @Override
    public List<UsuarioRol> findOneByUsuarioUser(String user) {
        return repo.findOneByUsuarioUser(user);
    }

    @Override
    public UsuarioRol save(UsuarioRol ur) {
        return repo.save(ur);
    }

    @Override
    public List<UsuarioRol> findByUsuarioId(Long usuarioId) {
        return repo.findByUsuarioIdUsuario(usuarioId);
    }

    @Override
    public void delete(UsuarioRol usuarioRol) {
        repo.delete(usuarioRol);
    }

    @Override
    public void deleteById(Long id) {
        // Para eliminar por ID en UsuarioRol, necesitamos el ID compuesto (UsuarioRolPK)
        // Esto requeriría buscar el UsuarioRolPK primero o modificar la interfaz
        // Por ahora, si se necesita eliminar por un solo ID, se debería hacer a través de la entidad
        // o se podría buscar el UsuarioRol por el ID del usuario y luego eliminarlo.
        // Dado que delete(UsuarioRol usuarioRol) ya existe, se priorizará ese.
        // Si realmente se necesita eliminar por un solo Long ID, se necesitaría más lógica aquí.
        // Por ejemplo:
        // UsuarioRolPK pk = new UsuarioRolPK(id, /* rolId */);
        // repo.deleteById(pk);
        // Por la forma en que se usa en UsuarioServiceImp, se espera delete(UsuarioRol).
        throw new UnsupportedOperationException("Eliminar UsuarioRol por un solo ID no está directamente soportado sin el ID del rol. Use delete(UsuarioRol usuarioRol) o deleteById(UsuarioRolPK pk).");
    }
}
