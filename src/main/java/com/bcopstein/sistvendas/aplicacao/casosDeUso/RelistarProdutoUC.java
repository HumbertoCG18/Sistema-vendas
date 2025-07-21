package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class RelistarProdutoUC {
    private final ServicoDeEstoque servicoDeEstoque;

    public RelistarProdutoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public boolean run(long produtoId) {
        return servicoDeEstoque.relistarProduto(produtoId);
    }
}