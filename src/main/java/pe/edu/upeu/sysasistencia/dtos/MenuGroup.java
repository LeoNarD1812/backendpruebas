package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroup {
    private Long id;
    private String name;
    private String icon;
    private String path; // null si es grupo colapsable
    private boolean expandedByDefault;
    private List<MenuItem> items = new ArrayList<>();

    // Constructor para grupos sin items
    public MenuGroup(Long id, String name, String icon, String path, boolean expandedByDefault) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.path = path;
        this.expandedByDefault = expandedByDefault;
    }
}