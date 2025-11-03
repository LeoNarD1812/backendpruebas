package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.GrupoParticipante;
import java.util.List;
import java.util.Optional;

public interface IGrupoParticipanteRepository extends ICrudGenericoRepository<GrupoParticipante, Long> {

    List<GrupoParticipante> findByGrupoPequenoIdGrupoPequeno(Long grupoPequenoId);

    List<GrupoParticipante> findByPersonaIdPersona(Long personaId);

    Optional<GrupoParticipante> findByGrupoPequenoIdGrupoPequenoAndPersonaIdPersona(
            Long grupoPequenoId, Long personaId
    );

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM GrupoParticipante p " +
            "WHERE p.persona.idPersona = :personaId " +
            "AND p.grupoPequeno.grupoGeneral.eventoGeneral.idEventoGeneral = :eventoId " +
            "AND p.estado = 'ACTIVO'")
    boolean existeEnEvento(@Param("personaId") Long personaId, @Param("eventoId") Long eventoId);

    @Query("SELECT p FROM GrupoParticipante p " +
            "WHERE p.grupoPequeno.grupoGeneral.idGrupoGeneral = :grupoGeneralId " +
            "AND p.estado = 'ACTIVO'")
    List<GrupoParticipante> findByGrupoGeneral(@Param("grupoGeneralId") Long grupoGeneralId);
}