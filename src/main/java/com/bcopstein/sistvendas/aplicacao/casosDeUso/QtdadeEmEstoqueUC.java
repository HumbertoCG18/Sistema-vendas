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

    // Método run existente para um único ID
    public int run(long id) { //
        return servicoDeEstoque.qtdadeEmEstoque(id);
    }

    // >>> NOVO MÉTODO run SOBRECARREGADO para uma lista de IDs <<<
    public List<ProdutoEstoqueDTO> run(List<Long> idsProdutos) {
        return servicoDeEstoque.quantidadesEmEstoquePorLista(idsProdutos);
    }
}