package cl.duoc.vidasalud.appointments.messaging;

import cl.duoc.vidasalud.appointments.entity.Atencion;

/**
 * La lógica de negocio solo conoce esta interfaz; no sabe nada de RabbitMQ.
 */
public interface PublicadorEventos {
    void publicar(Atencion atencion);
}
