package pe.edu.upeu.sysasistencia.servicio;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.sysasistencia.dtos.ImportFilterDTO;
import pe.edu.upeu.sysasistencia.dtos.ImportResultDTO;
import pe.edu.upeu.sysasistencia.modelo.Matricula;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;

import java.util.List;

public interface IMatriculaService extends ICrudGenericoService<Matricula, Long>{
    List<Matricula> findByCodigoEstudiante(String codigo);

    // ✅ ACTUALIZADO: Con periodoId
    List<Matricula> findByFiltros(
            Long sedeId,
            Long facultadId,
            Long programaId,
            Long periodoId,
            TipoPersona tipoPersona
    );

    ImportResultDTO importarDesdeExcel(MultipartFile file, ImportFilterDTO filtros) throws Exception;

    // ✅ ACTUALIZADO: Con periodoId
    byte[] exportarMatriculasAExcel(
            Long sedeId,
            Long facultadId,
            Long programaId,
            Long periodoId,
            TipoPersona tipoPersona
    ) throws Exception;

    byte[] descargarPlantilla() throws Exception;
}