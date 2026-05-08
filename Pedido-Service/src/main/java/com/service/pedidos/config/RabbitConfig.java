package com.service.pedidos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PEDIDO_EXCHANGE = "pedido-exchange";
    public static final String DEAD_LETTER_EXCHANGE = "dead-letter-exchange";

    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(PEDIDO_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue pedidoQueue() {
        return new Queue("pedido-queue", true);
    }

    @Bean
    public Queue cozinhaQueue() {
        return new Queue("cozinha-queue", true);
    }

    @Bean
    public Queue userQueue() {
        return new Queue("user-queue", true);
    }

    @Bean
    public Queue frontQueue() {
        return new Queue("front-queue", true);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dead-letter-queue", true);
    }

    @Bean
    public Binding pedidoBinding(Queue pedidoQueue, TopicExchange pedidoExchange) {
        return BindingBuilder.bind(pedidoQueue).to(pedidoExchange).with("pedido-key.update");
    }

    @Bean
    public Binding cozinhaBinding(Queue cozinhaQueue, TopicExchange pedidoExchange) {
        return BindingBuilder.bind(cozinhaQueue).to(pedidoExchange).with("pedido-key.pago");
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange pedidoExchange) {
        return BindingBuilder.bind(userQueue).to(pedidoExchange).with("pedido-key.status");
    }

    @Bean
    public Binding frontBinding(Queue frontQueue, TopicExchange pedidoExchange) {
        return BindingBuilder.bind(frontQueue).to(pedidoExchange).with("pedido-key.status");
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead-message");
    }
}
