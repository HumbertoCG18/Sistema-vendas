package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class QtdadeEmEstoqueUC {
    private final ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public QtdadeEmEstoqueUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public int run(long id) {
        return servicoDeEstoque.qtdadeEmEstoque(id);
    }
}
