package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ParticipanteAsistenciaDTO {
    private Long personaId;
    private String nombreCompleto;
    private String codigoEstudiante;
    private String documento;
    private String grupoPequenoNombre;
    private Boolean tieneAsistencia;
    private String estadoAsistencia; // PRESENTE, TARDE, AUSENTE, JUSTIFICADO, PENDIENTE
    private LocalDateTime horaRegistro;
    private String observacion;
}