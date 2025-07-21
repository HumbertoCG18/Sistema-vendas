package com.bcopstein.sistvendas.aplicacao.dtos;

import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class ProdutoDTO {
    private final long id;
    private final String descricao;
    private final double precoUnitario;

    public ProdutoDTO(long id, String descricao, double precoUnitario) {
        this.id = id;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
    }
//Getters
    public long getId() { return this.id;}
    public String getDescricao() { return this.descricao;}
    public double getPrecoUnitario() { return this.precoUnitario;}

    public static ProdutoDTO fromModel(ProdutoModel produto) {
        return new ProdutoDTO(produto.getId(), produto.getDescricao(), produto.getPrecoUnitario());
    }
}
