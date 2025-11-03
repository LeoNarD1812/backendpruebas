package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.modelo.GrupoParticipante;
import java.util.List;

public interface IGrupoParticipanteService extends ICrudGenericoService<GrupoParticipante, Long> {
    List<GrupoParticipante> findByGrupoPequeno(Long grupoPequenoId);
    List<GrupoParticipante> findByPersona(Long personaId);
    GrupoParticipante agregarParticipante(Long grupoPequenoId, Long personaId);
    void removerParticipante(Long grupoParticipanteId);
}