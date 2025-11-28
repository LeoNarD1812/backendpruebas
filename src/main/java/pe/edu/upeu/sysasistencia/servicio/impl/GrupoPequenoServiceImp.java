package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.GrupoPequenoDTO;
import pe.edu.upeu.sysasistencia.dtos.LiderDisponibleDTO;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.GrupoPequenoMapper;
import pe.edu.upeu.sysasistencia.modelo.*;
import pe.edu.upeu.sysasistencia.repositorio.*;
import pe.edu.upeu.sysasistencia.servicio.IEventoGeneralService;
import pe.edu.upeu.sysasistencia.servicio.IGrupoPequenoService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioRolService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrupoPequenoServiceImp extends CrudGenericoServiceImp<GrupoPequeno, Long>
        implements IGrupoPequenoService {

    private final IGrupoPequenoRepository repo;
    private final IMatriculaRepository matriculaRepo;
    private final IGrupoParticipanteRepository participanteRepo;
    private final IEventoGeneralService eventoService;
    private final GrupoPequenoMapper grupoPequenoMapper;
    private final IPersonaRepository personaRepo;
    private final IUsuarioRolService usuarioRolService; // Inyectado
    private final IEventoGeneralRepository eventoGeneralRepo;

    @Override
    protected ICrudGenericoRepository<GrupoPequeno, Long> getRepo() {
        return repo;
    }

    @Override
    public List<GrupoPequeno> findByGrupoGeneral(Long grupoGeneralId) {
        return repo.findByGrupoGeneralIdGrupoGeneral(grupoGeneralId);
    }

    @Override
    public List<GrupoPequeno> findByLider(Long liderId) {
        return repo.findByLiderIdPersona(liderId);
    }

    @Override
    public List<GrupoPequenoDTO> findDtosByLider(Long liderId) {
        List<GrupoPequeno> grupos = repo.findByLiderIdPersona(liderId);
        return grupos.stream().map(grupo -> {
            GrupoPequenoDTO dto = grupoPequenoMapper.toDTO(grupo);
            Integer participantes = repo.countParticipantesActivos(grupo.getIdGrupoPequeno());
            dto.setParticipantesActuales(participantes);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public CustomResponse delete(Long id) {
        List<pe.edu.upeu.sysasistencia.modelo.GrupoParticipante> participantes = participanteRepo.findByGrupoPequenoIdGrupoPequeno(id);
        if (!participantes.isEmpty()) {
            log.info("Eliminando {} participantes del grupo pequeño ID: {}", participantes.size(), id);
            participanteRepo.deleteAll(participantes);
        }
        repo.deleteById(id);
        log.info("Grupo pequeño con ID {} eliminado", id);
        return new CustomResponse(200, LocalDateTime.now(), "true", "Grupo pequeño y sus participantes eliminados correctamente");
    }

    @Override
    public List<ParticipanteDisponibleDTO> getParticipantesDisponibles(Long eventoGeneralId, String ciclo) {
        log.info("🔍 Buscando participantes disponibles para evento: {} y ciclo: {}", eventoGeneralId, ciclo);

        EventoGeneral evento = eventoService.findById(eventoGeneralId);
        log.info("📋 Evento: {} - Programa: {} - Periodo: {}",
                evento.getNombre(),
                evento.getPrograma().getNombre(),
                evento.getPeriodo().getNombre());

        // 1. Obtener estudiantes matriculados
        List<Persona> estudiantes = matriculaRepo.findByFiltros(
                null, null, evento.getPrograma().getIdPrograma(), evento.getPeriodo().getIdPeriodo(), null, ciclo
        ).stream().map(Matricula::getPersona).collect(Collectors.toList());
        log.info("📊 Total estudiantes matriculados: {}", estudiantes.size());

        // 2. Obtener invitados
        List<Persona> invitados = personaRepo.findByTipoPersona(TipoPersona.INVITADO);
        log.info("📊 Total invitados: {}", invitados.size());

        // 3. Combinar listas y eliminar duplicados
        List<Persona> todosLosPosibles = Stream.concat(estudiantes.stream(), invitados.stream())
                .distinct()
                .collect(Collectors.toList());
        log.info("📊 Total combinado (únicos): {}", todosLosPosibles.size());

        // 4. Filtrar por rol 'INTEGRANTE'
        List<Persona> soloIntegrantes = todosLosPosibles.stream()
                .filter(persona -> {
                    if (persona.getUsuario() == null) return false; // Si no tiene usuario, no puede tener rol
                    return usuarioRolService.findByUsuarioId(persona.getUsuario().getIdUsuario())
                            .stream()
                            .anyMatch(ur -> ur.getRol().getNombre() == Rol.RolNombre.INTEGRANTE);
                })
                .collect(Collectors.toList());
        log.info("✅ Total con rol INTEGRANTE: {}", soloIntegrantes.size());

        // 5. Mapear a DTO y verificar inscripción
        return soloIntegrantes.stream().map(p -> {
            ParticipanteDisponibleDTO dto = new ParticipanteDisponibleDTO();
            dto.setPersonaId(p.getIdPersona());
            dto.setNombreCompleto(p.getNombreCompleto());
            dto.setCodigoEstudiante(p.getCodigoEstudiante());
            dto.setDocumento(p.getDocumento());
            dto.setCorreo(p.getCorreo());

            boolean inscrito = participanteRepo.existeEnEvento(p.getIdPersona(), eventoGeneralId);
            dto.setYaInscrito(inscrito);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<LiderDisponibleDTO> getLideresDisponibles(Long eventoGeneralId, Long excludeGrupoId) {
        EventoGeneral evento = eventoGeneralRepo.findById(eventoGeneralId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        List<Persona> lideres = personaRepo.findLideresByFacultadAndPrograma(
                evento.getPrograma().getFacultad().getIdFacultad(),
                evento.getPrograma().getIdPrograma(),
                excludeGrupoId
        );

        return lideres.stream()
                .map(p -> new LiderDisponibleDTO(
                        p.getIdPersona(),
                        p.getNombreCompleto(),
                        p.getCodigoEstudiante(),
                        p.getCorreo(),
                        p.getCelular()
                ))
                .collect(Collectors.toList());
    }
}
