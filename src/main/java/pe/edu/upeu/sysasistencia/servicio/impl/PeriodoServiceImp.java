package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.modelo.Periodo;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IPeriodoRepository;
import pe.edu.upeu.sysasistencia.servicio.IPeriodoService;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PeriodoServiceImp extends CrudGenericoServiceImp<Periodo, Long>
        implements IPeriodoService {

    private final IPeriodoRepository repo;

    @Override
    protected ICrudGenericoRepository<Periodo, Long> getRepo() {
        return repo;
    }

    @Override
    public Optional<Periodo> findByNombre(String nombre) {
        return repo.findByNombre(nombre);
    }

    @Override
    public List<Periodo> findByEstado(String estado) {
        return repo.findByEstado(estado);
    }

    @Override
    public Periodo getPeriodoActivo() {
        return repo.findFirstByEstadoOrderByFechaInicioDesc("ACTIVO")
                .orElseThrow(() -> new RuntimeException("No hay periodo activo"));
    }
}