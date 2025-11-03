package pe.edu.upeu.sysasistencia.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.sysasistencia.modelo.Asistencia;
import java.util.List;
import java.util.Optional;

public interface IAsistenciaRepository extends ICrudGenericoRepository<Asistencia, Long> {

    List<Asistencia> findByEventoEspecificoIdEventoEspecifico(Long eventoEspecificoId);

    List<Asistencia> findByPersonaIdPersona(Long personaId);

    Optional<Asistencia> findByEventoEspecificoIdEventoEspecificoAndPersonaIdPersona(
            Long eventoEspecificoId, Long personaId
    );

    @Query("SELECT a FROM Asistencia a " +
            "WHERE a.eventoEspecifico.eventoGeneral.idEventoGeneral = :eventoId " +
            "AND a.persona.idPersona = :personaId")
    List<Asistencia> findByEventoGeneralAndPersona(
            @Param("eventoId") Long eventoId,
            @Param("personaId") Long personaId
    );

    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.persona.idPersona = :personaId " +
            "AND a.eventoEspecifico.eventoGeneral.idEventoGeneral = :eventoId " +
            "AND a.estado = :estado")
    Integer countByPersonaEventoAndEstado(
            @Param("personaId") Long personaId,
            @Param("eventoId") Long eventoId,
            @Param("estado") Asistencia.EstadoAsistencia estado
    );
}