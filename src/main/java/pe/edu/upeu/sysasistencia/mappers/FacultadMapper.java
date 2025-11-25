package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.sysasistencia.dtos.FacultadDTO;
import pe.edu.upeu.sysasistencia.modelo.Facultad;

@Mapper(componentModel = "spring")
public interface FacultadMapper extends GenericMapper<FacultadDTO, Facultad> {

    @Override
    @Mapping(source = "sede.idSede", target = "idSede")
    FacultadDTO toDTO(Facultad entity);

    @Override
    @Mapping(target = "sede", ignore = true)
    Facultad toEntity(FacultadDTO dto);
}
