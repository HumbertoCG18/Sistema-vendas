package com.bcopstein.sistvendas.dominio.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_de_estoque")
public class ItemDeEstoqueModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "produto_id")
    private ProdutoModel produto;

    private int quantidade;
    private int estoqueMin;
    private int estoqueMax;
    private boolean listado = true;

    // Construtor padrão para JPA
    protected ItemDeEstoqueModel() {}

    // Construtor para novos itens (sem ID) - ADICIONADO/GARANTIDO
    public ItemDeEstoqueModel(ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.listado = true;
    }
    
    // Construtor com ID (mantido para flexibilidade/data.sql)
    public ItemDeEstoqueModel(long id, ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.listado = true;
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public ProdutoModel getProduto() { return produto; }
    public void setProduto(ProdutoModel produto) { this.produto = produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public int getEstoqueMin() { return estoqueMin; }
    public void setEstoqueMin(int estoqueMin) { this.estoqueMin = estoqueMin; }
    public int getEstoqueMax() { return estoqueMax; }
    public void setEstoqueMax(int estoqueMax) { this.estoqueMax = estoqueMax; }
    public boolean isListado() { return listado; }
    public void setListado(boolean listado) { this.listado = listado; }

    @Override
    public String toString() {
        return "ItemDeEstoqueModel[" +
                "id=" + id +
                ", produto=" + (produto != null ? produto.getId() : "null") +
                ", quantidade=" + quantidade +
                ", estoqueMin=" + estoqueMin +
                ", estoqueMax=" + estoqueMax +
                ", listado=" + listado +
                ']';
    }
}