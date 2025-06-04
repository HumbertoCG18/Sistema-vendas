package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;

public class ItemCompradoDTO {
    private long idProduto;
    private String descricaoProduto;
    private int quantidadeTotalComprada;
    private BigDecimal valorTotalGastoNoProduto;

    public ItemCompradoDTO(long idProduto, String descricaoProduto, int quantidadeTotalComprada, BigDecimal valorTotalGastoNoProduto) {
        this.idProduto = idProduto;
        this.descricaoProduto = descricaoProduto;
        this.quantidadeTotalComprada = quantidadeTotalComprada;
        this.valorTotalGastoNoProduto = valorTotalGastoNoProduto;
    }

    // Getters
    public long getIdProduto() { return idProduto; }
    public String getDescricaoProduto() { return descricaoProduto; }
    public int getQuantidadeTotalComprada() { return quantidadeTotalComprada; }
    public BigDecimal getValorTotalGastoNoProduto() { return valorTotalGastoNoProduto; }
}