package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GrupoParticipanteDTO {
    private Long idGrupoParticipante;
    private Long grupoPequenoId;
    private String grupoPequenoNombre;
    private Long personaId;
    private String personaNombre;
    private String personaCodigo;
    private String personaDocumento;
    private LocalDateTime fechaInscripcion;
    private String estado;
}