package com.dlqSupport.entities;

import java.util.Map;

public record EstruturaMensagem(
        Map<String, CamposExigidos> valores
) {
}
