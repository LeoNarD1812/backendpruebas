package pe.edu.upeu.sysasistencia.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "persona") // Excluir 'persona' del toString para evitar recursión
@EqualsAndHashCode(exclude = "persona") // Excluir 'persona' del equals/hashCode
@Entity
@Table(name = "upeu_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "user", nullable = false, unique = true, length = 100)
    private String user;

    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @OneToOne(mappedBy = "usuario")
    @JsonBackReference
    private Persona persona;
}
