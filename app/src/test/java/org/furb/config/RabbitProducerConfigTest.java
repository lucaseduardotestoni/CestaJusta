package org.furb.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RabbitProducerConfigTest {

    private final RabbitProducerConfig config = new RabbitProducerConfig();

    @Test
    void jsonMessageConverter_naoEhNulo() {
        assertThat(config.jsonMessageConverter()).isNotNull();
    }

    @Test
    void rabbitTemplate_usaOConversorJsonInjetado() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        MessageConverter converter = config.jsonMessageConverter();

        RabbitTemplate template = config.rabbitTemplate(connectionFactory, converter);

        assertThat(template.getMessageConverter()).isSameAs(converter);
    }
}