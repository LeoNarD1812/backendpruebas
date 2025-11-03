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
        log.info("🔍 Iniciando proceso de agregar participante: Grupo={}, Persona={}",
                grupoPequenoId, personaId);

        // 1. Validar que el grupo existe
        GrupoPequeno grupo = grupoPequenoRepo.findById(grupoPequenoId)
                .orElseThrow(() -> {
                    log.error("❌ Grupo pequeño no encontrado: {}", grupoPequenoId);
                    return new ModelNotFoundException("Grupo pequeño no encontrado");
                });

        log.info("✅ Grupo encontrado: {} (Evento: {})",
                grupo.getNombre(),
                grupo.getGrupoGeneral().getEventoGeneral().getNombre());

        // 2. Validar que la persona existe
        Persona persona = personaService.findById(personaId);
        log.info("✅ Persona encontrada: {}", persona.getNombreCompleto());

        // 3. Obtener ID del evento general
        Long eventoGeneralId = grupo.getGrupoGeneral().getEventoGeneral().getIdEventoGeneral();
        log.info("📋 Evento General ID: {}", eventoGeneralId);

        // 4. ✅ VALIDACIÓN CORRECTA: Verificar si ya está inscrito en CUALQUIER grupo del evento
        boolean yaInscritoEnEvento = repo.existeEnEvento(personaId, eventoGeneralId);
        if (yaInscritoEnEvento) {
            log.error("❌ La persona {} ya está inscrita en otro grupo del evento {}",
                    personaId, eventoGeneralId);
            throw new RuntimeException("La persona ya está inscrita en un grupo de este evento");
        }
        log.info("✅ La persona NO está inscrita en ningún grupo del evento");

        // 5. Validar capacidad del grupo
        Integer participantesActuales = grupoPequenoRepo.countParticipantesActivos(grupoPequenoId);
        log.info("📊 Capacidad: {}/{}", participantesActuales, grupo.getCapacidadMaxima());

        if (participantesActuales >= grupo.getCapacidadMaxima()) {
            log.error("❌ El grupo ha alcanzado su capacidad máxima: {}/{}",
                    participantesActuales, grupo.getCapacidadMaxima());
            throw new RuntimeException("El grupo ha alcanzado su capacidad máxima");
        }
        log.info("✅ El grupo tiene espacio disponible");

        // 6. Crear participante
        GrupoParticipante participante = GrupoParticipante.builder()
                .grupoPequeno(grupo)
                .persona(persona)
                .estado(GrupoParticipante.EstadoParticipante.ACTIVO)
                .build();

        GrupoParticipante guardado = repo.save(participante);

        log.info("✅ Participante agregado exitosamente: {} al grupo {}",
                persona.getNombreCompleto(), grupo.getNombre());

        return guardado;
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