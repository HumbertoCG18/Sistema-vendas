package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class EditarProdutoUC {
    private final ServicoDeEstoque servicoDeEstoque;

    public EditarProdutoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public ProdutoDTO run(long produtoId, ProdutoDTO produtoDTO) {
        ProdutoModel produtoEditado = servicoDeEstoque.editarProduto(produtoId, produtoDTO);
        if (produtoEditado == null) {
            return null;
        }
        return ProdutoDTO.fromModel(produtoEditado);
    }
}