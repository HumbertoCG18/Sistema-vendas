package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class DesativarProdutoUC {
    private final ServicoDeEstoque servicoDeEstoque;
    private final ServicoDeVendas servicoDeVendas;

    public DesativarProdutoUC(ServicoDeEstoque servicoDeEstoque, ServicoDeVendas servicoDeVendas) {
        this.servicoDeEstoque = servicoDeEstoque;
        this.servicoDeVendas = servicoDeVendas;
    }

    @Transactional // Garante que ambas as operações ocorram ou nenhuma
    public boolean run(long produtoId) {
        boolean desativado = servicoDeEstoque.desativarProduto(produtoId);
        if (desativado) {
            // Se desativou, atualiza os orçamentos que continham o produto
            servicoDeVendas.atualizarOrcamentosAposRemocaoProduto(produtoId);
        }
        return desativado;
    }
}