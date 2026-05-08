package com.microchefs.produtoservice.dto;

public record UpdatePedidoDTO(
        long id,
        Long usuarioId,
        String statusPedido
) {}
