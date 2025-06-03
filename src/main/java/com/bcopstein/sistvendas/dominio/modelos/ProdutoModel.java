package com.bcopstein.sistvendas.dominio.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto")
public class ProdutoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String descricao;
    private double precoUnitario;

    // Construtor padrão para JPA
    protected ProdutoModel() {}

    // Construtor para novos produtos (sem ID) - ADICIONADO/GARANTIDO
    public ProdutoModel(String descricao, double precoUnitario) {
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
    }

    // Construtor com ID (mantido para flexibilidade/data.sql)
    public ProdutoModel(long id, String descricao, double precoUnitario) {
        this.id = id;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
    }

    // Getters e Setters
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPrecoUnitario() {
        return this.precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    @Override
    public String toString() {
        return "ProdutoModel{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", precoUnitario=" + precoUnitario +
                '}';
    }
}