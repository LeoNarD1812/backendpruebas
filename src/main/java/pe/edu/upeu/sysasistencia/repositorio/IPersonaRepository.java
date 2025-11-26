package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import java.util.Optional;
import java.util.List;

public interface IPersonaRepository extends ICrudGenericoRepository<Persona, Long>{
    Optional<Persona> findByCodigoEstudiante(String codigoEstudiante);
    Optional<Persona> findByDocumento(String documento);
    Optional<Persona> findByCorreo(String correo);
    Optional<Persona> findByUsuarioIdUsuario(Long idUsuario);
    List<Persona> findByTipoPersona(TipoPersona tipoPersona); // Método añadido

    @Query(value = """
        SELECT p.* FROM upeu_persona p
        JOIN upeu_usuario u ON p.usuario_id = u.id_usuario
        JOIN upeu_usuario_rol ur ON u.id_usuario = ur.usuario_id
        JOIN upeu_roles r ON ur.rol_id = r.id_rol
        WHERE r.nombre = 'LIDER'
        AND p.id_persona NOT IN (
            SELECT gp.lider_id FROM upeu_grupo_pequeno gp
            WHERE (:excludeGrupoId IS NULL OR gp.id_grupo_pequeno != :excludeGrupoId)
        )
    """, nativeQuery = true)
    List<Persona> findLideresDisponibles(@Param("excludeGrupoId") Long excludeGrupoId);

    @Query(value = """
        SELECT p.* FROM upeu_persona p
        JOIN upeu_matricula m ON p.id_persona = m.persona_id
        JOIN upeu_programa_estudio pe ON m.programa_id = pe.id_programa
        JOIN upeu_facultad f ON pe.facultad_id = f.id_facultad
        JOIN upeu_usuario u ON p.usuario_id = u.id_usuario
        JOIN upeu_usuario_rol ur ON u.id_usuario = ur.usuario_id
        JOIN upeu_roles r ON ur.rol_id = r.id_rol
        WHERE r.nombre = 'LIDER'
        AND f.id_facultad = :facultadId
        AND pe.id_programa = :programaId
        AND p.id_persona NOT IN (
            SELECT gp.lider_id FROM upeu_grupo_pequeno gp
            WHERE (:excludeGrupoId IS NULL OR gp.id_grupo_pequeno != :excludeGrupoId)
        )
    """, nativeQuery = true)
    List<Persona> findLideresByFacultadAndPrograma(
            @Param("facultadId") Long facultadId,
            @Param("programaId") Long programaId,
            @Param("excludeGrupoId") Long excludeGrupoId
    );
}
