package com.cozinha.consumer;

import com.cozinha.dto.RecoveryPedidoDTO;
import com.cozinha.exceptions.ErroPedidoException;
import com.cozinha.exceptions.InfraException;
import com.cozinha.producer.PedidoProducer;
import com.cozinha.service.CozinhaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

@Component
@EnableRetry
public class PedidoConsumer {

    @Autowired
    private CozinhaService cozinhaService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "cozinha-queue")
    public void receberPedido(@Payload String pedidoJSON) {
        try {
            RecoveryPedidoDTO pedido = converterMensagemJSON(pedidoJSON);
            cozinhaService.realizarPedido(pedido);
        } catch (ErroPedidoException | InfraException e) {
            cozinhaService.processarErro(e, pedidoJSON);
        }
    }

    private RecoveryPedidoDTO converterMensagemJSON(String pedidoJson) {
        try {
            return objectMapper.readValue(pedidoJson, RecoveryPedidoDTO.class);
        } catch (InvalidFormatException | StreamReadException e) {
            throw new ErroPedidoException("JSON contém dados inválidos");
        }
    }
}
