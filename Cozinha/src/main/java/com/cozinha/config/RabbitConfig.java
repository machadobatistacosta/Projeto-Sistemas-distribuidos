package com.cozinha.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange("pedido-exchange", true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("dead-letter-exchange", true, false);
    }

    @Bean
    public Queue cozinhaQueue() {
        return new Queue("cozinha-queue", true);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dead-letter-queue", true);
    }

    @Bean
    public Binding cozinhaBinding(Queue cozinhaQueue, TopicExchange pedidoExchange) {
        return BindingBuilder.bind(cozinhaQueue).to(pedidoExchange).with("pedido-key.pago");
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead-message");
    }
}
