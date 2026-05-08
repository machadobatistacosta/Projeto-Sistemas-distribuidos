package com.cozinha.dto;

import jakarta.validation.constraints.NotNull;

public record RecoveryItemPedidoDTO(
        @NotNull long idProduto,
        @NotNull int quantidadeProduto
) {
}
