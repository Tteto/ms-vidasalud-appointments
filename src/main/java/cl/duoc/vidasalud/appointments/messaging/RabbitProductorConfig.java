package cl.duoc.vidasalud.appointments.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de mensajería del productor. El exchange se declara aquí
 * para que exista aunque el servicio de notificaciones aún no haya partido.
 */
@Configuration
public class RabbitProductorConfig {

    @Bean
    public TopicExchange exchangeAtenciones(@Value("${app.rabbit.exchange-atenciones}") String nombre) {
        return ExchangeBuilder.topicExchange(nombre).durable(true).build();
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        return new Jackson2JsonMessageConverter(mapper);
    }
}
