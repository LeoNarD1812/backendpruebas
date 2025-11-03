package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GrupoPequenoDTO {
    private Long idGrupoPequeno;
    private Long grupoGeneralId;
    private String grupoGeneralNombre;
    private String nombre;
    private Long liderId;
    private String liderNombre;
    private String liderCodigo;
    private Integer capacidadMaxima;
    private Integer participantesActuales;
    private String descripcion;
}