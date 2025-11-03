package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import java.util.List;

public interface IGrupoPequenoService extends ICrudGenericoService<GrupoPequeno, Long> {
    List<GrupoPequeno> findByGrupoGeneral(Long grupoGeneralId);
    List<GrupoPequeno> findByLider(Long liderId);
    List<ParticipanteDisponibleDTO> getParticipantesDisponibles(Long eventoGeneralId);
}