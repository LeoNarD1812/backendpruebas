package pe.edu.upeu.sysasistencia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona; // Importar TipoPersona

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDTO {
    private Long idUsuario;
    @NotNull
    private String user;
    private String clave; // Añadido para la actualización
    @NotNull
    private String estado;
    private String token;

    // --- CAMPOS AÑADIDOS ---
    private Long personaId;
    private String nombreCompleto;
    private String codigoEstudiante;
    private String documento;
    private String correo; // Añadido para la actualización
    private String nombreRol;
    private TipoPersona tipoPersona; // Añadido
    private String periodo; // Asumiremos que es el nombre del periodo actual

    public record CredencialesDto(
            @NotBlank(message = "El usuario es obligatorio") String user,
            @NotBlank(message = "La contraseña es obligatoria") String clave
    ) {}

    public record UsuarioCrearDto(
            @NotBlank(message = "El usuario es obligatorio") String user,
            @NotBlank(message = "El nombre completo es obligatorio") String nombreCompleto,
            @NotBlank(message = "El correo es obligatorio") @Email(message = "Debe ser un correo válido") String correo,
            @NotBlank(message = "El documento es obligatorio") String documento,
            @NotBlank(message = "La contraseña es obligatoria") String clave,
            String rol,
            String estado,
            TipoPersona tipoPersona // Añadido
    ) {}
}
