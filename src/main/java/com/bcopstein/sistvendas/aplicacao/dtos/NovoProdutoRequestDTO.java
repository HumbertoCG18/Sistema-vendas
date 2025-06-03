package com.bcopstein.sistvendas.aplicacao.dtos;

public class NovoProdutoRequestDTO {
    private String descricao;
    private double precoUnitario;
    private int quantidadeInicialEstoque;
    private int estoqueMin;
    private int estoqueMax;

    // Construtor padrão para Jackson
    public NovoProdutoRequestDTO() {}

    // Construtor completo
    public NovoProdutoRequestDTO(String descricao, double precoUnitario, int quantidadeInicialEstoque, int estoqueMin, int estoqueMax) {
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidadeInicialEstoque = quantidadeInicialEstoque;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
    }

    // Getters
    public String getDescricao() { return descricao; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidadeInicialEstoque() { return quantidadeInicialEstoque; }
    public int getEstoqueMin() { return estoqueMin; }
    public int getEstoqueMax() { return estoqueMax; }

    // Setters
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public void setQuantidadeInicialEstoque(int quantidadeInicialEstoque) { this.quantidadeInicialEstoque = quantidadeInicialEstoque; }
    public void setEstoqueMin(int estoqueMin) { this.estoqueMin = estoqueMin; }
    public void setEstoqueMax(int estoqueMax) { this.estoqueMax = estoqueMax; }
}