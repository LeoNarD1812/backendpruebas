package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.GrupoPequenoDTO;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.GrupoPequenoMapper;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import pe.edu.upeu.sysasistencia.modelo.Matricula;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoPequenoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoParticipanteRepository;
import pe.edu.upeu.sysasistencia.repositorio.IMatriculaRepository;
import pe.edu.upeu.sysasistencia.servicio.IGrupoPequenoService;
import pe.edu.upeu.sysasistencia.servicio.IEventoGeneralService;
import java.time.LocalDateTime;
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
    private final GrupoPequenoMapper grupoPequenoMapper;

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
    public List<ParticipanteDisponibleDTO> getParticipantesDisponibles(Long eventoGeneralId) {
        log.info("🔍 Buscando participantes disponibles para evento: {}", eventoGeneralId);

        EventoGeneral evento = eventoService.findById(eventoGeneralId);
        log.info("📋 Evento: {} - Programa: {} - Periodo: {}",
                evento.getNombre(),
                evento.getPrograma().getNombre(),
                evento.getPeriodo().getNombre());

        List<Matricula> matriculas = matriculaRepo.findByFiltros(
                null,
                null,
                evento.getPrograma().getIdPrograma(),
                evento.getPeriodo().getIdPeriodo(),
                null
        );

        log.info("📊 Total matriculados en el programa {} del periodo {}: {}",
                evento.getPrograma().getNombre(),
                evento.getPeriodo().getNombre(),
                matriculas.size());

        return matriculas.stream().map(m -> {
            ParticipanteDisponibleDTO dto = new ParticipanteDisponibleDTO();
            dto.setPersonaId(m.getPersona().getIdPersona());
            dto.setNombreCompleto(m.getPersona().getNombreCompleto());
            dto.setCodigoEstudiante(m.getPersona().getCodigoEstudiante());
            dto.setDocumento(m.getPersona().getDocumento());
            dto.setCorreo(m.getPersona().getCorreo());

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
    }
}
