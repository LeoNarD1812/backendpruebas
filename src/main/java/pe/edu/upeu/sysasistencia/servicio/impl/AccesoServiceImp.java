package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.sysasistencia.dtos.MenuGroup;
import pe.edu.upeu.sysasistencia.dtos.MenuItem;
import pe.edu.upeu.sysasistencia.modelo.Acceso;
import pe.edu.upeu.sysasistencia.repositorio.IAccesoRepository;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.servicio.IAccesoService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccesoServiceImp extends CrudGenericoServiceImp<Acceso, Long> implements IAccesoService {
    private final IAccesoRepository repo;

    @Override
    protected ICrudGenericoRepository<Acceso, Long> getRepo() {
        return repo;
    }

    @Override
    public List<Acceso> getAccesoByUser(String username) {
        return repo.getAccesoByUser(username);
    }

    // metodo para 0btener menu
    public List<MenuGroup> getMenuByUser(String username) {
        List<Acceso> accesos = getAccesoByUser(username);
        return estructurarMenu(accesos);
    }

    private List<MenuGroup> estructurarMenu(List<Acceso> accesos) {
        Map<String, MenuGroup> grupos = new LinkedHashMap<>();

        // Definir estructura de grupos
        grupos.put("dashboard", new MenuGroup(1L, "Dashboard", "fa-tachometer-alt", "/dashboard", true));
        grupos.put("administracion", new MenuGroup(2L, "Administración", "fa-cog", null, true));
        grupos.put("eventos", new MenuGroup(3L, "Eventos", "fa-calendar-alt", null, true));
        grupos.put("asistencia", new MenuGroup(4L, "Asistencia", "fa-user-check", null, false));

        // Asignar items a grupos según la URL y nombre
        for (Acceso acceso : accesos) {
            String url = acceso.getUrl();
            String nombre = acceso.getNombre();
            String icono = acceso.getIcono();

            // --- REEMPLAZA TU LÓGICA DE 'if/else if' CON ESTA ---

            if (url.contains("dashboard") || nombre.toLowerCase().contains("dashboard")) {
                addMenuItem(grupos.get("dashboard"), acceso, nombre, url, icono);

            } else if (url.contains("matriculas") || url.contains("sedes") ||
                    url.contains("facultades") || url.contains("programas") ||
                    url.contains("usuarios") || url.contains("roles") ||
                    url.contains("configuracion") || nombre.toLowerCase().contains("admin")) {
                addMenuItem(grupos.get("administracion"), acceso, nombre, url, icono);

                // REGLA 1: Capturar el reporte de asistencia (por URL exacta)
            } else if (url.equals("/asistencias/reporte")) {
                addMenuItem(grupos.get("asistencia"), acceso, "Reporte Asistencia", url, icono);

                // REGLA 2: Capturar el resto de enlaces de "Asistencia"
            } else if (url.contains("asistencias") || url.contains("asistencia") ||
                    nombre.toLowerCase().contains("asistencia")) {
                addMenuItem(grupos.get("asistencia"), acceso, nombre, url, icono);

                // REGLA 3: Capturar el reporte general (por URL exacta)
            } else if (url.equals("/reportes")) {
                addMenuItem(grupos.get("eventos"), acceso, "Reporte Eventos", url, icono);

                // REGLA 4: Capturar el resto de enlaces de "Eventos"
            } else if (url.contains("eventos") || url.contains("grupos") ||
                    url.contains("sesiones") || nombre.toLowerCase().contains("evento")) {
                addMenuItem(grupos.get("eventos"), acceso, nombre, url, icono);

            } else {
                // Item suelto - crear grupo individual
                String groupKey = "item_" + acceso.getIdAcceso();
                grupos.put(groupKey, new MenuGroup(
                        acceso.getIdAcceso(),
                        nombre,
                        icono,
                        url,
                        false
                ));
            }
            // --- FIN DEL REEMPLAZO ---
        }

        // Filtrar grupos vacíos y ordenar
        return grupos.values().stream()
                .filter(grupo -> grupo.getPath() != null || !grupo.getItems().isEmpty())
                .sorted(Comparator.comparing(MenuGroup::getId))
                .collect(Collectors.toList());
    }

    // --- CAMBIO AQUÍ: 'nombre' por 'label' ---
    private void addMenuItem(MenuGroup grupo, Acceso acceso, String label, String url, String icono) {
        if (grupo.getPath() == null) { // Solo agregar items si es grupo colapsable
            grupo.getItems().add(new MenuItem(
                    acceso.getIdAcceso(),
                    label, // <-- Usar el 'label' modificado
                    url,
                    icono
            ));
        }
    }

}