package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.GrupoGeneralDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.GrupoGeneralMapper;
import pe.edu.upeu.sysasistencia.modelo.GrupoGeneral;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoGeneralRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoPequenoRepository;
import pe.edu.upeu.sysasistencia.servicio.IGrupoGeneralService;
import pe.edu.upeu.sysasistencia.servicio.IGrupoPequenoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrupoGeneralServiceImp extends CrudGenericoServiceImp<GrupoGeneral, Long>
        implements IGrupoGeneralService {

    private final IGrupoGeneralRepository repo;
    private final IGrupoPequenoRepository grupoPequenoRepo;
    private final GrupoGeneralMapper grupoGeneralMapper;
    private final IGrupoPequenoService grupoPequenoService;

    @Override
    protected ICrudGenericoRepository<GrupoGeneral, Long> getRepo() {
        return repo;
    }

    @Override
    public List<GrupoGeneral> findByEventoGeneral(Long eventoGeneralId) {
        return repo.findByEventoGeneralIdEventoGeneral(eventoGeneralId);
    }

    @Override
    public CustomResponse delete(Long id) {
        List<GrupoPequeno> gruposPequenos = grupoPequenoRepo.findByGrupoGeneralIdGrupoGeneral(id);
        if (!gruposPequenos.isEmpty()) {
            log.info("Eliminando {} grupos pequeños asociados al grupo general ID: {}", gruposPequenos.size(), id); // CORREGIDO
            gruposPequenos.forEach(gp -> grupoPequenoService.delete(gp.getIdGrupoPequeno()));
        }

        repo.deleteById(id);
        log.info("Grupo general con ID {} eliminado", id);

        return new CustomResponse(200, LocalDateTime.now(), "true", "Grupo general y sus dependencias eliminados correctamente");
    }

    @Override
    public List<GrupoGeneralDTO> findAllConEstadisticas() {
        return repo.findAll().stream().map(grupoGeneral -> {
            GrupoGeneralDTO dto = grupoGeneralMapper.toDTO(grupoGeneral);
            
            int cantidadGrupos = grupoPequenoRepo.countByGrupoGeneralIdGrupoGeneral(grupoGeneral.getIdGrupoGeneral());
            dto.setCantidadGruposPequenos(cantidadGrupos);

            int totalParticipantes = grupoPequenoRepo.findByGrupoGeneralIdGrupoGeneral(grupoGeneral.getIdGrupoGeneral())
                    .stream()
                    .mapToInt(gp -> grupoPequenoRepo.countParticipantesActivos(gp.getIdGrupoPequeno()))
                    .sum();
            dto.setTotalParticipantes(totalParticipantes);
            
            return dto;
        }).collect(Collectors.toList());
    }
}
