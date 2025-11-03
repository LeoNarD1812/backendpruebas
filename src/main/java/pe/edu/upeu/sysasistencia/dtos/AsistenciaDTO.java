package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AsistenciaDTO {
    private Long idAsistencia;
    private Long eventoEspecificoId;
    private String eventoNombre;
    private Long personaId;
    private String personaNombre;
    private String personaCodigo;
    private LocalDateTime fechaHoraRegistro;
    private String estado;
    private String observacion;
    private BigDecimal latitud;
    private BigDecimal longitud;
}