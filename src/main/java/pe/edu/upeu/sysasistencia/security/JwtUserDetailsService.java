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
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRepository;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRolRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final IUsuarioRolRepository repo;
    private final IUsuarioRepository repoU;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        Usuario u = repoU.findOneByUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario not found: " + username));
        log.info("Found Usuario: {} with ID: {}", u.getUser(), u.getIdUsuario());

        List<String> nombresRoles = repo.getNombresRolesPorUsuario(username);
        if (nombresRoles.isEmpty()) {
            log.warn("No roles found for user: {}", username);
            throw new UsernameNotFoundException("No roles found for user: " + username);
        }
        log.info("Found {} roles for user: {}", nombresRoles.size(), username);

        List<GrantedAuthority> roles = nombresRoles.stream()
                .map(nombreRol -> new SimpleGrantedAuthority("ROLE_" + nombreRol))
                .collect(Collectors.toList());

        log.info("Successfully loaded UserDetails for: {}", username);
        return new org.springframework.security.core.userdetails.User(u.getUser(), u.getClave(), roles);
    }
}
