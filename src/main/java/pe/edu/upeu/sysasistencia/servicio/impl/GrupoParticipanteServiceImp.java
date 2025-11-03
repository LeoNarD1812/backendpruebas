package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysasistencia.modelo.GrupoParticipante;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoParticipanteRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoPequenoRepository;
import pe.edu.upeu.sysasistencia.servicio.IGrupoParticipanteService;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrupoParticipanteServiceImp extends CrudGenericoServiceImp<GrupoParticipante, Long>
        implements IGrupoParticipanteService {

    private final IGrupoParticipanteRepository repo;
    private final IGrupoPequenoRepository grupoPequenoRepo;
    private final IPersonaService personaService;

    @Override
    protected ICrudGenericoRepository<GrupoParticipante, Long> getRepo() {
        return repo;
    }

    @Override
    public List<GrupoParticipante> findByGrupoPequeno(Long grupoPequenoId) {
        return repo.findByGrupoPequenoIdGrupoPequeno(grupoPequenoId);
    }

    @Override
    public List<GrupoParticipante> findByPersona(Long personaId) {
        return repo.findByPersonaIdPersona(personaId);
    }

    @Override
    public GrupoParticipante agregarParticipante(Long grupoPequenoId, Long personaId) {
        // Validar que el grupo existe
        GrupoPequeno grupo = grupoPequenoRepo.findById(grupoPequenoId)
                .orElseThrow(() -> new ModelNotFoundException("Grupo pequeño no encontrado"));

        // Validar capacidad
        Integer participantesActuales = grupoPequenoRepo.countParticipantesActivos(grupoPequenoId);
        if (participantesActuales >= grupo.getCapacidadMaxima()) {
            throw new RuntimeException("El grupo ha alcanzado su capacidad máxima");
        }

        // Validar que la persona existe
        Persona persona = personaService.findById(personaId);

        // Validar que no esté ya inscrito
        var existente = repo.findByGrupoPequenoIdGrupoPequenoAndPersonaIdPersona(
                grupoPequenoId, personaId
        );
        if (existente.isPresent()) {
            throw new RuntimeException("La persona ya está inscrita en este grupo");
        }

        // Crear participante
        GrupoParticipante participante = GrupoParticipante.builder()
                .grupoPequeno(grupo)
                .persona(persona)
                .estado(GrupoParticipante.EstadoParticipante.ACTIVO)
                .build();

        log.info("Participante {} agregado al grupo {}", personaId, grupoPequenoId);
        return repo.save(participante);
    }

    @Override
    public void removerParticipante(Long grupoParticipanteId) {
        GrupoParticipante participante = findById(grupoParticipanteId);
        participante.setEstado(GrupoParticipante.EstadoParticipante.INACTIVO);
        repo.save(participante);
        log.info("Participante {} removido del grupo", grupoParticipanteId);
    }
}