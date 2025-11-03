package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventoGeneralDTO {
    private Long idEventoGeneral;
    private String nombre;
    private String descripcion;
    private String lugar;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long periodoId;
    private String periodoNombre;
    private Long programaId;
    private String programaNombre;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}