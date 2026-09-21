package cl.duoc.vidasalud.appointments.dto;

import cl.duoc.vidasalud.appointments.entity.EstadoAtencion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AtencionDtos {

    public record CrearAtencionRequest(
            @NotBlank String pacienteId,
            @NotBlank String paciente,
            @NotBlank String prestacion,
            @NotNull Long boxId,
            @NotNull LocalDateTime fecha
    ) {}

    public record CambiarEstadoRequest(
            @NotNull EstadoAtencion status
    ) {}

    public record AtencionResponse(
            Long id,
            String pacienteId,
            String paciente,
            String prestacion,
            Long boxId,
            EstadoAtencion estado,
            LocalDateTime fecha
    ) {}
}
