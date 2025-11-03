package pe.edu.upeu.sysasistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventoEspecificoDTO {
    private Long idEventoEspecifico;
    private Long eventoGeneralId;
    private String eventoGeneralNombre;
    private String nombreSesion;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String lugar;
    private String descripcion;
    private Integer toleranciaMinutos;
    private String estado;
}