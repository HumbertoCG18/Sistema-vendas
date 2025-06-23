package com.bcopstein.sistvendas.dominio.servicos.impostos;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.math.BigDecimal;
import java.util.List;

public class CalculadorImpostoRS implements CalculadorImpostoEstadualStrategy {
    private static final BigDecimal CEM = new BigDecimal("100.00");
    private static final BigDecimal ALIQUOTA_RS = new BigDecimal("0.10");

    @Override
    public BigDecimal calcular(List<ItemPedidoModel> itens) {
        BigDecimal custoItens = itens.stream()
                .map(item -> BigDecimal.valueOf(item.getProduto().getPrecoUnitario())
                        .multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (custoItens.compareTo(CEM) >= 0) {
            return (custoItens.subtract(CEM)).multiply(ALIQUOTA_RS);
        }
        return BigDecimal.ZERO; // Isento
    }

    @Override
    public BigDecimal getAliguotaEfetiva(List<ItemPedidoModel> itens) {
        BigDecimal custoItens = itens.stream()
                .map(item -> BigDecimal.valueOf(item.getProduto().getPrecoUnitario())
                        .multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // A alíquota só é 10% se o valor for maior ou igual a 100, senão é 0.
        return custoItens.compareTo(CEM) >= 0 ? ALIQUOTA_RS : BigDecimal.ZERO;
    }

    @Override
    public String getNomeImposto() {
        return "ICMS-RS";
    }
}