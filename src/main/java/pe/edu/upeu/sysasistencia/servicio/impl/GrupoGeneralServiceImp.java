package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.modelo.GrupoGeneral;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IGrupoGeneralRepository;
import pe.edu.upeu.sysasistencia.servicio.IGrupoGeneralService;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GrupoGeneralServiceImp extends CrudGenericoServiceImp<GrupoGeneral, Long>
        implements IGrupoGeneralService {

    private final IGrupoGeneralRepository repo;

    @Override
    protected ICrudGenericoRepository<GrupoGeneral, Long> getRepo() {
        return repo;
    }

    @Override
    public List<GrupoGeneral> findByEventoGeneral(Long eventoGeneralId) {
        return repo.findByEventoGeneralIdEventoGeneral(eventoGeneralId);
    }
}