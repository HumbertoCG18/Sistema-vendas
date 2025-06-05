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

    public ItemDeEstoqueModel(ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.listado = true;
    }
    
    public ItemDeEstoqueModel(long id, ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.listado = true;
    }

    //Setters
    public void setId(long id) { this.id = id;}
    public void setQuantidade(int quantidade) { this.quantidade = quantidade;}
    public void setProduto(ProdutoModel produto) { this.produto = produto;}
    public void setEstoqueMax(int estoqueMax) { this.estoqueMax = estoqueMax;}
    public void setListado(boolean listado) { this.listado = listado;}
    public void setEstoqueMin(int estoqueMin) { this.estoqueMin = estoqueMin; }

    // Getters
    public long getId() { return id; }
    public ProdutoModel getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public int getEstoqueMin() { return estoqueMin; }
    public int getEstoqueMax() { return estoqueMax;}
    
    public boolean isListado() { return listado; }

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