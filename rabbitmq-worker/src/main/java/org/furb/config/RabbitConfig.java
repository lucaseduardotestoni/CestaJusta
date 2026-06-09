package org.furb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.furb.messaging.contract.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(RoutingKeys.DLX, true, false);
    }

    @Bean
    public Queue filaFoto() {
        return QueueBuilder.durable(RoutingKeys.FILA_FOTO)
                .withArgument("x-dead-letter-exchange", RoutingKeys.DLX)
                .withArgument("x-dead-letter-routing-key", RoutingKeys.FILA_FOTO_DLQ)
                .build();
    }

    @Bean
    public Queue filaNotificacao() {
        return QueueBuilder.durable(RoutingKeys.FILA_NOTIFICACAO)
                .withArgument("x-dead-letter-exchange", RoutingKeys.DLX)
                .withArgument("x-dead-letter-routing-key", RoutingKeys.FILA_NOTIFICACAO_DLQ)
                .build();
    }

    @Bean
    public Queue filaFotoDlq() {
        return QueueBuilder.durable(RoutingKeys.FILA_FOTO_DLQ).build();
    }

    @Bean
    public Queue filaNotificacaoDlq() {
        return QueueBuilder.durable(RoutingKeys.FILA_NOTIFICACAO_DLQ).build();
    }

    @Bean
    public Binding bindFoto(Queue filaFoto, TopicExchange eventsExchange) {
        return BindingBuilder.bind(filaFoto).to(eventsExchange).with(RoutingKeys.FOTO_SOLICITADA);
    }

    @Bean
    public Binding bindNotifAprovada(Queue filaNotificacao, TopicExchange eventsExchange) {
        return BindingBuilder.bind(filaNotificacao).to(eventsExchange).with(RoutingKeys.DENUNCIA_APROVADA);
    }

    @Bean
    public Binding bindNotifRejeitada(Queue filaNotificacao, TopicExchange eventsExchange) {
        return BindingBuilder.bind(filaNotificacao).to(eventsExchange).with(RoutingKeys.DENUNCIA_REJEITADA);
    }

    @Bean
    public Binding bindNotifPrecoRejeitado(Queue filaNotificacao, TopicExchange eventsExchange) {
        return BindingBuilder.bind(filaNotificacao).to(eventsExchange).with(RoutingKeys.PRECO_REJEITADO);
    }

    @Bean
    public Binding bindFotoDlq(Queue filaFotoDlq, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(filaFotoDlq).to(deadLetterExchange).with(RoutingKeys.FILA_FOTO_DLQ);
    }

    @Bean
    public Binding bindNotifDlq(Queue filaNotificacaoDlq, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(filaNotificacaoDlq).to(deadLetterExchange).with(RoutingKeys.FILA_NOTIFICACAO_DLQ);
    }
}