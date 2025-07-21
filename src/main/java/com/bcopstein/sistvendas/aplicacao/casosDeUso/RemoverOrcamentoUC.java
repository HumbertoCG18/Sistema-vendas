package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class RemoverOrcamentoUC {
    private final ServicoDeVendas servicoDeVendas;

    public RemoverOrcamentoUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public boolean run(long orcamentoId) {
        return servicoDeVendas.removerOrcamento(orcamentoId);
    }
}