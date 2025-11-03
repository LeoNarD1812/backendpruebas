package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.EventoGeneralDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.EventoGeneralMapper;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import pe.edu.upeu.sysasistencia.servicio.IEventoGeneralService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos-generales")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventoGeneralController {

    private final IEventoGeneralService eventoService;
    private final EventoGeneralMapper eventoMapper;

    @GetMapping
    public ResponseEntity<List<EventoGeneralDTO>> findAll() {
        List<EventoGeneralDTO> list = eventoMapper.toDTOs(eventoService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoGeneralDTO> findById(@PathVariable Long id) {
        EventoGeneral obj = eventoService.findById(id);
        return ResponseEntity.ok(eventoMapper.toDTO(obj));
    }

    @GetMapping("/programa/{programaId}")
    public ResponseEntity<List<EventoGeneralDTO>> findByPrograma(@PathVariable Long programaId) {
        List<EventoGeneralDTO> list = eventoMapper.toDTOs(
                eventoService.findByPrograma(programaId)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EventoGeneralDTO>> findEventosActivos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        List<EventoGeneralDTO> list = eventoMapper.toDTOs(
                eventoService.findEventosActivos(fecha)
        );
        return ResponseEntity.ok(list);
    }

    // ✅ ACTUALIZADO: Buscar por periodo y programa
    @GetMapping("/periodo/{periodoId}/programa/{programaId}")
    public ResponseEntity<List<EventoGeneralDTO>> findByPeriodoYPrograma(
            @PathVariable Long periodoId,
            @PathVariable Long programaId
    ) {
        List<EventoGeneralDTO> list = eventoMapper.toDTOs(
                eventoService.findByPeriodoYPrograma(periodoId, programaId)
        );
        return ResponseEntity.ok(list);
    }

    // ✅ NUEVO: Buscar solo por periodo
    @GetMapping("/periodo/{periodoId}")
    public ResponseEntity<List<EventoGeneralDTO>> findByPeriodo(
            @PathVariable Long periodoId
    ) {
        List<EventoGeneralDTO> list = eventoMapper.toDTOs(
                eventoService.findByPeriodo(periodoId)
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<EventoGeneralDTO> save(@RequestBody EventoGeneralDTO dto) {
        EventoGeneral obj = eventoService.save(eventoMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoGeneralDTO> update(
            @PathVariable Long id,
            @RequestBody EventoGeneralDTO dto
    ) {
        dto.setIdEventoGeneral(id);
        EventoGeneral obj = eventoService.update(id, eventoMapper.toEntity(dto));
        return ResponseEntity.ok(eventoMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.delete(id));
    }
}