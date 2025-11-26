package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IPersonaRepository;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonaServiceImp extends CrudGenericoServiceImp<Persona, Long> implements IPersonaService {
    private final IPersonaRepository repo;

    @Override
    protected ICrudGenericoRepository<Persona, Long> getRepo() {
        return repo;
    }

    @Override
    public Persona update(Long id, Persona persona) {
        Persona personaDB = repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Persona no encontrada con ID: " + id));

        personaDB.setNombreCompleto(persona.getNombreCompleto());
        personaDB.setDocumento(persona.getDocumento());
        personaDB.setCorreo(persona.getCorreo());
        personaDB.setCelular(persona.getCelular());
        personaDB.setTipoPersona(persona.getTipoPersona());
        personaDB.setCodigoEstudiante(persona.getCodigoEstudiante());

        return repo.save(personaDB);
    }

    @Override
    public Optional<Persona> findByCodigoEstudiante(String codigo) {
        return repo.findByCodigoEstudiante(codigo);
    }

    @Override
    public Optional<Persona> findByDocumento(String documento) {
        return repo.findByDocumento(documento);
    }

    @Override
    public Optional<Persona> findByUsuarioId(Long usuarioId) {
        return repo.findByUsuarioIdUsuario(usuarioId);
    }

    @Override
    public List<Persona> findLideresByFacultadAndPrograma(Long facultadId, Long programaId, Long excludeGrupoId) {
        return repo.findLideresByFacultadAndPrograma(facultadId, programaId, excludeGrupoId);
    }
}
