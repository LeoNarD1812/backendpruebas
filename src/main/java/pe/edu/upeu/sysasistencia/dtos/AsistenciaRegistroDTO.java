package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AsistenciaRegistroDTO {
    private Long eventoEspecificoId;
    private Long personaId;
    private String observacion;
    private BigDecimal latitud;
    private BigDecimal longitud;
}