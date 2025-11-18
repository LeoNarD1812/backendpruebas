package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.AccesoDTO;
import pe.edu.upeu.sysasistencia.dtos.MenuGroup;
import pe.edu.upeu.sysasistencia.mappers.AccesoMapper;
import pe.edu.upeu.sysasistencia.servicio.impl.AccesoServiceImp; // Cambiado a la implementación

import java.util.List;

@RestController
@RequestMapping("/accesos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AccesoController {
    // Inyectar la implementación para acceder a ambos métodos
    private final AccesoServiceImp accesoService;
    private final AccesoMapper accesoMapper;

    @PostMapping("/user")
    public ResponseEntity<List<AccesoDTO>> getMenusByUser(@RequestBody String username){
        List<AccesoDTO> accesosDTO = accesoMapper.toDTOs(accesoService.getAccesoByUser(username));
        return ResponseEntity.ok(accesosDTO);
    }

    // Endpoint para la WEB
    @PostMapping("/menu")
    public ResponseEntity<List<MenuGroup>> getMenuByUser(@RequestBody String username){
        List<MenuGroup> menu = accesoService.getMenuByUser(username);
        return ResponseEntity.ok(menu);
    }

    // NUEVO: Endpoint para el MÓVIL
    @PostMapping("/menu-movil")
    public ResponseEntity<List<MenuGroup>> getMenuMovilByUser(@RequestBody String username){
        List<MenuGroup> menu = accesoService.getMenuMovilByUser(username);
        return ResponseEntity.ok(menu);
    }
}
