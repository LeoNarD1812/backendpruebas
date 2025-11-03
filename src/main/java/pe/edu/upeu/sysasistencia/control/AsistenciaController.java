package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.AsistenciaDTO;
import pe.edu.upeu.sysasistencia.dtos.AsistenciaRegistroDTO;
import pe.edu.upeu.sysasistencia.dtos.ReporteAsistenciaDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.AsistenciaMapper;
import pe.edu.upeu.sysasistencia.modelo.Asistencia;
import pe.edu.upeu.sysasistencia.servicio.IAsistenciaService;

import java.util.List;

@RestController
@RequestMapping("/asistencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AsistenciaController {

    private final IAsistenciaService asistenciaService;
    private final AsistenciaMapper asistenciaMapper;

    @GetMapping
    public ResponseEntity<List<AsistenciaDTO>> findAll() {
        List<AsistenciaDTO> list = asistenciaMapper.toDTOs(asistenciaService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> findById(@PathVariable Long id) {
        Asistencia obj = asistenciaService.findById(id);
        return ResponseEntity.ok(asistenciaMapper.toDTO(obj));
    }

    @GetMapping("/evento/{eventoEspecificoId}")
    public ResponseEntity<List<AsistenciaDTO>> findByEventoEspecifico(
            @PathVariable Long eventoEspecificoId
    ) {
        List<AsistenciaDTO> list = asistenciaMapper.toDTOs(
                asistenciaService.findByEventoEspecifico(eventoEspecificoId)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<AsistenciaDTO>> findByPersona(@PathVariable Long personaId) {
        List<AsistenciaDTO> list = asistenciaMapper.toDTOs(
                asistenciaService.findByPersona(personaId)
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping("/registrar")
    public ResponseEntity<AsistenciaDTO> registrarAsistencia(
            @RequestBody AsistenciaRegistroDTO dto
    ) {
        Asistencia obj = asistenciaService.registrarAsistencia(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaMapper.toDTO(obj));
    }

    @GetMapping("/reporte/{eventoGeneralId}")
    public ResponseEntity<List<ReporteAsistenciaDTO>> generarReporte(
            @PathVariable Long eventoGeneralId
    ) {
        List<ReporteAsistenciaDTO> reporte = asistenciaService.generarReporteAsistencia(eventoGeneralId);
        return ResponseEntity.ok(reporte);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> update(
            @PathVariable Long id,
            @RequestBody AsistenciaDTO dto
    ) {
        dto.setIdAsistencia(id);
        Asistencia obj = asistenciaService.update(id, asistenciaMapper.toEntity(dto));
        return ResponseEntity.ok(asistenciaMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(asistenciaService.delete(id));
    }
}