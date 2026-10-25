package cl.duoc.vidasalud.appointments.messaging;

import cl.duoc.vidasalud.appointments.entity.Atencion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Productor: publica el evento en el exchange topic de atenciones.
 *
 * Si RabbitMQ no está disponible, se registra en el log pero la atención
 * se guarda igual: la mensajería no debe romper la lógica existente.
 */
@Component
public class RabbitPublicadorEventos implements PublicadorEventos {

    private static final Logger log = LoggerFactory.getLogger(RabbitPublicadorEventos.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String prefijoRoutingKey;

    public RabbitPublicadorEventos(RabbitTemplate rabbitTemplate,
            @Value("${app.rabbit.exchange-atenciones}") String exchange,
            @Value("${app.rabbit.prefijo-routing-key}") String prefijoRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.prefijoRoutingKey = prefijoRoutingKey;
    }

    @Override
    public void publicar(Atencion atencion) {
        String routingKey = prefijoRoutingKey + atencion.getEstado().name().toLowerCase();
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, EventoAtencion.desde(atencion));
            log.info("Evento publicado: {} -> atención {}", routingKey, atencion.getId());
        } catch (AmqpException e) {
            log.error("No se pudo publicar {} (atención {}): {}", routingKey, atencion.getId(), e.getMessage());
        }
    }
}
