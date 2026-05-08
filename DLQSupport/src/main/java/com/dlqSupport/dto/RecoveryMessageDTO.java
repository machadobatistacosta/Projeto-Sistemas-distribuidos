package com.dlqSupport.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RecoveryMessageDTO(
        @NotNull Long id,
        @NotNull String mensagemOriginal,
        @NotNull String tipoMensagem,
        @NotNull String filaDeOrigem,
        @NotNull String tipoErro,
        @NotNull String mensagemDeErro,
        String mensagemCorrigida,
        String correcaoDocumentada,
        @NotNull LocalDateTime timestamp
) {
}
