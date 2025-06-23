package com.bcopstein.sistvendas.dominio.servicos.impostos;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import java.math.BigDecimal;
import java.util.List;

public class CalculadorImpostoSP implements CalculadorImpostoEstadualStrategy {
    private static final BigDecimal ALIQUOTA_SP = new BigDecimal("0.12");

    @Override
    public BigDecimal calcular(List<ItemPedidoModel> itens) {
        BigDecimal custoItens = itens.stream()
                .map(item -> BigDecimal.valueOf(item.getProduto().getPrecoUnitario())
                        .multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return custoItens.multiply(ALIQUOTA_SP);
    }
    
    @Override
    public BigDecimal getAliguotaEfetiva(List<ItemPedidoModel> itens) {
        return ALIQUOTA_SP; 
    }
    
    @Override
    public String getNomeImposto() {
        return "ICMS-SP";
    }
}