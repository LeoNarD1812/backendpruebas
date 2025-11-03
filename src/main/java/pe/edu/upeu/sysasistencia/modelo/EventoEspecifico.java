package pe.edu.upeu.sysasistencia.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "upeu_evento_especifico")
public class EventoEspecifico {

    public enum EstadoSesion {
        PROGRAMADO, EN_CURSO, FINALIZADO, CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento_especifico")
    private Long idEventoEspecifico;

    @ManyToOne
    @JoinColumn(name = "evento_general_id", nullable = false)
    private EventoGeneral eventoGeneral;

    @Column(name = "nombre_sesion", nullable = false, length = 200)
    private String nombreSesion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "lugar", length = 200)
    private String lugar;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tolerancia_minutos")
    @Builder.Default
    private Integer toleranciaMinutos = 10;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    @Builder.Default
    private EstadoSesion estado = EstadoSesion.PROGRAMADO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}