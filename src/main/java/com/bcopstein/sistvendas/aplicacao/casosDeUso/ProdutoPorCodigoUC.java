package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Importa o modelo correto e o DTO
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO; 
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class ProdutoPorCodigoUC {
    private final ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public ProdutoPorCodigoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    // Retorna um DTO para consistência e desacoplamento
    public ProdutoDTO run(long id) {
        ProdutoModel produto = servicoDeEstoque.produtoPorCodigo(id);
        if (produto != null) {
            return ProdutoDTO.fromModel(produto);
        }
        return null; // Retorna nulo se não encontrar
    }
}