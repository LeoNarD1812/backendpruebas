package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReporteAsistenciaDTO {
    private Long personaId;
    private String nombreCompleto;
    private String codigoEstudiante;
    private Integer totalSesiones;
    private Integer asistenciasPresente;
    private Integer asistenciasTarde;
    private Integer asistenciasAusente;
    private Integer asistenciasJustificado;
    private Double porcentajeAsistencia;
}