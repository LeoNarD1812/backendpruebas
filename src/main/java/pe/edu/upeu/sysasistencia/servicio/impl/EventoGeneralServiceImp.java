package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IEventoGeneralRepository;
import pe.edu.upeu.sysasistencia.servicio.IEventoGeneralService;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoGeneralServiceImp extends CrudGenericoServiceImp<EventoGeneral, Long>
        implements IEventoGeneralService {

    private final IEventoGeneralRepository repo;

    @Override
    protected ICrudGenericoRepository<EventoGeneral, Long> getRepo() {
        return repo;
    }

    @Override
    public List<EventoGeneral> findByPrograma(Long programaId) {
        return repo.findByProgramaIdPrograma(programaId);
    }

    @Override
    public List<EventoGeneral> findEventosActivos(LocalDate fecha) {
        return repo.findEventosActivos(fecha);
    }

    // ✅ ACTUALIZADO: Busca por periodoId en lugar de cicloAcademico
    @Override
    public List<EventoGeneral> findByPeriodoYPrograma(Long periodoId, Long programaId) {
        return repo.findByPeriodoIdPeriodoAndProgramaIdPrograma(periodoId, programaId);
    }

    // ✅ NUEVO
    @Override
    public List<EventoGeneral> findByPeriodo(Long periodoId) {
        return repo.findByPeriodoIdPeriodo(periodoId);
    }
}