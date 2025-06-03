package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditarProdutoUC {
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
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