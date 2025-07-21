package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class QtdadeEmEstoqueUC {
    private final ServicoDeEstoque servicoDeEstoque;

    public QtdadeEmEstoqueUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public int run(long id) { //
        return servicoDeEstoque.qtdadeEmEstoque(id);
    }

    public List<ProdutoEstoqueDTO> run(List<Long> idsProdutos) {
        return servicoDeEstoque.quantidadesEmEstoquePorLista(idsProdutos);
    }
}