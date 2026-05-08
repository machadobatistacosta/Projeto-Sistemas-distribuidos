package com.dlqSupport.entities;
import java.util.List;
import java.util.Map;

public class RegistroEstruturas {
    private Map<String, EstruturaMensagem> registro;

    public RegistroEstruturas() {
        this.registro = Map.of("PEDIDO_STATUS_UPDATE", new EstruturaMensagem
                        (Map.of("id", new CamposExigidos("long", true, null, null),
                                "statusPedido", new CamposExigidos("Enum", true, List.of("EM_PREPARO", "PRONTO"), null))),
                        "PEDIDO_STATUS_PAGO", new EstruturaMensagem
                        (Map.of("id", new CamposExigidos("long", true, null, null),
                                "dataDoPedido", new CamposExigidos("LocalDate", true, null, null),
                                "itens", new CamposExigidos("List<ItemPedido>", true, null, Map.of(
                                                "idProduto", new CamposExigidos("long", true, null, null),
                                                "quantidadeProduto", new CamposExigidos("int", true, null, null))))));
    }

    public EstruturaMensagem buscarRegistro(String registroKey) {
        return registro.get(registroKey);
    }
}
