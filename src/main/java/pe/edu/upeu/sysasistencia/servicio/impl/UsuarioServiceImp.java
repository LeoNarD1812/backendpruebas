package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysasistencia.mappers.UsuarioMapper;
import pe.edu.upeu.sysasistencia.modelo.*;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IPersonaRepository;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRepository;
import pe.edu.upeu.sysasistencia.servicio.IRolService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioRolService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioService;
import pe.edu.upeu.sysasistencia.modelo.Persona;

import java.util.Optional;
import java.util.List;

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


    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return repo;
    }

    @Override
    public UsuarioDTO login(UsuarioDTO.CredencialesDto credentialsDto) {
        Usuario user = repo.findOneByUser(credentialsDto.user())
                .orElseThrow(() -> new ModelNotFoundException("Usuario desconocido", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(credentialsDto.clave(), user.getClave())) {
            return userMapper.toDTO(user);
        }

        throw new ModelNotFoundException("Contraseña inválida", HttpStatus.BAD_REQUEST);
    }
    @Override
    public List<Usuario> findByRol(String rolNombre) {
        try {
            // Convierte el nombre de rol (ej: "LIDER") a mayúsculas para asegurar la coincidencia con el ENUM/BD
            Rol.RolNombre rolEnum = Rol.RolNombre.valueOf(rolNombre.toUpperCase());
            // Llama al método del repositorio con el nombre del ENUM
            return repo.findByRol(rolEnum.name());
        } catch (IllegalArgumentException e) {
            log.error("Rol no válido: {}", rolNombre);
            return List.of(); // Devuelve lista vacía si el rol no existe
        }
    }
    @Override
    public List<Persona> getLideresDisponibles(Long excludeGrupoId) {
        // Llama al nuevo método del repositorio de personas
        return personaRepository.findLideresDisponibles(excludeGrupoId);
    }

    /**
     * ✅ REGISTRO MEJORADO: Crea Usuario + Persona automáticamente
     *
     * Campos del frontend:
     * - usuario (user)
     * - nombreCompleto
     * - correo
     * - documento
     * - clave (contraseña)
     *
     * Flujo:
     * 1. Valida que el usuario no exista
     * 2. Valida que el correo no esté duplicado
     * 3. Valida que el documento no esté duplicado
     * 4. Crea el usuario con la contraseña proporcionada (encriptada)
     * 5. Asigna rol por defecto (INTEGRANTE) si no se especifica
     * 6. Crea la persona asociada con los datos básicos
     */
    @Override
    public UsuarioDTO register(UsuarioDTO.UsuarioCrearDto userDto) {
        // 1. Validar que el usuario no exista
        Optional<Usuario> optionalUser = repo.findOneByUser(userDto.user());
        if (optionalUser.isPresent()) {
            throw new ModelNotFoundException("El usuario '" + userDto.user() + "' ya existe", HttpStatus.BAD_REQUEST);
        }

        // 2. Validar que el correo no esté duplicado
        Optional<Persona> personaConCorreo = personaRepository.findAll().stream()
                .filter(p -> p.getCorreo() != null && p.getCorreo().equalsIgnoreCase(userDto.correo()))
                .findFirst();
        if (personaConCorreo.isPresent()) {
            throw new ModelNotFoundException("El correo '" + userDto.correo() + "' ya está registrado", HttpStatus.BAD_REQUEST);
        }

        // 3. Validar que el documento no esté duplicado
        Optional<Persona> personaExistente = personaRepository.findByDocumento(userDto.documento());
        if (personaExistente.isPresent()) {
            throw new ModelNotFoundException("El documento '" + userDto.documento() + "' ya está registrado", HttpStatus.BAD_REQUEST);
        }

        // 4. Crear usuario con la contraseña proporcionada (encriptada)
        Usuario user = Usuario.builder()
                .user(userDto.user())
                .clave(passwordEncoder.encode(userDto.clave())) // ✅ Contraseña del frontend (String)
                .estado(userDto.estado() != null ? userDto.estado() : "ACTIVO") // Por defecto ACTIVO
                .build();

        Usuario savedUser = repo.save(user);
        log.info("✅ Usuario creado: {}", savedUser.getUser());

        // 5. Asignar rol (por defecto INTEGRANTE)
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

        // 6. Crear persona con datos del registro
        Persona persona = Persona.builder()
                .nombreCompleto(userDto.nombreCompleto())
                .documento(userDto.documento())
                .correo(userDto.correo()) // ✅ Correo del frontend
                .tipoPersona(TipoPersona.INVITADO) // Por defecto INVITADO
                .usuario(savedUser)
                // Resto de campos NULL (se completarán después)
                .build();

        personaRepository.save(persona);
        log.info("✅ Persona creada: {} - Correo: {} - Documento: {}",
                persona.getNombreCompleto(), persona.getCorreo(), persona.getDocumento());

        return userMapper.toDTO(savedUser);
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
}