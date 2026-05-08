package com.cozinha.producer;

import com.cozinha.dto.DLQSupportDTO;
import com.cozinha.dto.UpdatePedidoDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class PedidoProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void enviarAtualizacao(UpdatePedidoDTO update) {
        amqpTemplate.convertAndSend(
                "pedido-exchange",
                "pedido-key.update",
                objectMapper.writeValueAsString(update)
        );
    }

    public void dlqSender(DLQSupportDTO dlqSupportDTO) {
        amqpTemplate.convertAndSend(
                "dead-letter-exchange",
                "dead-message",
                objectMapper.writeValueAsString(dlqSupportDTO)
        );
    }
}
