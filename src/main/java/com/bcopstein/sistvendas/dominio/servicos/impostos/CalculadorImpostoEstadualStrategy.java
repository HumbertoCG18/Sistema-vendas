package com.bcopstein.sistvendas.dominio.servicos.impostos;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.math.BigDecimal;
import java.util.List;

public interface CalculadorImpostoEstadualStrategy {
    BigDecimal calcular(List<ItemPedidoModel> itens);
    BigDecimal getAliguotaEfetiva(List<ItemPedidoModel> itens);
    String getNomeImposto();
}