package pe.edu.upeu.sysasistencia.dtos;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class RecurrenceRequestDTO {

    @NotNull
    private Long idEventoGeneral;

    @NotNull
    private String nombreSesion;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicioRecurrencia;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFinRecurrencia;

    @NotNull
    private String horaInicio;

    @NotNull
    private String horaFin;

    @NotNull
    private Integer toleranciaMinutos; // Use Integer for non-primitive types in DTOs

    // List of day numbers (1=Monday, 7=Sunday)
    private List<Integer> diasSemana;

    private String lugar; // Si está vacío, usa el del evento general
    private String descripcion; // Si está vacío, usa la del evento general
}