package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.Mapper;
import pe.edu.upeu.sysasistencia.dtos.PeriodoDTO;
import pe.edu.upeu.sysasistencia.modelo.Periodo;

@Mapper(componentModel = "spring")
public interface PeriodoMapper extends GenericMapper<PeriodoDTO, Periodo> {
}