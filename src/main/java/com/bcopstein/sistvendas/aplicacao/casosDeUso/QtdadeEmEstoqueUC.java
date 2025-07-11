package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO;
import java.util.List;

@Component
public class QtdadeEmEstoqueUC {
    private final ServicoDeEstoque servicoDeEstoque;

    @Autowired
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