package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bcopstein.sistvendas.aplicacao.dtos.NovoProdutoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
@Service
public class AdicionarProdutoUC {
    private final ServicoDeEstoque servicoDeEstoque;


    public AdicionarProdutoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public ProdutoDTO run(NovoProdutoRequestDTO novoProdutoRequestDTO) {
        ProdutoModel produtoAdicionado = servicoDeEstoque.adicionarNovoProduto(novoProdutoRequestDTO);
        return ProdutoDTO.fromModel(produtoAdicionado);
    }
}