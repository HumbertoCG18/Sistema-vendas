package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class EntradaEstoqueUC {
    private final ServicoDeEstoque servicoDeEstoque;

    public EntradaEstoqueUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public void run(long id, int qtdade) {
        servicoDeEstoque.entradaEstoque(id, qtdade);
    }
}