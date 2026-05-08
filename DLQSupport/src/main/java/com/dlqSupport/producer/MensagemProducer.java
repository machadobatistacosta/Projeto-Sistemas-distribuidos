package com.dlqSupport.producer;

import com.dlqSupport.dto.FixedMessageDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class MensagemProducer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void reenviarJson(FixedMessageDTO fixedMessageDTO) {
        String exchange = "pedido-exchange";
        String routingKey = "pedido-key.pago";

        if(fixedMessageDTO.filaDeOrigem().equals("pedido-queue")) {
            routingKey = "pedido-key.update";
        }

        if(fixedMessageDTO.filaDeOrigem().equals("user-queue")
                || fixedMessageDTO.filaDeOrigem().equals("front-queue")) {
            routingKey = "pedido-key.status";
        }

        amqpTemplate.convertAndSend(
                exchange,
                routingKey,
                fixedMessageDTO.mensagemCorrigida()
        );

    }

}
