package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ParticipanteDisponibleDTO {
    private Long personaId;
    private String nombreCompleto;
    private String codigoEstudiante;
    private String documento;
    private String correo;
    private Boolean yaInscrito;
    private String grupoActual;
}