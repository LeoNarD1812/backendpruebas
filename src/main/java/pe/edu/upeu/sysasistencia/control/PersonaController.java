package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // NUEVO: Endpoint para obtener el perfil del usuario autenticado
    @GetMapping("/my-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIDER', 'INTEGRANTE', 'SUPERADMIN')") // DESCOMENTADO
    public ResponseEntity<PersonaDTO> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Attempting to get profile for username: {}", username); // Log 1

        Usuario usuario = usuarioService.findOneByUser(username)
                .orElseThrow(() -> {
                    log.warn("Usuario not found for username: {}", username); // Log 2
                    return new ModelNotFoundException("Usuario no encontrado con username: " + username);
                });
        log.info("Found user with ID: {}", usuario.getIdUsuario()); // Log 3

        Persona persona = personaService.findByUsuarioId(usuario.getIdUsuario())
                .orElseThrow(() -> {
                    log.warn("Persona not found for user ID: {}", usuario.getIdUsuario()); // Log 4
                    return new ModelNotFoundException("Persona no encontrada para el usuario: " + username);
                });
        log.info("Found persona with ID: {}", persona.getIdPersona()); // Log 5

        return ResponseEntity.ok(personaMapper.toDTO(persona));
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> save(@RequestBody PersonaDTO dto) {
        Persona obj = personaService.save(personaMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede actualizar cualquier perfil por ID
    public ResponseEntity<PersonaDTO> update(@PathVariable Long id, @RequestBody PersonaDTO dto) {
        dto.setIdPersona(id);
        Persona obj = personaService.update(id, personaMapper.toEntity(dto));
        return ResponseEntity.ok(personaMapper.toDTO(obj));
    }

    @PutMapping("/my-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIDER', 'INTEGRANTE', 'SUPERADMIN')") // Todos los roles pueden actualizar su propio perfil
    public ResponseEntity<PersonaDTO> updateMyProfile(@RequestBody PersonaDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Attempting to update profile for username: {}", username); // Log para update

        Usuario usuario = usuarioService.findOneByUser(username)
                .orElseThrow(() -> {
                    log.warn("Usuario not found for username: {}", username);
                    return new ModelNotFoundException("Usuario no encontrado con username: " + username);
                });

        Persona persona = personaService.findByUsuarioId(usuario.getIdUsuario())
                .orElseThrow(() -> {
                    log.warn("Persona not found for user ID: {}", usuario.getIdUsuario());
                    return new ModelNotFoundException("Persona no encontrada para el usuario: " + username);
                });

        // Asegurarse de que el ID de la persona en el DTO coincida con el ID de la persona del usuario autenticado
        dto.setIdPersona(persona.getIdPersona());
        // El usuario no puede cambiar su tipo de persona a través de este endpoint
        // dto.setTipoPersona(persona.getTipoPersona()); // Mantener el tipo de persona existente

        Persona obj = personaService.update(persona.getIdPersona(), personaMapper.toEntity(dto));
        return ResponseEntity.ok(personaMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede eliminar perfiles
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.delete(id));
    }
}