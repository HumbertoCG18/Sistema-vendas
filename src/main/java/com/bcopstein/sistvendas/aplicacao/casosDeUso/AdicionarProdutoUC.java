package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.NovoProdutoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdicionarProdutoUC {
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public AdicionarProdutoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public ProdutoDTO run(NovoProdutoRequestDTO novoProdutoRequestDTO) {
        ProdutoModel produtoAdicionado = servicoDeEstoque.adicionarNovoProduto(novoProdutoRequestDTO);
        return ProdutoDTO.fromModel(produtoAdicionado);
    }
}