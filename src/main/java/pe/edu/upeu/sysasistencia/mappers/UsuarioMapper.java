package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.modelo.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends GenericMapper<UsuarioDTO, Usuario> {

    @Mapping(target = "token", ignore = true)
    @Mapping(source = "persona.idPersona", target = "personaId")
    @Mapping(source = "persona.nombreCompleto", target = "nombreCompleto")
    @Mapping(source = "persona.documento", target = "documento")
    @Mapping(source = "persona.correo", target = "correo")
    @Mapping(source = "persona.tipoPersona", target = "tipoPersona")
    // @Mapping(source = "roles", target = "nombreRol", qualifiedByName = "rolesToNombreRol") // Revertido
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "clave", ignore = true)
    @Mapping(target = "persona", ignore = true)
    // @Mapping(target = "roles", ignore = true) // Revertido
    Usuario toEntity(UsuarioDTO dto);

    @Mapping(target = "clave", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "persona", ignore = true)
    // @Mapping(target = "roles", ignore = true) // Revertido
    Usuario toEntityFromCADTO(UsuarioDTO.UsuarioCrearDto usuarioCrearDto);

    // @Named("rolesToNombreRol") // Revertido
    // default String rolesToNombreRol(List<UsuarioRol> roles) { // Revertido
    //     if (roles == null || roles.isEmpty()) { // Revertido
    //         return null; // Revertido
    //     } // Revertido
    //     return roles.get(0).getRol().getNombre().name(); // Revertido
    // } // Revertido
}
