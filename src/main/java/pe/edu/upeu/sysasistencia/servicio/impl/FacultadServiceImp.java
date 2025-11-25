package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.FacultadDTO;
import pe.edu.upeu.sysasistencia.mappers.FacultadMapper;
import pe.edu.upeu.sysasistencia.modelo.Facultad;
import pe.edu.upeu.sysasistencia.modelo.Sede;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IFacultadRepository;
import pe.edu.upeu.sysasistencia.repositorio.ISedeRepository;
import pe.edu.upeu.sysasistencia.servicio.IFacultadService;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FacultadServiceImp extends CrudGenericoServiceImp<Facultad, Long> implements IFacultadService {
    private final IFacultadRepository repo;
    private final ISedeRepository sedeRepository;
    private final FacultadMapper facultadMapper;

    @Override
    protected ICrudGenericoRepository<Facultad, Long> getRepo() {
        return repo;
    }

    @Override
    public Facultad create(FacultadDTO dto) {
        if (dto.getIdSede() == null) {
            throw new IllegalArgumentException("La sede es obligatoria");
        }
        Sede sede = sedeRepository.findById(dto.getIdSede())
                .orElseThrow(() -> new IllegalArgumentException("Sede no encontrada con ID: " + dto.getIdSede()));
        
        Facultad facultad = facultadMapper.toEntity(dto);
        facultad.setSede(sede);
        
        return repo.save(facultad);
    }

    @Override
    public Optional<Facultad> findByNombre(String nombre) {
        return repo.findByNombre(nombre);
    }
}
