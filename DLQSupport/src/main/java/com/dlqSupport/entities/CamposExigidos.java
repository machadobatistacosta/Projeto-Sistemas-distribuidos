package com.dlqSupport.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CamposExigidos(
        String tipo,
        Boolean isObrigatorio,
        List<String> valoresPermitidos,
        Map<String, CamposExigidos> subValores
) {
}
