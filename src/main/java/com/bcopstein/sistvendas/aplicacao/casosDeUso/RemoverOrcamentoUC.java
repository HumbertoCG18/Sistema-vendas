package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoverOrcamentoUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public RemoverOrcamentoUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public boolean run(long orcamentoId) {
        return servicoDeVendas.removerOrcamento(orcamentoId);
    }
}