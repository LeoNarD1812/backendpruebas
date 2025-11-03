package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    public ResponseEntity<?> save(@RequestBody GrupoParticipanteDTO dto) {
        try {
            log.info("📥 Solicitud para agregar participante: Grupo={}, Persona={}",
                    dto.getGrupoPequenoId(), dto.getPersonaId());

            // Validaciones básicas
            if (dto.getGrupoPequenoId() == null) {
                log.error("❌ grupoPequenoId es null");
                return ResponseEntity.badRequest().body(
                        crearErrorResponse("El ID del grupo es obligatorio")
                );
            }

            if (dto.getPersonaId() == null) {
                log.error("❌ personaId es null");
                return ResponseEntity.badRequest().body(
                        crearErrorResponse("El ID de la persona es obligatorio")
                );
            }

            // Intentar agregar participante
            GrupoParticipante obj = participanteService.agregarParticipante(
                    dto.getGrupoPequenoId(),
                    dto.getPersonaId()
            );

            log.info("✅ Participante agregado exitosamente");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(participanteMapper.toDTO(obj));

        } catch (ModelNotFoundException e) {
            log.error("❌ Entidad no encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse(e.getMessage()));

        } catch (RuntimeException e) {
            log.error("❌ Error de lógica de negocio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("❌ Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/remover/{id}")
    public ResponseEntity<CustomResponse> removerParticipante(@PathVariable Long id) {
        try {
            participanteService.removerParticipante(id);

            CustomResponse response = new CustomResponse();
            response.setStatusCode(200);
            response.setDatetime(LocalDateTime.now());
            response.setMessage("Participante removido exitosamente");
            response.setDetails("El participante ha sido marcado como INACTIVO");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al remover participante: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(participanteService.delete(id));
    }

    // ✅ MÉTODO AUXILIAR PARA RESPUESTAS DE ERROR CONSISTENTES
    private CustomResponse crearErrorResponse(String mensaje) {
        CustomResponse response = new CustomResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setDatetime(LocalDateTime.now());
        response.setMessage(mensaje);
        response.setDetails("Error al procesar la solicitud");
        return response;
    }
}