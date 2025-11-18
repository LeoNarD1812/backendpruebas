package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.PersonaDTO;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.PersonaMapper;
import pe.edu.upeu.sysasistencia.mappers.UsuarioMapper;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {
    private final IUsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper; // Mantener para otros mapeos si es necesario
    private final PersonaMapper personaMapper;

    // ENDPOINTS CRUD PARA GESTIÓN DE USUARIOS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        List<UsuarioDTO> usuarioDTOs = usuarioService.findAllDTOs(); // Usar el nuevo método que devuelve DTOs
        return ResponseEntity.ok(usuarioDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = usuarioService.findDTOById(id); // Usar el nuevo método que devuelve un DTO
        return ResponseEntity.ok(usuarioDTO);
    }

    @PostMapping("/create-with-role")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UsuarioDTO> createUsuarioWithRole(@RequestBody UsuarioDTO.UsuarioCrearDto dto) {
        UsuarioDTO createdUser = usuarioService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        UsuarioDTO updatedUser = usuarioService.updateUserAndRole(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.delete(id));
    }

    // ENDPOINTS ESPECÍFICOS
    @GetMapping("/rol/{rolNombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosPorRol(@PathVariable String rolNombre) {
        List<UsuarioDTO> usuarioDTOs = usuarioService.findByRolDTOs(rolNombre); // Usar el nuevo método que devuelve DTOs
        return ResponseEntity.ok(usuarioDTOs);
    }

    // NUEVO ENDPOINT: Para "Gestión de Participantes"
    @GetMapping("/integrantes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'LIDER')")
    public ResponseEntity<List<UsuarioDTO>> getIntegrantes() {
        List<UsuarioDTO> usuarioDTOs = usuarioService.findByRolDTOs("INTEGRANTE"); // Usar el nuevo método que devuelve DTOs
        return ResponseEntity.ok(usuarioDTOs);
    }

    @GetMapping("/lideres-disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'LIDER')")
    public ResponseEntity<List<PersonaDTO>> getLideresDisponibles(
            @RequestParam(required = false) Long excludeGrupoId
    ) {
        List<Persona> lideres = usuarioService.getLideresDisponibles(excludeGrupoId);
        return ResponseEntity.ok(personaMapper.toDTOs(lideres));
    }
}
