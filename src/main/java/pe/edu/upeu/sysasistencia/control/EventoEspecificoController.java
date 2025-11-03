package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.EventoEspecificoDTO;
import pe.edu.upeu.sysasistencia.dtos.RecurrenceRequestDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.EventoEspecificoMapper;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;
import pe.edu.upeu.sysasistencia.servicio.IEventoEspecificoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos-especificos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventoEspecificoController {

    private final IEventoEspecificoService eventoService;
    private final EventoEspecificoMapper eventoMapper;

    @GetMapping
    public ResponseEntity<List<EventoEspecificoDTO>> findAll() {
        List<EventoEspecificoDTO> list = eventoMapper.toDTOs(eventoService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoEspecificoDTO> findById(@PathVariable Long id) {
        EventoEspecifico obj = eventoService.findById(id);
        return ResponseEntity.ok(eventoMapper.toDTO(obj));
    }

    @GetMapping("/evento-general/{eventoGeneralId}")
    public ResponseEntity<List<EventoEspecificoDTO>> findByEventoGeneral(
            @PathVariable Long eventoGeneralId
    ) {
        List<EventoEspecificoDTO> list = eventoMapper.toDTOs(
                eventoService.findByEventoGeneral(eventoGeneralId)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<EventoEspecificoDTO>> findByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        List<EventoEspecificoDTO> list = eventoMapper.toDTOs(
                eventoService.findByFecha(fecha)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/rango")
    public ResponseEntity<List<EventoEspecificoDTO>> findByRangoFechas(
            @RequestParam Long eventoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        List<EventoEspecificoDTO> list = eventoMapper.toDTOs(
                eventoService.findByEventoYRangoFechas(eventoId, inicio, fin)
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<EventoEspecificoDTO> save(@RequestBody EventoEspecificoDTO dto) {
        EventoEspecifico obj = eventoService.save(eventoMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoMapper.toDTO(obj));
    }

    @PostMapping("/recurrencia") // NEW ENDPOINT
    public ResponseEntity<List<EventoEspecificoDTO>> createRecurrence(
            @RequestBody RecurrenceRequestDTO dto
    ) {
        // Delegate the complex recurrence logic to the service layer
        List<EventoEspecifico> newEvents = eventoService.createRecurrence(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoMapper.toDTOs(newEvents));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoEspecificoDTO> update(
            @PathVariable Long id,
            @RequestBody EventoEspecificoDTO dto
    ) {
        dto.setIdEventoEspecifico(id);
        EventoEspecifico obj = eventoService.update(id, eventoMapper.toEntity(dto));
        return ResponseEntity.ok(eventoMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.delete(id));
    }
}