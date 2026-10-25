package cl.duoc.vidasalud.appointments.messaging;

import cl.duoc.vidasalud.appointments.entity.Atencion;

import java.time.LocalDateTime;

/** Mensaje que se publica en RabbitMQ cada vez que una atención se crea o cambia de estado. */
public record EventoAtencion(
        Long id,
        String pacienteId,
        String paciente,
        String prestacion,
        Long boxId,
        String estado,
        LocalDateTime fecha,
        LocalDateTime ocurridoEn
) {
    public static EventoAtencion desde(Atencion a) {
        return new EventoAtencion(a.getId(), a.getPacienteId(), a.getPaciente(), a.getPrestacion(),
                a.getBoxId(), a.getEstado().name(), a.getFecha(), LocalDateTime.now());
    }
}
