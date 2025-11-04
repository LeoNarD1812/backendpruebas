package pe.edu.upeu.sysasistencia.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDTO {
    private Long idUsuario;
    @NotNull
    private String user;
    @NotNull
    private String estado;
    private String token;

    private Long personaId;

    public record CredencialesDto(
            @NotBlank(message = "El usuario es obligatorio")
            String user,
            @NotBlank(message = "La contraseña es obligatoria")
            String clave
    ) { }

    /**
     * DTO para registro desde el frontend
     * Campos requeridos:
     * - usuario: username único
     * - nombreCompleto: nombre completo de la persona
     * - correo: correo electrónico
     * - documento: número de documento (DNI, pasaporte, etc.)
     * - clave: contraseña
     */
    public record UsuarioCrearDto(
            @NotBlank(message = "El usuario es obligatorio")
            String user,  // username único

            @NotBlank(message = "El nombre completo es obligatorio")
            String nombreCompleto,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "Debe ser un correo válido")
            String correo,

            @NotBlank(message = "El documento es obligatorio")
            String documento,

            @NotBlank(message = "La contraseña es obligatoria")
            String clave,

            // Campos opcionales con valores por defecto
            String rol,      // Por defecto: "INTEGRANTE"
            String estado    // Por defecto: "ACTIVO"
    ) { }
}