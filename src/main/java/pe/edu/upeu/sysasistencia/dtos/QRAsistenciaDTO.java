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
public class QRAsistenciaDTO {
    private Long eventoEspecificoId;
    private String eventoNombre;
    private String sesionNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer toleranciaMinutos;
    private String lugar;
    private Long timestamp; // Para validar vigencia del QR
}