package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import pe.edu.upeu.sysasistencia.modelo.Matricula;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoPequenoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoParticipanteRepository;
import pe.edu.upeu.sysasistencia.repositorio.IMatriculaRepository;
import pe.edu.upeu.sysasistencia.servicio.IGrupoPequenoService;
import pe.edu.upeu.sysasistencia.servicio.IEventoGeneralService;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ParticipanteDisponibleDTO> getParticipantesDisponibles(Long eventoGeneralId) {
        log.info("🔍 Buscando participantes disponibles para evento: {}", eventoGeneralId);

        // 1. Obtener el evento para conocer programa y periodo
        EventoGeneral evento = eventoService.findById(eventoGeneralId);
        log.info("📋 Evento: {} - Programa: {} - Periodo: {}",
                evento.getNombre(),
                evento.getPrograma().getNombre(),
                evento.getPeriodo().getNombre());

        // 2. ✅ CORRECCIÓN: Obtener matriculados filtrando por programa Y periodo del evento
        List<Matricula> matriculas = matriculaRepo.findByFiltros(
                null,                                    // sedeId
                null,                                    // facultadId
                evento.getPrograma().getIdPrograma(),    // programaId - ✅ CRÍTICO
                evento.getPeriodo().getIdPeriodo(),      // periodoId - ✅ NUEVO Y CRÍTICO
                null                                     // tipoPersona
        );

        log.info("📊 Total matriculados en el programa {} del periodo {}: {}",
                evento.getPrograma().getNombre(),
                evento.getPeriodo().getNombre(),
                matriculas.size());

        // 3. Mapear a DTO y verificar inscripción
        List<ParticipanteDisponibleDTO> disponibles = matriculas.stream().map(m -> {
            ParticipanteDisponibleDTO dto = new ParticipanteDisponibleDTO();
            dto.setPersonaId(m.getPersona().getIdPersona());
            dto.setNombreCompleto(m.getPersona().getNombreCompleto());
            dto.setCodigoEstudiante(m.getPersona().getCodigoEstudiante());
            dto.setDocumento(m.getPersona().getDocumento());
            dto.setCorreo(m.getPersona().getCorreo());

            // Verificar si ya está inscrito en algún grupo del evento
            boolean inscrito = participanteRepo.existeEnEvento(
                    m.getPersona().getIdPersona(),
                    eventoGeneralId
            );
            dto.setYaInscrito(inscrito);

            if (inscrito) {
                log.debug("⚠️ {} ya está inscrito en el evento", m.getPersona().getNombreCompleto());
            }

            return dto;
        }).collect(Collectors.toList());

        long disponiblesCount = disponibles.stream().filter(d -> !d.getYaInscrito()).count();
        log.info("✅ Participantes disponibles: {} de {}", disponiblesCount, disponibles.size());

        return disponibles;
    }
}