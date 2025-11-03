package pe.edu.upeu.sysasistencia.servicio;

import pe.edu.upeu.sysasistencia.modelo.Acceso;
import pe.edu.upeu.sysasistencia.dtos.MenuGroup;
import java.util.List;

public interface IAccesoService {
    List<Acceso> getAccesoByUser(String username);
    List<MenuGroup> getMenuByUser(String username);
}
