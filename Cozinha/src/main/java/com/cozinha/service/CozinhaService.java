package com.cozinha.service;

import com.cozinha.dto.DLQSupportDTO;
import com.cozinha.dto.RecoveryPedidoDTO;
import com.cozinha.dto.UpdatePedidoDTO;
import com.cozinha.entities.StatusPedido;
import com.cozinha.exceptions.InfraException;
import com.cozinha.producer.PedidoProducer;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CozinhaService {

    @Autowired
    private PedidoProducer pedidoProducer;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 5000), retryFor = {
            InfraException.class
    })
    public void realizarPedido(RecoveryPedidoDTO pedido) {
        try {
            System.out.println(pedido);

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            pedidoProducer.enviarAtualizacao(new UpdatePedidoDTO(pedido.id(), pedido.usuarioId(), StatusPedido.EM_PREPARO));
            System.out.println("Atualizado (EM_PREPARO)");

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            pedidoProducer.enviarAtualizacao(new UpdatePedidoDTO(pedido.id(), pedido.usuarioId(), StatusPedido.PRONTO));
            System.out.println("Atualizado (PRONTO)");
        } catch (AmqpException e) {
            throw new InfraException("Erro na conexão");
        }
    }

    public void processarErro(Exception e, String JSON) {
        String tipo = "DATA_ERROR";
        if (e instanceof InfraException) {
            tipo = "INFRA_ERROR";
        }
        DLQSupportDTO dlqSupportDTO = new DLQSupportDTO(
                "PEDIDO_STATUS_PAGO",
                "cozinha-queue",
                tipo,
                e.getMessage(),
                JSON,
                LocalDateTime.now()
        );
        pedidoProducer.dlqSender(dlqSupportDTO);
        System.out.println(dlqSupportDTO);
    }
}
