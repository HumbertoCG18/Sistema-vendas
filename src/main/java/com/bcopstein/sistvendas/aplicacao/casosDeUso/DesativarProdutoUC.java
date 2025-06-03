package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas; // Adiciona import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Adiciona import

@Component
public class DesativarProdutoUC {
    private ServicoDeEstoque servicoDeEstoque;
    private ServicoDeVendas servicoDeVendas; // Adiciona dependência

    @Autowired
    public DesativarProdutoUC(ServicoDeEstoque servicoDeEstoque, ServicoDeVendas servicoDeVendas) {
        this.servicoDeEstoque = servicoDeEstoque;
        this.servicoDeVendas = servicoDeVendas; // Injeta
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