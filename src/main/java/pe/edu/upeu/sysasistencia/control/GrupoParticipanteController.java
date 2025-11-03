package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.GrupoParticipanteDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.GrupoParticipanteMapper;
import pe.edu.upeu.sysasistencia.modelo.GrupoParticipante;
import pe.edu.upeu.sysasistencia.servicio.IGrupoParticipanteService;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/grupo-participantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GrupoParticipanteController {

    private final IGrupoParticipanteService participanteService;
    private final GrupoParticipanteMapper participanteMapper;

    @GetMapping
    public ResponseEntity<List<GrupoParticipanteDTO>> findAll() {
        List<GrupoParticipanteDTO> list = participanteMapper.toDTOs(participanteService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoParticipanteDTO> findById(@PathVariable Long id) {
        GrupoParticipante obj = participanteService.findById(id);
        return ResponseEntity.ok(participanteMapper.toDTO(obj));
    }

    @GetMapping("/grupo/{grupoPequenoId}")
    public ResponseEntity<List<GrupoParticipanteDTO>> findByGrupoPequeno(
            @PathVariable Long grupoPequenoId
    ) {
        List<GrupoParticipanteDTO> list = participanteMapper.toDTOs(
                participanteService.findByGrupoPequeno(grupoPequenoId)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<GrupoParticipanteDTO>> findByPersona(
            @PathVariable Long personaId
    ) {
        List<GrupoParticipanteDTO> list = participanteMapper.toDTOs(
                participanteService.findByPersona(personaId)
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<GrupoParticipanteDTO> save(@RequestBody GrupoParticipanteDTO dto) {
        try {
            // El frontend envía {grupoPequenoId, personaId}. Usaremos estos campos.
            GrupoParticipante obj = participanteService.agregarParticipante(
                    dto.getGrupoPequenoId(),
                    dto.getPersonaId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(participanteMapper.toDTO(obj));
        } catch (ModelNotFoundException e) {
            // Manejar 404/400 si el grupo o persona no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            // MANEJO DE EXCEPCIONES DE NEGOCIO (Capacidad, Ya Inscrito)
            CustomResponse response = new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    LocalDateTime.now(),
                    e.getMessage(),
                    "Error de lógica de negocio al agregar participante"
            );
            // Devolver 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/remover/{id}")
    public ResponseEntity<CustomResponse> removerParticipante(@PathVariable Long id) {
        participanteService.removerParticipante(id);
        CustomResponse response = new CustomResponse();
        response.setStatusCode(200);
        response.setDatetime(LocalDateTime.now());
        response.setMessage("Participante removido exitosamente");
        response.setDetails("El participante ha sido marcado como INACTIVO");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(participanteService.delete(id));
    }
}