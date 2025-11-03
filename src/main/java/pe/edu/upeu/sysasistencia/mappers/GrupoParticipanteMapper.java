package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.GrupoParticipanteDTO;
import pe.edu.upeu.sysasistencia.modelo.GrupoParticipante;

@Mapper(componentModel = "spring")
public interface GrupoParticipanteMapper extends GenericMapper<GrupoParticipanteDTO, GrupoParticipante> {

    @Mapping(source = "grupoPequeno.idGrupoPequeno", target = "grupoPequenoId")
    @Mapping(source = "grupoPequeno.nombre", target = "grupoPequenoNombre")
    @Mapping(source = "persona.idPersona", target = "personaId")
    @Mapping(source = "persona.nombreCompleto", target = "personaNombre")
    @Mapping(source = "persona.codigoEstudiante", target = "personaCodigo")
    @Mapping(source = "persona.documento", target = "personaDocumento")
    GrupoParticipanteDTO toDTO(GrupoParticipante entity);

    @Mapping(source = "grupoPequenoId", target = "grupoPequeno.idGrupoPequeno")
    @Mapping(source = "personaId", target = "persona.idPersona")
    @Mapping(target = "grupoPequeno.grupoGeneral", ignore = true)
    @Mapping(target = "grupoPequeno.nombre", ignore = true)
    @Mapping(target = "grupoPequeno.lider", ignore = true)
    @Mapping(target = "grupoPequeno.capacidadMaxima", ignore = true)
    @Mapping(target = "grupoPequeno.descripcion", ignore = true)
    @Mapping(target = "grupoPequeno.createdAt", ignore = true)
    @Mapping(target = "persona.codigoEstudiante", ignore = true)
    @Mapping(target = "persona.nombreCompleto", ignore = true)
    @Mapping(target = "persona.documento", ignore = true)
    @Mapping(target = "persona.correo", ignore = true)
    @Mapping(target = "persona.correoInstitucional", ignore = true)
    @Mapping(target = "persona.celular", ignore = true)
    @Mapping(target = "persona.pais", ignore = true)
    @Mapping(target = "persona.foto", ignore = true)
    @Mapping(target = "persona.religion", ignore = true)
    @Mapping(target = "persona.fechaNacimiento", ignore = true)
    @Mapping(target = "persona.tipoPersona", ignore = true)
    @Mapping(target = "persona.usuario", ignore = true)
    GrupoParticipante toEntity(GrupoParticipanteDTO dto);
}