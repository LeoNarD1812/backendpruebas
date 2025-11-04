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
public class QRScanResponseDTO {
    private Boolean exito;
    private String mensaje;
    private String estado; // PRESENTE, TARDE, ERROR
    private LocalDateTime horaRegistro;
    private String observacion;
}