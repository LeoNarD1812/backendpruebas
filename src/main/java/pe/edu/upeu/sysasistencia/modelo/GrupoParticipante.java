package pe.edu.upeu.sysasistencia.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "upeu_grupo_participante",
        uniqueConstraints = @UniqueConstraint(columnNames = {"grupo_pequeno_id", "persona_id"}))
public class GrupoParticipante {

    public enum EstadoParticipante {
        ACTIVO, INACTIVO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo_participante")
    private Long idGrupoParticipante;

    @ManyToOne
    @JoinColumn(name = "grupo_pequeno_id", nullable = false)
    private GrupoPequeno grupoPequeno;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    @Builder.Default
    private EstadoParticipante estado = EstadoParticipante.ACTIVO;

    @PrePersist
    protected void onCreate() {
        fechaInscripcion = LocalDateTime.now();
    }
}