package com.dlqSupport.consumer;

import com.dlqSupport.dto.DLQSupportDTO;
import com.dlqSupport.service.SupportService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SupportConsumer {

    @Autowired
    private SupportService supportService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "dead-letter-queue")
    public void receberMensagem(@Payload String mensagem) {
        DLQSupportDTO dlqSupportDTO = objectMapper.readValue(mensagem, DLQSupportDTO.class);
        supportService.processarMensagem(dlqSupportDTO);
    }
}
