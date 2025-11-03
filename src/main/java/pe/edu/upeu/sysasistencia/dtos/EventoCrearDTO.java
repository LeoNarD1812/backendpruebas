package pe.edu.upeu.sysasistencia.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventoCrearDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private String lugar;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotBlank(message = "El ciclo académico es obligatorio")
    private String cicloAcademico;

    @NotNull(message = "El programa es obligatorio")
    private Long programaId;
}