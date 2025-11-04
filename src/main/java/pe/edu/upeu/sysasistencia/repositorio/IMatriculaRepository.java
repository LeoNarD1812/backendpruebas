package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.Matricula;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;

import java.util.List;
import java.util.Optional;

public interface IMatriculaRepository extends ICrudGenericoRepository<Matricula, Long>{
    Optional<Matricula> findByPersonaIdPersona(Long idPersona);

    Optional<Matricula> findByPersonaIdPersonaAndPeriodoIdPeriodo(Long personaId, Long periodoId);

    @Query("SELECT m FROM Matricula m WHERE m.persona.codigoEstudiante = :codigo")
    List<Matricula> findByCodigoEstudiante(@Param("codigo") String codigo);

    // ✅ CONSULTA CORREGIDA
    // Esta consulta usa COALESCE para manejar correctamente los filtros nulos.
    // Si el filtro (ej: :sedeId) es null, lo reemplaza con el valor de la columna (m.sede.idSede),
    // haciendo que la comparación (m.sede.idSede = m.sede.idSede) siempre sea verdadera para ese filtro.
    @Query("SELECT m FROM Matricula m WHERE " +
            "m.sede.idSede = COALESCE(:sedeId, m.sede.idSede) AND " +
            "m.facultad.idFacultad = COALESCE(:facultadId, m.facultad.idFacultad) AND " +
            "m.programaEstudio.idPrograma = COALESCE(:programaId, m.programaEstudio.idPrograma) AND " +
            "m.periodo.idPeriodo = COALESCE(:periodoId, m.periodo.idPeriodo) AND " +
            "m.persona.tipoPersona = COALESCE(:tipoPersona, m.persona.tipoPersona)")
    List<Matricula> findByFiltros(
            @Param("sedeId") Long sedeId,
            @Param("facultadId") Long facultadId,
            @Param("programaId") Long programaId,
            @Param("periodoId") Long periodoId,
            @Param("tipoPersona") TipoPersona tipoPersona
    );
}