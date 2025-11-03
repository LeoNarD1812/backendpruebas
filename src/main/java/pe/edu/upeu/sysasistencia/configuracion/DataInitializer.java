package pe.edu.upeu.sysasistencia.configuracion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.modelo.*;
import pe.edu.upeu.sysasistencia.repositorio.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IRolRepository rolRepository;
    private final IAccesoRepository accesoRepository;
    private final IAccesoRolRepository accesoRolRepository;
    private final ISedeRepository sedeRepository;
    private final IFacultadRepository facultadRepository;
    private final IProgramaEstudioRepository programaEstudioRepository;
    private final IPeriodoRepository periodoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Iniciando carga de datos predeterminados...");

            // Crear datos en el orden correcto
            crearSedes();
            crearFacultades();
            crearProgramasEstudio();
            crearPeriodos();
            crearRoles();
            crearAccesos();
            crearAccesosEventos();
            asignarAccesosARoles();

            log.info("Carga de datos completada exitosamente");
        } catch (Exception e) {
            log.error("Error al inicializar datos", e);
        }
    }

    private void crearSedes() {
        try {
            String nombreSede = "Filial Juliaca";
            if (!sedeRepository.existsByNombre(nombreSede)) {
                Sede sede = Sede.builder()
                        .nombre(nombreSede)
                        .descripcion("Filial Juliaca")
                        .build();
                Sede sedeGuardada = sedeRepository.save(sede);
                log.info("Sede creada: {} con ID: {}", sedeGuardada.getNombre(), sedeGuardada.getIdSede());
            } else {
                log.debug("La sede '{}' ya existe", nombreSede);
            }
        } catch (Exception e) {
            log.error("Error al crear sede", e);
        }
    }

    private void crearFacultades() {
        try {
            String nombreFacultad = "Facultad de Ingeniería y Arquitectura";
            if (!facultadRepository.existsByNombre(nombreFacultad)) {
                Facultad facultad = Facultad.builder()
                        .nombre(nombreFacultad)
                        .descripcion("Facultad de Ingeniería y Arquitectura")
                        .build();
                Facultad facultadGuardada = facultadRepository.save(facultad);
                log.info("Facultad creada: {} con ID: {}", facultadGuardada.getNombre(), facultadGuardada.getIdFacultad());
            } else {
                log.debug("La facultad '{}' ya existe", nombreFacultad);
            }
        } catch (Exception e) {
            log.error("Error al crear facultad", e);
        }
    }

    private void crearProgramasEstudio() {
        try {
            String nombrePrograma = "EP Ingeniería de Sistemas";
            String nombreFacultad = "Facultad de Ingeniería y Arquitectura";

            if (!programaEstudioRepository.existsByNombre(nombrePrograma)) {
                // Obtener la facultad por nombre
                Facultad facultad = facultadRepository.findByNombre(nombreFacultad)
                        .orElseThrow(() -> new RuntimeException("Facultad no encontrada: " + nombreFacultad));

                ProgramaEstudio programa = ProgramaEstudio.builder()
                        .nombre(nombrePrograma)
                        .descripcion("EP Ingeniería de Sistemas")
                        .facultad(facultad)
                        .build();
                ProgramaEstudio programaGuardado = programaEstudioRepository.save(programa);
                log.info("Programa de estudio creado: {} con ID: {}", programaGuardado.getNombre(), programaGuardado.getIdPrograma());
            } else {
                log.debug("El programa de estudio '{}' ya existe", nombrePrograma);
            }
        } catch (Exception e) {
            log.error("Error al crear programa de estudio", e);
        }
    }

    private void crearPeriodos() {
        try {
            List<Periodo> periodosDefault = Arrays.asList(
                    Periodo.builder()
                            .nombre("2025-I")
                            .descripcion("Primer semestre 2025")
                            .fechaInicio(LocalDate.of(2025, 3, 1))
                            .fechaFin(LocalDate.of(2025, 7, 31))
                            .estado("ACTIVO")
                            .build(),
                    Periodo.builder()
                            .nombre("2025-II")
                            .descripcion("Segundo semestre 2025")
                            .fechaInicio(LocalDate.of(2025, 8, 1))
                            .fechaFin(LocalDate.of(2025, 12, 31))
                            .estado("INACTIVO")
                            .build(),
                    Periodo.builder()
                            .nombre("2024-II")
                            .descripcion("Segundo semestre 2024")
                            .fechaInicio(LocalDate.of(2024, 8, 1))
                            .fechaFin(LocalDate.of(2024, 12, 31))
                            .estado("FINALIZADO")
                            .build()
            );

            for (Periodo periodo : periodosDefault) {
                if (!periodoRepository.existsByNombre(periodo.getNombre())) {
                    Periodo guardado = periodoRepository.save(periodo);
                    log.info("Periodo creado: {} con ID: {}", guardado.getNombre(), guardado.getIdPeriodo());
                } else {
                    log.debug("El periodo '{}' ya existe", periodo.getNombre());
                }
            }
        } catch (Exception e) {
            log.error("Error al crear periodos", e);
        }
    }

    private void crearRoles() {
        Arrays.stream(Rol.RolNombre.values()).forEach(rolNombre -> {
            Optional<Rol> rolExistente = rolRepository.findByNombre(rolNombre);

            if (rolExistente.isEmpty()) {
                Rol nuevoRol = Rol.builder()
                        .nombre(rolNombre)
                        .descripcion(obtenerDescripcionRol(rolNombre))
                        .build();
                rolRepository.save(nuevoRol);
                log.info("Rol creado: {}", rolNombre);
            } else {
                log.debug("Rol ya existe: {}", rolNombre);
            }
        });
    }

    private String obtenerDescripcionRol(Rol.RolNombre rolNombre) {
        return switch (rolNombre) {
            case SUPERADMIN -> "Super Administrador - Acceso total al sistema";
            case ADMIN -> "Administrador - Gestión de matrículas, sedes, facultades y programas";
            case LIDER -> "Líder - Acceso a dashboard de líder";
            case INTEGRANTE -> "Integrante - Acceso a dashboard de integrante";
        };
    }

    private void crearAccesos() {
        List<Acceso> accesos = Arrays.asList(
                // Accesos SUPERADMIN
                crearAcceso("Usuarios", "/usuarios", "fa-users"),
                crearAcceso("Roles", "/roles", "fa-user-shield"),
                crearAcceso("Configuración", "/configuracion", "fa-cog"),

                // Accesos ADMIN
                crearAcceso("Matrículas", "/matriculas", "fa-clipboard-list"),
                crearAcceso("Importar Excel", "/matriculas/importar", "fa-file-excel"),               
                crearAcceso("Sedes", "/sedes", "fa-building"),
                crearAcceso("Facultades", "/facultades", "fa-university"),
                crearAcceso("Programas", "/programas", "fa-graduation-cap"),
                crearAcceso("Reportes", "/reportes", "fa-chart-bar"),
                crearAcceso("Dashboard Admin", "/dashboard/admin", "fa-tachometer-alt"),

                // Accesos LIDER
                crearAcceso("Dashboard Líder", "/dashboard/lider", "fa-chart-line"),

                // Accesos INTEGRANTE
                crearAcceso("Dashboard Integrante", "/dashboard/integrante", "fa-chart-pie")
        );

        accesos.forEach(acceso -> {
            if (!accesoRepository.existsByUrl(acceso.getUrl())) {
                accesoRepository.save(acceso);
                log.info("Acceso creado: {} - {}", acceso.getNombre(), acceso.getUrl());
            }
        });
    }
    private void crearAccesosEventos() {
        List<Acceso> accesosEventos = Arrays.asList(
                // Accesos para ADMIN y SUPERADMIN
                crearAcceso("Eventos Generales", "/eventos-generales", "fa-calendar-alt"),
                crearAcceso("Sesiones", "/eventos-especificos", "fa-clock"),
                crearAcceso("Grupos Generales", "/grupos-generales", "fa-users"),
                crearAcceso("Grupos Pequeños", "/grupos-pequenos", "fa-user-friends"),
                crearAcceso("Gestión Participantes", "/grupo-participantes", "fa-user-plus"),

                // Accesos para LIDER
                crearAcceso("Mis Grupos", "/grupos-pequenos/lider", "fa-user-tie"),
                crearAcceso("Registrar Asistencia", "/asistencias/registrar", "fa-check-square"),
                crearAcceso("Ver Asistencias", "/asistencias", "fa-list-alt"),

                // Accesos para todos
                crearAcceso("Mis Asistencias", "/asistencias/persona", "fa-user-check"),
                crearAcceso("Reportes Eventos", "/asistencias/reporte", "fa-chart-line")
        );

        accesosEventos.forEach(acceso -> {
            if (!accesoRepository.existsByUrl(acceso.getUrl())) {
                accesoRepository.save(acceso);
                log.info("Acceso de evento creado: {} - {}", acceso.getNombre(), acceso.getUrl());
            }
        });
    }

    private Acceso crearAcceso(String nombre, String url, String icono) {
        return Acceso.builder()
                .nombre(nombre)
                .url(url)
                .icono(icono)
                .build();
    }

    private void asignarAccesosARoles() {
        // SUPERADMIN: acceso a todo
        asignarTodosLosAccesos(Rol.RolNombre.SUPERADMIN);

        // ADMIN: accesos específicos
        asignarAccesosPorNombres(Rol.RolNombre.ADMIN, Arrays.asList(
                "Matrículas", "Importar Excel", "Sedes", "Facultades",
                "Programas", "Periodos", "Reportes", "Dashboard Admin",
                "Eventos Generales", "Sesiones", "Grupos Generales",
                "Grupos Pequeños", "Gestión Participantes", "Ver Asistencias",
                "Reportes Eventos"
        ));

        // LIDER: dashboard líder + gestión de sus grupos
        asignarAccesosPorNombres(Rol.RolNombre.LIDER, Arrays.asList(
                "Dashboard Líder",
                "Mis Grupos", "Registrar Asistencia", "Ver Asistencias",
                "Mis Asistencias"
        ));

        // INTEGRANTE: dashboard integrante + ver sus asistencias
        asignarAccesosPorNombres(Rol.RolNombre.INTEGRANTE, Arrays.asList(
                "Dashboard Integrante",
                "Mis Asistencias"
        ));
    }

    private void asignarTodosLosAccesos(Rol.RolNombre rolNombre) {
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));

        List<Acceso> todosLosAccesos = accesoRepository.findAll();

        todosLosAccesos.forEach(acceso -> {
            if (!accesoRolRepository.existsByRolAndAcceso(rol, acceso)) {
                AccesoRol accesoRol = AccesoRol.builder()
                        .rol(rol)
                        .acceso(acceso)
                        .build();
                accesoRolRepository.save(accesoRol);
                log.debug("Acceso '{}' asignado a rol '{}'", acceso.getNombre(), rolNombre);
            }
        });

        log.info("Todos los accesos asignados a: {}", rolNombre);
    }

    private void asignarAccesosPorNombres(Rol.RolNombre rolNombre, List<String> nombresAccesos) {
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));

        nombresAccesos.forEach(nombreAcceso -> {
            Acceso acceso = accesoRepository.findByNombre(nombreAcceso)
                    .orElse(null);

            if (acceso != null && !accesoRolRepository.existsByRolAndAcceso(rol, acceso)) {
                AccesoRol accesoRol = AccesoRol.builder()
                        .rol(rol)
                        .acceso(acceso)
                        .build();
                accesoRolRepository.save(accesoRol);
                log.debug("Acceso '{}' asignado a rol '{}'", nombreAcceso, rolNombre);
            }
        });

        log.info("Accesos asignados a {}: {}", rolNombre, nombresAccesos);
    }
}