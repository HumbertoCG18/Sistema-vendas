package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class ProdutosDisponiveisUC {
    private ServicoDeEstoque servicoEstoque;

    @Autowired
    public ProdutosDisponiveisUC(ServicoDeEstoque servicoEstoque){
        this.servicoEstoque = servicoEstoque;
    }

    public List<ProdutoDTO> run(){
        return servicoEstoque.produtosDisponiveis().stream()
            .map(p->ProdutoDTO.fromModel(p))
            .toList();
    }
}
