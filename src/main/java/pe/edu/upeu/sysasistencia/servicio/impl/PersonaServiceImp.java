package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IPersonaRepository;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;

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
    public Persona update(Long id, Persona personaActualizada) {
        Persona personaExistente = repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Persona no encontrada con ID: " + id));

        // Actualizar campos comunes
        personaExistente.setNombreCompleto(personaActualizada.getNombreCompleto());
        personaExistente.setDocumento(personaActualizada.getDocumento());
        personaExistente.setCorreo(personaActualizada.getCorreo());
        personaExistente.setCelular(personaActualizada.getCelular());
        personaExistente.setPais(personaActualizada.getPais());
        personaExistente.setFoto(personaActualizada.getFoto());
        personaExistente.setReligion(personaActualizada.getReligion());
        personaExistente.setFechaNacimiento(personaActualizada.getFechaNacimiento());
        // El tipoPersona no debería cambiar por una actualización de perfil de usuario
        // personaExistente.setTipoPersona(personaActualizada.getTipoPersona());

        // Lógica específica para INVITADO: no puede modificar correoInstitucional ni codigoEstudiante
        if (personaExistente.getTipoPersona() != TipoPersona.INVITADO) {
            personaExistente.setCodigoEstudiante(personaActualizada.getCodigoEstudiante());
            personaExistente.setCorreoInstitucional(personaActualizada.getCorreoInstitucional());
        } else {
            // Si es INVITADO, asegúrate de que estos campos no se actualicen si vienen en el DTO
            // Opcional: podrías loggear un warning si intentan actualizar estos campos
            // personaExistente.setCodigoEstudiante(personaExistente.getCodigoEstudiante()); // Mantener el valor existente
            // personaExistente.setCorreoInstitucional(personaExistente.getCorreoInstitucional()); // Mantener el valor existente
        }

        return repo.save(personaExistente);
    }
}