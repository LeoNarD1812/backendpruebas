package pe.edu.upeu.sysasistencia.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.modelo.UsuarioRol;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRepository;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRolRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final IUsuarioRolRepository repo;
    private final IUsuarioRepository repoU;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        Usuario u = repoU.findOneByUser(username).orElse(null);
        if (u == null) {
            log.warn("Usuario not found in IUsuarioRepository for username: {}", username);
            throw new UsernameNotFoundException("Usuario not found: " + username);
        }
        log.info("Found Usuario: {} with ID: {}", u.getUser(), u.getIdUsuario());

        List<UsuarioRol> usuarioRoles = repo.findOneByUsuarioUser(username);
        if (usuarioRoles == null || usuarioRoles.isEmpty()) {
            log.warn("No roles found for user: {}", username);
            throw new UsernameNotFoundException("No roles found for user: " + username);
        }
        log.info("Found {} roles for user: {}", usuarioRoles.size(), username);

        List<GrantedAuthority> roles = new ArrayList<>();
        usuarioRoles.forEach(usuarioRol -> {
            // Añadir el prefijo "ROLE_" al nombre del rol
            String roleNameWithPrefix = "ROLE_" + usuarioRol.getRol().getNombre().name();
            roles.add(new SimpleGrantedAuthority(roleNameWithPrefix));
            log.debug("Adding role: {}", roleNameWithPrefix);
        });

        log.info("Successfully loaded UserDetails for: {}", username);
        return new org.springframework.security.core.userdetails.User(u.getUser(), u.getClave(), roles);
    }
}