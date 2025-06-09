package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelatorioEstoqueBaixoUC {
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public RelatorioEstoqueBaixoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public String run() {
        return servicoDeEstoque.gerarRelatorioEstoqueBaixo();
    }
}