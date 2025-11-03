package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.ParticipanteDisponibleDTO;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import pe.edu.upeu.sysasistencia.modelo.Matricula;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;
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
        // Obtener el evento para filtrar por programa
        var evento = eventoService.findById(eventoGeneralId);

        // Obtener matriculados del programa del evento - CORREGIDO: 5 parámetros
        List<Matricula> matriculas = matriculaRepo.findByFiltros(
                null,                                   // sedeId
                null,                                   // facultadId
                evento.getPrograma().getIdPrograma(),   // programaId
                null,                                   // periodoId
                null                  // tipoPersona - ✅ NUEVO PARÁMETRO
        );

        return matriculas.stream().map(m -> {
            ParticipanteDisponibleDTO dto = new ParticipanteDisponibleDTO();
            dto.setPersonaId(m.getPersona().getIdPersona());
            dto.setNombreCompleto(m.getPersona().getNombreCompleto());
            dto.setCodigoEstudiante(m.getPersona().getCodigoEstudiante());
            dto.setDocumento(m.getPersona().getDocumento());
            dto.setCorreo(m.getPersona().getCorreo());

            // Verificar si ya está inscrito en algún grupo del evento
            boolean inscrito = participanteRepo.existeEnEvento(
                    m.getPersona().getIdPersona(), eventoGeneralId
            );
            dto.setYaInscrito(inscrito);

            return dto;
        }).collect(Collectors.toList());
    }
}