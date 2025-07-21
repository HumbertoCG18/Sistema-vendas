package com.bcopstein.sistvendas.aplicacao.dtos;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class ProdutoEstoqueDTO {
    private final long id;
    private final String descricao;
    private final double precoUnitario;
    private final int quantidadeEmEstoque;
    private final boolean listado;
    private final int estoqueMin;
    private final int estoqueMax;
    private final String status; // NOVO CAMPO

    public ProdutoEstoqueDTO(long id, String descricao, double precoUnitario, int quantidadeEmEstoque, boolean listado, int estoqueMin, int estoqueMax) {
        this.id = id;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidadeEmEstoque = quantidadeEmEstoque;
        this.listado = listado;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.status = calcularStatus(listado, quantidadeEmEstoque, estoqueMin, estoqueMax); // Calcula o status no construtor
    }

    // Getters
    public long getId() { return id; }
    public String getDescricao() { return descricao; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public boolean isListado() { return listado; }
    public int getEstoqueMin() { return estoqueMin; }
    public int getEstoqueMax() { return estoqueMax; }
    public String getStatus() { return status; } // Getter para o novo campo

    // Método privado para calcular a string de status
    private static String calcularStatus(boolean listado, int qtd, int min, int max) {
        if (!listado) {
            return "De-listado";
        }
        if (qtd < min) {
            return "Baixo Estoque";
        }
        if (qtd > max) {
            return "Estoque Excedente";
        }
        return "Disponível"; // Ou "Estoque Normal"
    }

    public static ProdutoEstoqueDTO fromModels(ProdutoModel produto, ItemDeEstoqueModel itemEstoque) {
        if (produto == null) {
             return new ProdutoEstoqueDTO(0, "Produto Inválido", 0, 0, false, 0, 0);
        }
        if (itemEstoque == null) {
            // Se o produto existe mas não tem item de estoque, consideramos zerado e de-listado
            return new ProdutoEstoqueDTO(produto.getId(), produto.getDescricao(), produto.getPrecoUnitario(), 0, false, 0, 0);
        }
        return new ProdutoEstoqueDTO(
            produto.getId(),
            produto.getDescricao(),
            produto.getPrecoUnitario(),
            itemEstoque.getQuantidade(),
            itemEstoque.isListado(),
            itemEstoque.getEstoqueMin(),
            itemEstoque.getEstoqueMax()
        );
    }
}