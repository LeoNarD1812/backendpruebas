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

    // Lógica principal para la WEB
    @Override
    public List<MenuGroup> getMenuByUser(String username) {
        List<Acceso> accesos = getAccesoByUser(username);
        return estructurarMenuParaWeb(accesos);
    }

    // Lógica separada para el MÓVIL
    public List<MenuGroup> getMenuMovilByUser(String username) {
        List<Acceso> accesos = getAccesoByUser(username);
        return estructurarMenuParaMovil(accesos);
    }

    // Método para la WEB (lógica compleja original)
    private List<MenuGroup> estructurarMenuParaWeb(List<Acceso> accesos) {
        Map<String, MenuGroup> grupos = new LinkedHashMap<>();
        grupos.put("dashboard", new MenuGroup(1L, "Dashboard", "fa-tachometer-alt", "/dashboard", true));
        grupos.put("administracion", new MenuGroup(2L, "Administración", "fa-cog", null, true));
        grupos.put("eventos", new MenuGroup(3L, "Eventos", "fa-calendar-alt", null, true));
        grupos.put("asistencia", new MenuGroup(4L, "Asistencia", "fa-user-check", null, false));
        grupos.put("miPerfil", new MenuGroup(5L, "Mi Perfil", "fa-user-circle", null, true));

        // Temporary list to hold event-related menu items for sorting
        List<MenuItem> eventosMenuItems = new ArrayList<>();

        for (Acceso acceso : accesos) {
            String url = acceso.getUrl();
            String nombre = acceso.getNombre();
            String icono = acceso.getIcono();

            if (url.contains("dashboard") || nombre.toLowerCase().contains("dashboard")) {
                addMenuItem(grupos.get("dashboard"), acceso, nombre, url, icono);
            } else if (url.equals("/personas/my-profile")) {
                addMenuItem(grupos.get("miPerfil"), acceso, "Editar Perfil", url, icono);
            } else if (url.contains("matriculas") || url.contains("sedes") || url.contains("facultades") || url.contains("programas") || url.contains("usuarios") || url.contains("users")|| url.contains("roles") || url.contains("configuracion") || url.contains("periodos") || nombre.toLowerCase().contains("admin")) {
                addMenuItem(grupos.get("administracion"), acceso, nombre, url, icono);
            } else if (url.equals("/asistencias/reporte")) {
                addMenuItem(grupos.get("asistencia"), acceso, "Reporte Asistencia", url, icono);
            } else if (url.contains("asistencias") || url.contains("asistencia") || nombre.toLowerCase().contains("asistencia")) {
                addMenuItem(grupos.get("asistencia"), acceso, nombre, url, icono);
            }
            // Collect all Eventos related items into a temporary list
            else if (url.equals("/reportes") || url.contains("eventos") || url.contains("grupos") || url.contains("sesiones") || nombre.toLowerCase().contains("evento") || url.contains("participantes")) {
                eventosMenuItems.add(new MenuItem(acceso.getIdAcceso(), nombre, url, icono));
            }
            else {
                // If not caught by any specific group, add as a top-level item (should be rare for submenu items)
                grupos.put("item_" + acceso.getIdAcceso(), new MenuGroup(acceso.getIdAcceso(), nombre, icono, url, false));
            }
        }

        // Define a custom order for event items
        Map<String, Integer> orderMap = new HashMap<>();
        orderMap.put("/eventos/generales", 1);
        orderMap.put("/eventos/especificos", 2);
        orderMap.put("/grupos/pequenos", 3);
        orderMap.put("/grupos/participantes", 4); // "Gestión de Participantes" after "Grupos Pequeños"
        orderMap.put("/reportes", 5); // "Reporte Eventos" last

        // Sort the event-related menu items
        eventosMenuItems.sort((item1, item2) -> {
            Integer order1 = orderMap.getOrDefault(item1.getPath(), 99);
            Integer order2 = orderMap.getOrDefault(item2.getPath(), 99);
            return order1.compareTo(order2);
        });

        // Add sorted event items to the "Eventos" group
        for (MenuItem item : eventosMenuItems) {
            grupos.get("eventos").getItems().add(item);
        }

        return grupos.values().stream()
                .filter(grupo -> grupo.getPath() != null || !grupo.getItems().isEmpty())
                .sorted(Comparator.comparing(MenuGroup::getId))
                .collect(Collectors.toList());
    }

    private void addMenuItem(MenuGroup grupo, Acceso acceso, String label, String url, String icono) {
        if (grupo.getPath() == null) { // Only add items if it's a collapsible group
            grupo.getItems().add(new MenuItem(
                    acceso.getIdAcceso(),
                    label,
                    url,
                    icono
            ));
        }
    }

    // Método para el MÓVIL (con el filtro corregido)
    private List<MenuGroup> estructurarMenuParaMovil(List<Acceso> accesos) {
        return accesos.stream()
                .filter(acceso -> acceso.getUrl() == null || !acceso.getUrl().trim().startsWith("/dashboard"))
                .map(acceso -> new MenuGroup(
                        acceso.getIdAcceso(),
                        acceso.getNombre(),
                        acceso.getIcono(),
                        acceso.getUrl(),
                        false,
                        List.of()
                ))
                .collect(Collectors.toList());
    }
}
