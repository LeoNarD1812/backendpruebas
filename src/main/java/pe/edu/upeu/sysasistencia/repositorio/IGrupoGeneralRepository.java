package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.GrupoGeneral;
import java.util.List;

public interface IGrupoGeneralRepository extends ICrudGenericoRepository<GrupoGeneral, Long> {

    List<GrupoGeneral> findByEventoGeneralIdEventoGeneral(Long eventoGeneralId);

    @Query("SELECT COUNT(gp) FROM GrupoPequeno gp WHERE gp.grupoGeneral.idGrupoGeneral = :grupoId")
    Integer countGruposPequenos(@Param("grupoId") Long grupoId);

    @Query("SELECT COUNT(p) FROM GrupoParticipante p " +
            "JOIN p.grupoPequeno gp " +
            "WHERE gp.grupoGeneral.idGrupoGeneral = :grupoId")
    Integer countParticipantes(@Param("grupoId") Long grupoId);
}