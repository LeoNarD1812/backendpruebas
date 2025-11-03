package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.AsistenciaDTO;
import pe.edu.upeu.sysasistencia.modelo.Asistencia;

@Mapper(componentModel = "spring")
public interface AsistenciaMapper extends GenericMapper<AsistenciaDTO, Asistencia> {

    @Mapping(source = "eventoEspecifico.idEventoEspecifico", target = "eventoEspecificoId")
    @Mapping(source = "eventoEspecifico.nombreSesion", target = "eventoNombre")
    @Mapping(source = "persona.idPersona", target = "personaId")
    @Mapping(source = "persona.nombreCompleto", target = "personaNombre")
    @Mapping(source = "persona.codigoEstudiante", target = "personaCodigo")
    AsistenciaDTO toDTO(Asistencia entity);

    @Mapping(source = "eventoEspecificoId", target = "eventoEspecifico.idEventoEspecifico")
    @Mapping(source = "personaId", target = "persona.idPersona")
    @Mapping(target = "eventoEspecifico.eventoGeneral", ignore = true)
    @Mapping(target = "eventoEspecifico.nombreSesion", ignore = true)
    @Mapping(target = "eventoEspecifico.fecha", ignore = true)
    @Mapping(target = "eventoEspecifico.horaInicio", ignore = true)
    @Mapping(target = "eventoEspecifico.horaFin", ignore = true)
    @Mapping(target = "eventoEspecifico.lugar", ignore = true)
    @Mapping(target = "eventoEspecifico.descripcion", ignore = true)
    @Mapping(target = "eventoEspecifico.toleranciaMinutos", ignore = true)
    @Mapping(target = "eventoEspecifico.estado", ignore = true)
    @Mapping(target = "eventoEspecifico.createdAt", ignore = true)
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
    Asistencia toEntity(AsistenciaDTO dto);
}