package pe.edu.upeu.sysasistencia.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QRAsistenciaDTO {
    private Long eventoEspecificoId;
    private String eventoNombre;
    private String sesionNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer toleranciaMinutos;
    private String lugar;
    private Long timestamp;

    // NUEVO CAMPO: Contiene la fecha, hora y zona horaria en formato ISO 8601
    // Ejemplo: "2025-11-24T23:59:00-05:00"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime horaFinISO;
}
