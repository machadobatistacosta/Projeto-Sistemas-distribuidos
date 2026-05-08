package com.dlqSupport.dto;

import com.dlqSupport.entities.EstruturaMensagem;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DLQSupportDTO(
        @NotNull String tipoMensagem,
        @NotNull String filaDeOrigem,
        @NotNull String tipoErro,
        @NotNull String mensagemDeErro,
        @NotNull String mensagemOriginal,
        @NotNull LocalDateTime timestamp
) {}
