package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;

public class VendaProdutoDTO {
    private long idProduto;
    private String descricaoProduto;
    private int quantidadeTotalVendida;
    private BigDecimal valorTotalArrecadado; // Valor baseado no preço do item no orçamento

    public VendaProdutoDTO(long idProduto, String descricaoProduto, int quantidadeTotalVendida,
            BigDecimal valorTotalArrecadado) {
        this.idProduto = idProduto;
        this.descricaoProduto = descricaoProduto;
        this.quantidadeTotalVendida = quantidadeTotalVendida;
        this.valorTotalArrecadado = valorTotalArrecadado;
    }

    // Getters
    public long getIdProduto() {
        return idProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public int getQuantidadeTotalVendida() {
        return quantidadeTotalVendida;
    }

    public BigDecimal getValorTotalArrecadado() {
        return valorTotalArrecadado;
    }

}