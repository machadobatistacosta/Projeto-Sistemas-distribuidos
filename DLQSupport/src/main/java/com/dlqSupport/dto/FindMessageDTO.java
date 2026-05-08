package com.dlqSupport.dto;

import com.dlqSupport.entities.EstruturaMensagem;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record FindMessageDTO(
        @NotNull String mensagemOriginal,
        @NotNull String tipoMensagem,
        @NotNull String mensagemDeErro,
        @NotNull String filaDeOrigem,
        @NotNull LocalDateTime timestamp,
        @NotNull EstruturaMensagem estruturaMensagem,
        @NotNull String tipoErro
) {
}
