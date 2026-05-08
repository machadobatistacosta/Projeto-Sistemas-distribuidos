package com.cozinha.dto;

import com.cozinha.entities.StatusPedido;

public record UpdatePedidoDTO(
        long id,
        Long usuarioId,
        StatusPedido statusPedido
) {
}
