package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.mappers.UsuarioMapper;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioService;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {
    private final IUsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    // ... (otros endpoints como findAll, findById, etc. si son necesarios)

    // NUEVO ENDPOINT: /users/rol/{rolNombre}
    // Requerido por el frontend para obtener líderes
    @GetMapping("/rol/{rolNombre}")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosPorRol(@PathVariable String rolNombre) {
        List<Usuario> usuarios = usuarioService.findByRol(rolNombre);
        return ResponseEntity.ok(usuarioMapper.toDTOs(usuarios));
    }
}