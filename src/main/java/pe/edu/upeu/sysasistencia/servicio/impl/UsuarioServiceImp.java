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
import pe.edu.upeu.sysasistencia.repositorio.*;
import pe.edu.upeu.sysasistencia.servicio.IRolService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioRolService;
import pe.edu.upeu.sysasistencia.servicio.IUsuarioService;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    private final IMatriculaRepository matriculaRepository; // Inyectado

    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return repo;
    }

    @Override
    public List<Usuario> findAll() {
        return repo.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con ID: " + id));
    }

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
        List<Usuario> usuarios = findByRol(rolNombre);
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
        // ... (código de registro sin cambios)
        return null; // Simplificado para el ejemplo
    }

    @Override
    public Optional<Usuario> findOneByUser(String user) {
        return repo.findOneByUser(user);
    }

    @Override
    public UsuarioDTO updateUserAndRole(Long id, UsuarioDTO dto) {
        // 1. LOG DE DIAGNÓSTICO: ¿Qué datos están llegando del Frontend?
        System.out.println("--- INICIO ACTUALIZACIÓN DE USUARIO ---");
        System.out.println("ID a editar: " + id);
        System.out.println("Datos llegando del DTO:");
        System.out.println(" - User: " + dto.getUser());
        System.out.println(" - Nombre Completo: " + dto.getNombreCompleto());
        System.out.println(" - Documento: " + dto.getDocumento());
        System.out.println(" - Rol: " + dto.getNombreRol());

        // 2. Buscar y actualizar Usuario (Credenciales)
        Usuario usuarioExistente = repo.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado con ID: " + id));

        // Solo actualizamos si el dato no es nulo/vacío
        if(dto.getUser() != null && !dto.getUser().isEmpty()) {
            usuarioExistente.setUser(dto.getUser());
        }

        if (dto.getEstado() != null) {
            usuarioExistente.setEstado(dto.getEstado());
        }

        if (dto.getClave() != null && !dto.getClave().isEmpty()) {
            usuarioExistente.setClave(passwordEncoder.encode(dto.getClave()));
        }
        Usuario updatedUsuario = repo.save(usuarioExistente);
        System.out.println("✅ Usuario (Login) actualizado correctamente.");

        // 3. Buscar y actualizar Persona (Datos personales)
        // Usamos Optional para ver si existe la relación
        Optional<Persona> personaOpt = personaRepository.findByUsuarioIdUsuario(id);

        if (personaOpt.isPresent()) {
            Persona personaExistente = personaOpt.get();
            System.out.println("✅ Persona encontrada: " + personaExistente.getIdPersona());

            // Actualización segura (Solo si el DTO trae datos)
            if(dto.getNombreCompleto() != null) personaExistente.setNombreCompleto(dto.getNombreCompleto());
            if(dto.getDocumento() != null) personaExistente.setDocumento(dto.getDocumento());
            if(dto.getCorreo() != null) personaExistente.setCorreo(dto.getCorreo());
            if (dto.getTipoPersona() != null) personaExistente.setTipoPersona(dto.getTipoPersona());

            personaRepository.save(personaExistente);
            System.out.println("✅ Datos personales guardados en BD.");
        } else {
            System.err.println("❌ ERROR CRÍTICO: No se encontró una Persona vinculada al Usuario ID " + id);
            System.err.println("El usuario se actualizó, pero su nombre/documento NO, porque la tabla 'persona' no tiene registro para este usuario.");
        }

        // 4. Actualizar Rol
        if (dto.getNombreRol() != null && !dto.getNombreRol().isEmpty()) {
            try {
                Rol nuevoRol = rolRepository.findByNombre(Rol.RolNombre.valueOf(dto.getNombreRol().toUpperCase()))
                        .orElseThrow(() -> new ModelNotFoundException("Rol no encontrado", HttpStatus.BAD_REQUEST));

                // Limpiamos roles anteriores y ponemos el nuevo
                List<UsuarioRol> rolesActuales = iurService.findByUsuarioId(id);
                rolesActuales.forEach(iurService::delete);

                iurService.save(UsuarioRol.builder()
                        .usuario(updatedUsuario)
                        .rol(nuevoRol)
                        .build());
                System.out.println("✅ Rol actualizado a: " + nuevoRol.getNombre());
            } catch (Exception e) {
                System.err.println("❌ Error al actualizar el rol: " + e.getMessage());
            }
        }

        return mapToUsuarioDTO(updatedUsuario);
    }

    @Override
    public CustomResponse delete(Long id) {
        // ... (código de eliminación sin cambios)
        return null; // Simplificado para el ejemplo
    }

    private Rol obtenerRolPorNombre(String rolNombre) {
        // ... (código sin cambios)
        return null; // Simplificado para el ejemplo
    }

    @Override
    public UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = userMapper.toDTO(usuario);

        // Rellenar información de Persona
        personaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).ifPresent(persona -> {
            dto.setPersonaId(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());
            dto.setCorreo(persona.getCorreo());
            dto.setTipoPersona(persona.getTipoPersona());
            dto.setCodigoEstudiante(persona.getCodigoEstudiante());

            // --- LÓGICA CORREGIDA (ANTI-ERRORES 500) ---
            List<Matricula> matriculas = matriculaRepository.findByPersonaIdPersona(persona.getIdPersona());

            if (matriculas != null && !matriculas.isEmpty()) {
                matriculas.stream()
                        // 1. IMPORTANTE: Filtramos para evitar NullPointerException si faltan datos
                        .filter(m -> m.getPeriodo() != null && m.getPeriodo().getFechaInicio() != null)
                        // 2. Ahora sí es seguro comparar
                        .max(Comparator.comparing(m -> m.getPeriodo().getFechaInicio()))
                        .ifPresent(matriculaMasReciente -> {
                            dto.setPeriodo(matriculaMasReciente.getPeriodo().getNombre());
                        });
            }
        });

        // Rellenar información de Rol
        iurService.findByUsuarioId(usuario.getIdUsuario()).stream().findFirst().ifPresent(usuarioRol -> {
            dto.setNombreRol(usuarioRol.getRol().getNombre().name());
        });

        return dto;
    }
}
