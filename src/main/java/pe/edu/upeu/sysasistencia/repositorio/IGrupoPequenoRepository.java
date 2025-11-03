package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import java.util.List;

public interface IGrupoPequenoRepository extends ICrudGenericoRepository<GrupoPequeno, Long> {

    List<GrupoPequeno> findByGrupoGeneralIdGrupoGeneral(Long grupoGeneralId);

    List<GrupoPequeno> findByLiderIdPersona(Long liderId);

    @Query("SELECT COUNT(p) FROM GrupoParticipante p WHERE p.grupoPequeno.idGrupoPequeno = :grupoId AND p.estado = 'ACTIVO'")
    Integer countParticipantesActivos(@Param("grupoId") Long grupoId);

    @Query("SELECT gp FROM GrupoPequeno gp WHERE gp.grupoGeneral.eventoGeneral.idEventoGeneral = :eventoId")
    List<GrupoPequeno> findByEventoGeneral(@Param("eventoId") Long eventoId);
}