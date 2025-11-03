package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.modelo.GrupoGeneral;
import java.util.List;

public interface IGrupoGeneralService extends ICrudGenericoService<GrupoGeneral, Long> {
    List<GrupoGeneral> findByEventoGeneral(Long eventoGeneralId);
}
