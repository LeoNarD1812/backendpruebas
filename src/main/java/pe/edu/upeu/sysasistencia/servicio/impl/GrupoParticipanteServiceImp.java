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
import java.time.LocalDateTime; // Importar LocalDateTime
import java.util.List;
import java.util.Optional;

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
        log.info("🔍 Iniciando proceso de agregar participante: Grupo={}, Persona={}",
                grupoPequenoId, personaId);

        // 1. Validar que el grupo existe
        GrupoPequeno grupo = grupoPequenoRepo.findById(grupoPequenoId)
                .orElseThrow(() -> new ModelNotFoundException("Grupo pequeño no encontrado"));

        // 2. Validar que la persona existe
        Persona persona = personaService.findById(personaId);

        // --- INICIO LÓGICA UPSERT ---
        Optional<GrupoParticipante> existente = repo.findByGrupoPequenoIdGrupoPequenoAndPersonaIdPersona(grupoPequenoId, personaId);

        if (existente.isPresent()) {
            GrupoParticipante participante = existente.get();
            if (participante.getEstado() == GrupoParticipante.EstadoParticipante.ACTIVO) {
                throw new RuntimeException("La persona ya está activa en este grupo.");
            }
            // Si está INACTIVO, lo reactivamos
            log.info("Reactivando participante inactivo...");
            participante.setEstado(GrupoParticipante.EstadoParticipante.ACTIVO);
            participante.setFechaInscripcion(LocalDateTime.now()); // CORREGIDO
            return repo.save(participante);
        }
        // --- FIN LÓGICA UPSERT ---

        // 3. Validar capacidad del grupo (solo si es un nuevo participante)
        Integer participantesActuales = grupoPequenoRepo.countParticipantesActivos(grupoPequenoId);
        if (participantesActuales >= grupo.getCapacidadMaxima()) {
            throw new RuntimeException("El grupo ha alcanzado su capacidad máxima");
        }

        // 4. Validar si ya está en otro grupo del mismo evento
        Long eventoGeneralId = grupo.getGrupoGeneral().getEventoGeneral().getIdEventoGeneral();
        boolean yaInscritoEnEvento = repo.existeEnEvento(personaId, eventoGeneralId);
        if (yaInscritoEnEvento) {
            throw new RuntimeException("La persona ya está inscrita en otro grupo de este evento");
        }

        // 5. Crear nuevo participante
        GrupoParticipante nuevoParticipante = GrupoParticipante.builder()
                .grupoPequeno(grupo)
                .persona(persona)
                .estado(GrupoParticipante.EstadoParticipante.ACTIVO)
                .build();

        return repo.save(nuevoParticipante);
    }

    @Override
    public void removerParticipante(Long grupoParticipanteId) {
        log.info("🗑️ Removiendo participante: {}", grupoParticipanteId);

        GrupoParticipante participante = findById(grupoParticipanteId);
        participante.setEstado(GrupoParticipante.EstadoParticipante.INACTIVO);
        repo.save(participante);

        log.info("✅ Participante removido del grupo: {}", participante.getGrupoPequeno().getNombre());
    }
}
