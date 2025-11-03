package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.dtos.AsistenciaRegistroDTO;
import pe.edu.upeu.sysasistencia.dtos.ReporteAsistenciaDTO;
import pe.edu.upeu.sysasistencia.modelo.Asistencia;
import java.util.List;

public interface IAsistenciaService extends ICrudGenericoService<Asistencia, Long> {
    List<Asistencia> findByEventoEspecifico(Long eventoEspecificoId);
    List<Asistencia> findByPersona(Long personaId);
    Asistencia registrarAsistencia(AsistenciaRegistroDTO dto);
    List<ReporteAsistenciaDTO> generarReporteAsistencia(Long eventoGeneralId);
}