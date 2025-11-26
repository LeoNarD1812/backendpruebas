package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiderDisponibleDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String codigo;
    private String correo;
    private String celular;
}
