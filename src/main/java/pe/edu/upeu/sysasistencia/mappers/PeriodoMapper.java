package pe.edu.upeu.sysasistencia.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pe.edu.upeu.sysasistencia.dtos.PeriodoDTO;
import pe.edu.upeu.sysasistencia.modelo.Periodo;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface PeriodoMapper extends GenericMapper<PeriodoDTO, Periodo> {

    @AfterMapping
    default void calcularEstadoDinamico(Periodo periodo, @MappingTarget PeriodoDTO dto) {
        LocalDate hoy = LocalDate.now();
        String estado;

        if (hoy.isBefore(periodo.getFechaInicio())) {
            estado = "Programado";
        } else if (hoy.isAfter(periodo.getFechaFin())) {
            estado = "Finalizado";
        } else {
            estado = "Activo";
        }
        dto.setEstado(estado);
    }
}
