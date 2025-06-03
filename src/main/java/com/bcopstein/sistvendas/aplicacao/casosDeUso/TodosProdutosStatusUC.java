package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TodosProdutosStatusUC {
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public TodosProdutosStatusUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public List<ProdutoEstoqueDTO> run() {
        return servicoDeEstoque.getTodosProdutosComStatusEstoque();
    }
}