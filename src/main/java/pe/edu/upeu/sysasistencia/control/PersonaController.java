package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.PersonaDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysasistencia.mappers.PersonaMapper;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/personas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PersonaController {
    private final IPersonaService personaService;
    private final PersonaMapper personaMapper;
    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> findAll() {
        List<PersonaDTO> list = personaMapper.toDTOs(personaService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> findById(@PathVariable Long id) {
        Persona obj = personaService.findById(id);
        return ResponseEntity.ok(personaMapper.toDTO(obj));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PersonaDTO> findByCodigo(@PathVariable String codigo) {
        Persona obj = personaService.findByCodigoEstudiante(codigo)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return ResponseEntity.ok(personaMapper.toDTO(obj));
    }

    @GetMapping("/my-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIDER', 'INTEGRANTE', 'SUPERADMIN')")
    public ResponseEntity<PersonaDTO> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = usuarioService.findOneByUser(username)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con username: " + username));
        Persona persona = personaService.findByUsuarioId(usuario.getIdUsuario())
                .orElseThrow(() -> new ModelNotFoundException("Persona no encontrada para el usuario: " + username));
        return ResponseEntity.ok(personaMapper.toDTO(persona));
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> save(@RequestBody PersonaDTO dto) {
        Persona obj = personaService.save(personaMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIDER', 'INTEGRANTE', 'SUPERADMIN')")
    public ResponseEntity<PersonaDTO> update(@PathVariable Long id, @RequestBody PersonaDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String username = authentication.getName();
            Usuario usuario = usuarioService.findOneByUser(username)
                    .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con username: " + username));
            Persona persona = personaService.findByUsuarioId(usuario.getIdUsuario())
                    .orElseThrow(() -> new ModelNotFoundException("Persona no encontrada para el usuario: " + username));

            if (!persona.getIdPersona().equals(id)) {
                throw new AccessDeniedException("Acceso Denegado: No tienes permiso para modificar este perfil.");
            }
        }

        dto.setIdPersona(id);
        Persona obj = personaService.update(id, personaMapper.toEntity(dto));
        return ResponseEntity.ok(personaMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.delete(id));
    }
}
