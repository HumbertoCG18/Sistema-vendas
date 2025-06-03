package com.bcopstein.sistvendas.dominio.modelos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_pedido")
public class ItemPedidoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ProdutoModel produto;
    private int quantidade;

    @ManyToOne
    @JsonBackReference // Para evitar recursão infinita no JSON
    private OrcamentoModel orcamento;

    // Construtor padrão para JPA
    protected ItemPedidoModel() {}

    public ItemPedidoModel(ProdutoModel produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public ProdutoModel getProduto() { return produto; }
    public void setProduto(ProdutoModel produto) { this.produto = produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public OrcamentoModel getOrcamento() { return orcamento; }
    public void setOrcamento(OrcamentoModel orcamento) { this.orcamento = orcamento; }

     @Override
    public String toString() {
        return "ItemPedido [produto=" + (produto != null ? produto.getId() : "null") + ", quantidade=" + quantidade + "]";
    }
}