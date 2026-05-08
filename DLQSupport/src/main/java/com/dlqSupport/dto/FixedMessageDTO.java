package com.dlqSupport.dto;

import jakarta.validation.constraints.NotNull;

public record FixedMessageDTO(
        @NotNull Long id,
        @NotNull String filaDeOrigem,
        @NotNull String mensagemCorrigida,
        @NotNull String correcaoDocumentada
) {
}
