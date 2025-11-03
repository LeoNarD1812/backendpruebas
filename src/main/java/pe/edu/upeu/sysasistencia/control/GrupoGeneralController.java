package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.GrupoGeneralDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.GrupoGeneralMapper;
import pe.edu.upeu.sysasistencia.modelo.GrupoGeneral;
import pe.edu.upeu.sysasistencia.servicio.IGrupoGeneralService;

import java.util.List;

@RestController
@RequestMapping("/grupos-generales")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GrupoGeneralController {

    private final IGrupoGeneralService grupoService;
    private final GrupoGeneralMapper grupoMapper;

    @GetMapping
    public ResponseEntity<List<GrupoGeneralDTO>> findAll() {
        List<GrupoGeneralDTO> list = grupoMapper.toDTOs(grupoService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoGeneralDTO> findById(@PathVariable Long id) {
        GrupoGeneral obj = grupoService.findById(id);
        return ResponseEntity.ok(grupoMapper.toDTO(obj));
    }

    @GetMapping("/evento/{eventoGeneralId}")
    public ResponseEntity<List<GrupoGeneralDTO>> findByEventoGeneral(
            @PathVariable Long eventoGeneralId
    ) {
        List<GrupoGeneralDTO> list = grupoMapper.toDTOs(
                grupoService.findByEventoGeneral(eventoGeneralId)
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<GrupoGeneralDTO> save(@RequestBody GrupoGeneralDTO dto) {
        GrupoGeneral obj = grupoService.save(grupoMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoGeneralDTO> update(
            @PathVariable Long id,
            @RequestBody GrupoGeneralDTO dto
    ) {
        dto.setIdGrupoGeneral(id);
        GrupoGeneral obj = grupoService.update(id, grupoMapper.toEntity(dto));
        return ResponseEntity.ok(grupoMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.delete(id));
    }
}