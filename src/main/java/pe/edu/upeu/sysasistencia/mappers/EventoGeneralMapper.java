package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pe.edu.upeu.sysasistencia.dtos.EventoGeneralDTO;
import pe.edu.upeu.sysasistencia.modelo.EventoGeneral;

@Mapper(componentModel = "spring")
public interface EventoGeneralMapper extends GenericMapper<EventoGeneralDTO, EventoGeneral> {

    @Mapping(source = "programa.idPrograma", target = "programaId")
    @Mapping(source = "programa.nombre", target = "programaNombre")
    @Mapping(source = "periodo.idPeriodo", target = "periodoId")
    @Mapping(source = "periodo.nombre", target = "periodoNombre")
    EventoGeneralDTO toDTO(EventoGeneral entity);

    @Mapping(source = "programaId", target = "programa.idPrograma")
    @Mapping(source = "periodoId", target = "periodo.idPeriodo")
    @Mapping(target = "programa.nombre", ignore = true)
    @Mapping(target = "programa.facultad", ignore = true)
    @Mapping(target = "programa.descripcion", ignore = true)
    @Mapping(target = "periodo.nombre", ignore = true)
    @Mapping(target = "periodo.descripcion", ignore = true)
    @Mapping(target = "periodo.fechaInicio", ignore = true)
    @Mapping(target = "periodo.fechaFin", ignore = true)
    @Mapping(target = "periodo.estado", ignore = true)
    EventoGeneral toEntity(EventoGeneralDTO dto);

    @Named("estadoToString")
    default String estadoToString(EventoGeneral.EstadoEvento estado) {
        return estado != null ? estado.name() : "ACTIVO";
    }

    @Named("stringToEstado")
    default EventoGeneral.EstadoEvento stringToEstado(String estado) {
        if (estado == null) return EventoGeneral.EstadoEvento.ACTIVO;
        try {
            return EventoGeneral.EstadoEvento.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EventoGeneral.EstadoEvento.ACTIVO;
        }
    }
}