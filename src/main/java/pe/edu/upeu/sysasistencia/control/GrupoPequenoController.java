package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.GrupoPequenoDTO;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.GrupoPequenoMapper;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import pe.edu.upeu.sysasistencia.servicio.IGrupoPequenoService;

import java.util.List;

@RestController
@RequestMapping("/grupos-pequenos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GrupoPequenoController {

    private final IGrupoPequenoService grupoService;
    private final GrupoPequenoMapper grupoMapper;

    @GetMapping
    public ResponseEntity<List<GrupoPequenoDTO>> findAll() {
        List<GrupoPequenoDTO> list = grupoMapper.toDTOs(grupoService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoPequenoDTO> findById(@PathVariable Long id) {
        GrupoPequeno obj = grupoService.findById(id);
        return ResponseEntity.ok(grupoMapper.toDTO(obj));
    }

    @GetMapping("/grupo-general/{grupoGeneralId}")
    public ResponseEntity<List<GrupoPequenoDTO>> findByGrupoGeneral(
            @PathVariable Long grupoGeneralId
    ) {
        List<GrupoPequenoDTO> list = grupoMapper.toDTOs(
                grupoService.findByGrupoGeneral(grupoGeneralId)
        );
        return ResponseEntity.ok(list);
    }


    @GetMapping("/lider/{liderId}")
    public ResponseEntity<List<GrupoPequenoDTO>> findByLider(@PathVariable Long liderId) {
        List<GrupoPequenoDTO> list = grupoMapper.toDTOs(
                grupoService.findByLider(liderId)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/disponibles/{eventoGeneralId}") // <-- CAMBIO AQUÍ
    public ResponseEntity<List<ParticipanteDisponibleDTO>> getParticipantesDisponibles(
            @PathVariable Long eventoGeneralId
    ) {
        List<ParticipanteDisponibleDTO> list = grupoService.getParticipantesDisponibles(eventoGeneralId);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<GrupoPequenoDTO> save(@RequestBody GrupoPequenoDTO dto) {
        GrupoPequeno obj = grupoService.save(grupoMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoPequenoDTO> update(
            @PathVariable Long id,
            @RequestBody GrupoPequenoDTO dto
    ) {
        dto.setIdGrupoPequeno(id);
        GrupoPequeno obj = grupoService.update(id, grupoMapper.toEntity(dto));
        return ResponseEntity.ok(grupoMapper.toDTO(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.delete(id));
    }
}