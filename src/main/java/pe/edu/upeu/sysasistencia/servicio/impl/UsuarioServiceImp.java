package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysasistencia.mappers.UsuarioMapper;
import pe.edu.upeu.sysasistencia.modelo.*;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IPersonaRepository;
import pe.edu.upeu.sysasistencia.repositorio.IRolRepository;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRepository;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRolRepository;
import pe.edu.upeu.sysasistencia.servicio.IRolService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioRolService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImp extends CrudGenericoServiceImp<Usuario, Long> implements IUsuarioService {
    private final IUsuarioRepository repo;
    private final IPersonaRepository personaRepository;
    private final IRolService rolService;
    private final IUsuarioRolService iurService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper userMapper;
    private final IRolRepository rolRepository;

    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return repo;
    }

    // Revertido a la firma original para implementar ICrudGenericoService
    @Override
    public List<Usuario> findAll() {
        return repo.findAll();
    }

    // Revertido a la firma original para implementar ICrudGenericoService
    @Override
    public Usuario findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // Implementación de los nuevos métodos que devuelven DTOs
    @Override
    public List<UsuarioDTO> findAllDTOs() {
        return repo.findAll().stream()
                .map(this::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO findDTOById(Long id) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToUsuarioDTO(usuario);
    }

    @Override
    public List<UsuarioDTO> findByRolDTOs(String rolNombre) {
        List<Usuario> usuarios = findByRol(rolNombre); // Usa el método existente que devuelve entidades
        return usuarios.stream()
                .map(this::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO login(UsuarioDTO.CredencialesDto credentialsDto) {
        Usuario user = repo.findOneByUser(credentialsDto.user())
                .orElseThrow(() -> new ModelNotFoundException("Usuario desconocido", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(credentialsDto.clave(), user.getClave())) {
            return mapToUsuarioDTO(user);
        }

        throw new ModelNotFoundException("Contraseña inválida", HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<Usuario> findByRol(String rolNombre) {
        try {
            Rol.RolNombre rolEnum = Rol.RolNombre.valueOf(rolNombre.toUpperCase());
            return repo.findByRol(rolEnum.name());
        } catch (IllegalArgumentException e) {
            log.error("Rol no válido: {}", rolNombre);
            return List.of();
        }
    }

    @Override
    public List<Persona> getLideresDisponibles(Long excludeGrupoId) {
        return personaRepository.findLideresDisponibles(excludeGrupoId);
    }

    @Override
    public UsuarioDTO register(UsuarioDTO.UsuarioCrearDto userDto) {
        Optional<Usuario> optionalUser = repo.findOneByUser(userDto.user());
        if (optionalUser.isPresent()) {
            throw new ModelNotFoundException("El usuario '" + userDto.user() + "' ya existe", HttpStatus.BAD_REQUEST);
        }
        Optional<Persona> personaConCorreo = personaRepository.findAll().stream()
                .filter(p -> p.getCorreo() != null && p.getCorreo().equalsIgnoreCase(userDto.correo()))
                .findFirst();
        if (personaConCorreo.isPresent()) {
            throw new ModelNotFoundException("El correo '" + userDto.correo() + "' ya está registrado", HttpStatus.BAD_REQUEST);
        }
        Optional<Persona> personaExistente = personaRepository.findByDocumento(userDto.documento());
        if (personaExistente.isPresent()) {
            throw new ModelNotFoundException("El documento '" + userDto.documento() + "' ya está registrado", HttpStatus.BAD_REQUEST);
        }
        Usuario user = Usuario.builder()
                .user(userDto.user())
                .clave(passwordEncoder.encode(userDto.clave()))
                .estado(userDto.estado() != null ? userDto.estado() : "ACTIVO")
                .build();
        Usuario savedUser = repo.save(user);
        log.info("✅ Usuario creado: {}", savedUser.getUser());
        String rolNombre = userDto.rol() != null ? userDto.rol() : "INTEGRANTE";
        Rol rol = obtenerRolPorNombre(rolNombre);
        if (rol == null) {
            throw new ModelNotFoundException("Rol no encontrado: " + rolNombre, HttpStatus.BAD_REQUEST);
        }
        iurService.save(UsuarioRol.builder()
                .usuario(savedUser)
                .rol(rol)
                .build());
        log.info("✅ Rol asignado: {}", rol.getNombre());
        Persona persona = Persona.builder()
                .nombreCompleto(userDto.nombreCompleto())
                .documento(userDto.documento())
                .correo(userDto.correo())
                .tipoPersona(userDto.tipoPersona() != null ? userDto.tipoPersona() : TipoPersona.INVITADO)
                .usuario(savedUser)
                .build();
        personaRepository.save(persona);
        log.info("✅ Persona creada: {} - Correo: {} - Documento: {}",
                persona.getNombreCompleto(), persona.getCorreo(), persona.getDocumento());
        return mapToUsuarioDTO(savedUser);
    }

    @Override
    public Optional<Usuario> findOneByUser(String user) {
        return repo.findOneByUser(user);
    }

    @Override
    public UsuarioDTO updateUserAndRole(Long id, UsuarioDTO dto) {
        Usuario usuarioExistente = repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con ID: " + id));

        usuarioExistente.setUser(dto.getUser());
        usuarioExistente.setEstado(dto.getEstado());
        if (dto.getClave() != null && !dto.getClave().isEmpty()) {
            usuarioExistente.setClave(passwordEncoder.encode(dto.getClave()));
        }
        Usuario updatedUsuario = repo.save(usuarioExistente);

        personaRepository.findByUsuarioIdUsuario(id).ifPresentOrElse(personaExistente -> {
            personaExistente.setNombreCompleto(dto.getNombreCompleto());
            personaExistente.setDocumento(dto.getDocumento());
            personaExistente.setCorreo(dto.getCorreo());
            if (dto.getTipoPersona() != null) {
                personaExistente.setTipoPersona(dto.getTipoPersona());
            }
            personaRepository.save(personaExistente);
        }, () -> log.warn("No se encontró Persona asociada al Usuario con ID: {}", id));

        if (dto.getNombreRol() != null && !dto.getNombreRol().isEmpty()) {
            Rol nuevoRol = rolRepository.findByNombre(Rol.RolNombre.valueOf(dto.getNombreRol().toUpperCase()))
                    .orElseThrow(() -> new ModelNotFoundException("Rol no encontrado: " + dto.getNombreRol(), HttpStatus.BAD_REQUEST));

            iurService.findByUsuarioId(id).forEach(iurService::delete);
            iurService.save(UsuarioRol.builder()
                    .usuario(updatedUsuario)
                    .rol(nuevoRol)
                    .build());
            log.info("✅ Rol '{}' asignado al usuario '{}'", nuevoRol.getNombre().name(), updatedUsuario.getUser());
        }

        return mapToUsuarioDTO(updatedUsuario);
    }

    @Override
    public CustomResponse delete(Long id) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con ID: " + id));

        personaRepository.findByUsuarioIdUsuario(id).ifPresent(persona -> {
            personaRepository.delete(persona);
            log.info("✅ Persona con ID {} eliminada para el usuario {}", persona.getIdPersona(), usuario.getUser());
        });

        iurService.findByUsuarioId(id).forEach(iurService::delete);
        log.info("✅ Roles eliminados para el usuario {}", usuario.getUser());

        repo.delete(usuario);
        log.info("✅ Usuario {} con ID {} eliminado", usuario.getUser(), id);

        return new CustomResponse(200, LocalDateTime.now(), "true", "Usuario eliminado correctamente");
    }

    private Rol obtenerRolPorNombre(String rolNombre) {
        return switch (rolNombre.toUpperCase()) {
            case "SUPERADMIN" -> rolService.getByNombre(Rol.RolNombre.SUPERADMIN).orElse(null);
            case "ADMIN" -> rolService.getByNombre(Rol.RolNombre.ADMIN).orElse(null);
            case "LIDER" -> rolService.getByNombre(Rol.RolNombre.LIDER).orElse(null);
            case "INTEGRANTE" -> rolService.getByNombre(Rol.RolNombre.INTEGRANTE).orElse(null);
            default -> null;
        };
    }

    // Método auxiliar para mapear Usuario a UsuarioDTO con información completa
    @Override // Ahora implementa el método de la interfaz
    public UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = userMapper.toDTO(usuario); // Mapeo básico de Usuario a UsuarioDTO

        // Rellenar información de Persona
        personaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).ifPresent(persona -> {
            dto.setPersonaId(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());
            dto.setCorreo(persona.getCorreo());
            dto.setTipoPersona(persona.getTipoPersona());
            dto.setCodigoEstudiante(persona.getCodigoEstudiante()); // Añadido
        });

        // Rellenar información de Rol
        iurService.findByUsuarioId(usuario.getIdUsuario()).stream().findFirst().ifPresent(usuarioRol -> {
            dto.setNombreRol(usuarioRol.getRol().getNombre().name());
        });

        // Puedes añadir aquí el periodo si es necesario
        dto.setPeriodo("2025-I");

        return dto;
    }
}
