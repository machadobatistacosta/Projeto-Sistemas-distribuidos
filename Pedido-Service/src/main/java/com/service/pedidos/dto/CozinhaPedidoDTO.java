package com.service.pedidos.dto;

import java.time.LocalDate;
import java.util.List;

public record CozinhaPedidoDTO(
        Long id,
        Long usuarioId,
        LocalDate dataDoPedido,
        List<CozinhaItemPedidoDTO> itens
) {
}
