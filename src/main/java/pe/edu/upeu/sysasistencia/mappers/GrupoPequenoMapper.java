package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.GrupoPequenoDTO;
import pe.edu.upeu.sysasistencia.modelo.GrupoPequeno;

@Mapper(componentModel = "spring")
public interface GrupoPequenoMapper extends GenericMapper<GrupoPequenoDTO, GrupoPequeno> {

    @Mapping(source = "grupoGeneral.idGrupoGeneral", target = "grupoGeneralId")
    @Mapping(source = "grupoGeneral.nombre", target = "grupoGeneralNombre")
    @Mapping(source = "grupoGeneral.eventoGeneral.idEventoGeneral", target = "eventoGeneralId")
    @Mapping(source = "lider.idPersona", target = "liderId")
    @Mapping(source = "lider.nombreCompleto", target = "liderNombre")
    @Mapping(source = "lider.codigoEstudiante", target = "liderCodigo")
    @Mapping(target = "participantesActuales", ignore = true)
    GrupoPequenoDTO toDTO(GrupoPequeno entity);

    @Mapping(source = "grupoGeneralId", target = "grupoGeneral.idGrupoGeneral")
    @Mapping(source = "liderId", target = "lider.idPersona")
    @Mapping(target = "grupoGeneral.eventoGeneral", ignore = true)
    @Mapping(target = "grupoGeneral.nombre", ignore = true)
    @Mapping(target = "grupoGeneral.descripcion", ignore = true)
    @Mapping(target = "grupoGeneral.createdAt", ignore = true)
    @Mapping(target = "lider.codigoEstudiante", ignore = true)
    @Mapping(target = "lider.nombreCompleto", ignore = true)
    @Mapping(target = "lider.documento", ignore = true)
    @Mapping(target = "lider.correo", ignore = true)
    @Mapping(target = "lider.correoInstitucional", ignore = true)
    @Mapping(target = "lider.celular", ignore = true)
    @Mapping(target = "lider.pais", ignore = true)
    @Mapping(target = "lider.foto", ignore = true)
    @Mapping(target = "lider.religion", ignore = true)
    @Mapping(target = "lider.fechaNacimiento", ignore = true)
    @Mapping(target = "lider.tipoPersona", ignore = true)
    @Mapping(target = "lider.usuario", ignore = true)
    GrupoPequeno toEntity(GrupoPequenoDTO dto);
}