package com.bcopstein.sistvendas.aplicacao.dtos;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class ProdutoEstoqueDTO {
    private long id;
    private String descricao;
    private double precoUnitario;
    private int quantidadeEmEstoque;
    private boolean listado;
    private int estoqueMin;
    private int estoqueMax;

    public ProdutoEstoqueDTO(long id, String descricao, double precoUnitario, int quantidadeEmEstoque, boolean listado,
            int estoqueMin, int estoqueMax) {
        this.id = id;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidadeEmEstoque = quantidadeEmEstoque;
        this.listado = listado;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
    }

    // Getters
    public long getId() { return id; }
    public String getDescricao() { return descricao;}
    public double getPrecoUnitario() { return precoUnitario;}
    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque;}
    public int getEstoqueMin() { return estoqueMin;}
    public int getEstoqueMax() { return estoqueMax;}

    public boolean isListado() {
        return listado;
    }

    public static ProdutoEstoqueDTO fromModels(ProdutoModel produto, ItemDeEstoqueModel itemEstoque) {
        if (produto == null || itemEstoque == null) {
            if (produto != null) {
                return new ProdutoEstoqueDTO(produto.getId(), produto.getDescricao(), produto.getPrecoUnitario(), 0,
                        false, 0, 0);
            }
            return new ProdutoEstoqueDTO(0, "Produto Inválido", 0, 0, false, 0, 0);
        }
        return new ProdutoEstoqueDTO(
                produto.getId(),
                produto.getDescricao(),
                produto.getPrecoUnitario(),
                itemEstoque.getQuantidade(),
                itemEstoque.isListado(),
                itemEstoque.getEstoqueMin(),
                itemEstoque.getEstoqueMax());
    }
}