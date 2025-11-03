package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.PeriodoDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.PeriodoMapper;
import pe.edu.upeu.sysasistencia.modelo.Periodo;
import pe.edu.upeu.sysasistencia.servicio.IPeriodoService;

import java.util.List;

@RestController
@RequestMapping("/periodos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PeriodoController {
    private final IPeriodoService periodoService;
    private final PeriodoMapper periodoMapper;

    @GetMapping
    public ResponseEntity<List<PeriodoDTO>> findAll() {
        List<PeriodoDTO> list = periodoMapper.toDTOs(periodoService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodoDTO> findById(@PathVariable Long id) {
        Periodo obj = periodoService.findById(id);
        return ResponseEntity.ok(periodoMapper.toDTO(obj));
    }

    @GetMapping("/activo")
    public ResponseEntity<PeriodoDTO> getPeriodoActivo() {
        Periodo obj = periodoService.getPeriodoActivo();
        return ResponseEntity.ok(periodoMapper.toDTO(obj));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PeriodoDTO>> findByEstado(@PathVariable String estado) {
        List<PeriodoDTO> list = periodoMapper.toDTOs(
                periodoService.findByEstado(estado)
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<PeriodoDTO> save(@RequestBody PeriodoDTO dto) {
        Periodo obj = periodoService.save(periodoMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(periodoMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodoDTO> update(
            @PathVariable Long id,
            @RequestBody PeriodoDTO dto
    ) {
        dto.setIdPeriodo(id);
        Periodo obj = periodoService.update(id, periodoMapper.toEntity(dto));
        return ResponseEntity.ok(periodoMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(periodoService.delete(id));
    }
}