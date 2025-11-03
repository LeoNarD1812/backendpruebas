package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GrupoGeneralDTO {
    private Long idGrupoGeneral;
    private Long eventoGeneralId;
    private String eventoGeneralNombre;
    private String nombre;
    private String descripcion;
    private Integer cantidadGruposPequenos;
    private Integer totalParticipantes;
}