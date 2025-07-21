package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class TodosProdutosStatusUC {
    private final ServicoDeEstoque servicoDeEstoque;

    public TodosProdutosStatusUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public List<ProdutoEstoqueDTO> run() {
        return servicoDeEstoque.getTodosProdutosComStatusEstoque();
    }
}