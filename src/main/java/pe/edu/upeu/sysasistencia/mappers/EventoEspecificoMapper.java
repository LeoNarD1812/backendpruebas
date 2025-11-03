package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.EventoEspecificoDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoEspecifico;

@Mapper(componentModel = "spring")
public interface EventoEspecificoMapper extends GenericMapper<EventoEspecificoDTO, EventoEspecifico> {

    @Mapping(source = "eventoGeneral.idEventoGeneral", target = "eventoGeneralId")
    @Mapping(source = "eventoGeneral.nombre", target = "eventoGeneralNombre")
    EventoEspecificoDTO toDTO(EventoEspecifico entity);

    @Mapping(source = "eventoGeneralId", target = "eventoGeneral.idEventoGeneral")
    @Mapping(target = "eventoGeneral.nombre", ignore = true)
    @Mapping(target = "eventoGeneral.descripcion", ignore = true)
    @Mapping(target = "eventoGeneral.lugar", ignore = true)
    @Mapping(target = "eventoGeneral.fechaInicio", ignore = true)
    @Mapping(target = "eventoGeneral.fechaFin", ignore = true)
    @Mapping(target = "eventoGeneral.cicloAcademico", ignore = true)
    @Mapping(target = "eventoGeneral.programa", ignore = true)
    @Mapping(target = "eventoGeneral.estado", ignore = true)
    @Mapping(target = "eventoGeneral.createdAt", ignore = true)
    @Mapping(target = "eventoGeneral.updatedAt", ignore = true)
    EventoEspecifico toEntity(EventoEspecificoDTO dto);
}