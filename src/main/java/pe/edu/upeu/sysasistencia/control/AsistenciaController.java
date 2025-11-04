package pe.edu.upeu.sysasistencia.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.sysasistencia.dtos.*;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.AsistenciaMapper;
import pe.edu.upeu.sysasistencia.modelo.Asistencia;
import pe.edu.upeu.sysasistencia.servicio.impl.AsistenciaServiceImp;

import java.util.List;

@RestController
@RequestMapping("/asistencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaServiceImp asistenciaService;
    private final AsistenciaMapper asistenciaMapper;

    // ========== ENDPOINTS GENERALES ==========

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

    @GetMapping("/sesion/{eventoEspecificoId}")
    public ResponseEntity<List<AsistenciaDTO>> findByEventoEspecifico(
            @PathVariable Long eventoEspecificoId
    ) {
        List<AsistenciaDTO> list = asistenciaMapper.toDTOs(
                asistenciaService.findByEventoEspecifico(eventoEspecificoId)
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<AsistenciaDTO>> findByPersona(
            @PathVariable Long personaId
    ) {
        List<AsistenciaDTO> list = asistenciaMapper.toDTOs(
                asistenciaService.findByPersona(personaId)
        );
        return ResponseEntity.ok(list);
    }

    // ========== LÍDER: GENERAR QR ==========

    /**
     * ✅ LÍDER: Generar código QR para una sesión
     *
     * GET /asistencias/generar-qr/{eventoEspecificoId}/lider/{liderId}
     *
     * Response: {
     *   "qrImageBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
     *   "qrData": {
     *     "eventoEspecificoId": 5,
     *     "eventoNombre": "SAV 2025-I",
     *     "sesionNombre": "SAV Lunes 5 enero",
     *     "fecha": "2025-01-05",
     *     "horaInicio": "07:00:00",
     *     "horaFin": "08:30:00",
     *     "toleranciaMinutos": 10,
     *     "lugar": "Auditorio Principal",
     *     "timestamp": 1704438000000
     *   },
     *   "mensaje": "QR generado exitosamente"
     * }
     *
     * El frontend puede mostrar qrImageBase64 directamente en un <img>
     */
    @GetMapping("/generar-qr/{eventoEspecificoId}/lider/{liderId}")
    public ResponseEntity<AsistenciaServiceImp.QRResponseDTO> generarQR(
            @PathVariable Long eventoEspecificoId,
            @PathVariable Long liderId
    ) {
        AsistenciaServiceImp.QRResponseDTO response =
                asistenciaService.generarQRParaSesion(eventoEspecificoId, liderId);
        return ResponseEntity.ok(response);
    }

    // ========== INTEGRANTE: ESCANEAR QR ==========

    /**
     * ✅ INTEGRANTE: Registrar asistencia escaneando QR
     *
     * VALIDACIONES:
     * - Solo se puede registrar el día de la sesión
     * - Solo 30 min antes hasta 2 horas después
     * - No registros duplicados
     * - Debe pertenecer al evento
     *
     * POST /asistencias/registrar-qr
     * Body: {
     *   "eventoEspecificoId": 5,
     *   "personaId": 10,
     *   "latitud": -15.5,
     *   "longitud": -70.2,
     *   "observacion": "Registro por QR"
     * }
     */
    @PostMapping("/registrar-qr")
    public ResponseEntity<?> registrarAsistenciaPorQR(
            @RequestBody AsistenciaRegistroDTO dto
    ) {
        try {
            Asistencia obj = asistenciaService.registrarAsistencia(dto);
            AsistenciaDTO response = asistenciaMapper.toDTO(obj);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {
            // Retornar error descriptivo
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ========== LÍDER: LISTA Y MARCADO MANUAL ==========

    /**
     * ✅ LÍDER: Obtener lista de participantes con estado de asistencia
     *
     * GET /asistencias/lista-llamado/{eventoEspecificoId}/lider/{liderId}
     */
    @GetMapping("/lista-llamado/{eventoEspecificoId}/lider/{liderId}")
    public ResponseEntity<List<ParticipanteAsistenciaDTO>> obtenerListaLlamado(
            @PathVariable Long eventoEspecificoId,
            @PathVariable Long liderId
    ) {
        List<ParticipanteAsistenciaDTO> lista =
                asistenciaService.obtenerListaParaLlamado(eventoEspecificoId, liderId);
        return ResponseEntity.ok(lista);
    }

    /**
     * ✅ LÍDER: Marcar asistencia manualmente
     *
     * POST /asistencias/marcar-manual
     * Body: {
     *   "eventoEspecificoId": 5,
     *   "personaId": 10,
     *   "liderId": 2,
     *   "estado": "PRESENTE",
     *   "observacion": "Llegó a tiempo"
     * }
     */
    @PostMapping("/marcar-manual")
    public ResponseEntity<?> marcarAsistenciaManual(
            @RequestBody MarcarAsistenciaRequest request
    ) {
        try {
            Asistencia obj = asistenciaService.marcarAsistenciaPorLider(
                    request.eventoEspecificoId(),
                    request.personaId(),
                    request.liderId(),
                    Asistencia.EstadoAsistencia.valueOf(request.estado().toUpperCase()),
                    request.observacion()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(asistenciaMapper.toDTO(obj));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ========== REPORTES ==========

    @GetMapping("/reporte/{eventoGeneralId}")
    public ResponseEntity<List<ReporteAsistenciaDTO>> generarReporte(
            @PathVariable Long eventoGeneralId
    ) {
        List<ReporteAsistenciaDTO> reporte =
                asistenciaService.generarReporteAsistencia(eventoGeneralId);
        return ResponseEntity.ok(reporte);
    }

    // ========== CRUD ==========

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

    // ========== DTOs ==========

    public record MarcarAsistenciaRequest(
            Long eventoEspecificoId,
            Long personaId,
            Long liderId,
            String estado,
            String observacion
    ) {}

    public record ErrorResponse(String mensaje) {}
}