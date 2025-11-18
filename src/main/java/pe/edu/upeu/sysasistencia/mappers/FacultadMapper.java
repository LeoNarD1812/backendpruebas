package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pe.edu.upeu.sysasistencia.dtos.FacultadDTO;
import pe.edu.upeu.sysasistencia.modelo.Facultad;
import pe.edu.upeu.sysasistencia.modelo.Sede;

@Mapper(componentModel = "spring")
public interface FacultadMapper extends GenericMapper<FacultadDTO, Facultad> {

    @Mappings({
            @Mapping(source = "sede.idSede", target = "idSede")
    })
    @Override
    FacultadDTO toDTO(Facultad entity);

    @Mappings({
            @Mapping(source = "idSede", target = "sede")
    })
    @Override
    Facultad toEntity(FacultadDTO dto);

    default Sede fromId(Long idSede) {
        if (idSede == null) {
            return null;
        }
        Sede sede = new Sede();
        sede.setIdSede(idSede);
        return sede;
    }
}
