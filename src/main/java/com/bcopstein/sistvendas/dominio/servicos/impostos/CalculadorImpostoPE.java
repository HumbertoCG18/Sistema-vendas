package com.bcopstein.sistvendas.dominio.servicos.impostos;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CalculadorImpostoPE implements CalculadorImpostoEstadualStrategy {
    private static final BigDecimal ALIQUOTA_ESSENCIAIS = new BigDecimal("0.05");
    private static final BigDecimal ALIQUOTA_NAO_ESSENCIAIS = new BigDecimal("0.15");

    @Override
    public BigDecimal calcular(List<ItemPedidoModel> itens) {
        BigDecimal impostoEssenciais = BigDecimal.ZERO;
        BigDecimal impostoNaoEssenciais = BigDecimal.ZERO;

        for (ItemPedidoModel item : itens) {
            ProdutoModel produto = item.getProduto();
            BigDecimal valorItem = BigDecimal.valueOf(produto.getPrecoUnitario())
                    .multiply(new BigDecimal(item.getQuantidade()));

            if (produto.getDescricao().endsWith("*")) { // Produto essencial
                impostoEssenciais = impostoEssenciais.add(valorItem.multiply(ALIQUOTA_ESSENCIAIS));
            } else {
                impostoNaoEssenciais = impostoNaoEssenciais.add(valorItem.multiply(ALIQUOTA_NAO_ESSENCIAIS));
            }
        }
        return impostoEssenciais.add(impostoNaoEssenciais);
    }

    @Override
    public BigDecimal getAliguotaEfetiva(List<ItemPedidoModel> itens) {
        BigDecimal impostoTotal = calcular(itens);
        BigDecimal custoTotal = itens.stream()
                .map(item -> BigDecimal.valueOf(item.getProduto().getPrecoUnitario())
                        .multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (custoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // Retorna a alíquota média ponderada
        return impostoTotal.divide(custoTotal, 4, RoundingMode.HALF_UP);
    }

    @Override
    public String getNomeImposto() {
        return "ICMS-PE";
    }
}