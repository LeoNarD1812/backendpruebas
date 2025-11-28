package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.dtos.GrupoPequenoDTO;
import pe.edu.upeu.sysasistencia.dtos.LiderDisponibleDTO;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import java.util.List;

public interface IGrupoPequenoService extends ICrudGenericoService<GrupoPequeno, Long> {
    List<GrupoPequeno> findByGrupoGeneral(Long grupoGeneralId);
    List<GrupoPequeno> findByLider(Long liderId);
    List<GrupoPequenoDTO> findDtosByLider(Long liderId); // Nuevo método
    List<ParticipanteDisponibleDTO> getParticipantesDisponibles(Long eventoGeneralId, String ciclo);
    List<LiderDisponibleDTO> getLideresDisponibles(Long eventoGeneralId, Long excludeGrupoId);
}
