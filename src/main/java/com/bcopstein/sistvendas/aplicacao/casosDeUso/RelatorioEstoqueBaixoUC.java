package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class RelatorioEstoqueBaixoUC {
    private final ServicoDeEstoque servicoDeEstoque;

    public RelatorioEstoqueBaixoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public String run() {
        return servicoDeEstoque.gerarRelatorioEstoqueBaixo();
    }
}