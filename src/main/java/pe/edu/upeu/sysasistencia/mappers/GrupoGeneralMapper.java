package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.GrupoGeneralDTO;
import pe.edu.upeu.sysasistencia.modelo.GrupoGeneral;

@Mapper(componentModel = "spring")
public interface GrupoGeneralMapper extends GenericMapper<GrupoGeneralDTO, GrupoGeneral> {

    @Mapping(source = "eventoGeneral.idEventoGeneral", target = "eventoGeneralId")
    @Mapping(source = "eventoGeneral.nombre", target = "eventoGeneralNombre")
    @Mapping(target = "cantidadGruposPequenos", ignore = true)
    @Mapping(target = "totalParticipantes", ignore = true)
    GrupoGeneralDTO toDTO(GrupoGeneral entity);

    @Mapping(source = "eventoGeneralId", target = "eventoGeneral.idEventoGeneral")
    @Mapping(target = "eventoGeneral.nombre", ignore = true)
    @Mapping(target = "eventoGeneral.descripcion", ignore = true)
    @Mapping(target = "eventoGeneral.lugar", ignore = true)
    @Mapping(target = "eventoGeneral.fechaInicio", ignore = true)
    @Mapping(target = "eventoGeneral.fechaFin", ignore = true)
    @Mapping(target = "eventoGeneral.periodo", ignore = true)
    @Mapping(target = "eventoGeneral.programa", ignore = true)
    @Mapping(target = "eventoGeneral.estado", ignore = true)
    @Mapping(target = "eventoGeneral.createdAt", ignore = true)
    @Mapping(target = "eventoGeneral.updatedAt", ignore = true)
    GrupoGeneral toEntity(GrupoGeneralDTO dto);
}