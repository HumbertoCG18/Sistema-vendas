package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class UltimosOrcamentosEfetivadosUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public UltimosOrcamentosEfetivadosUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public List<OrcamentoDTO> run(int n) {
        return servicoDeVendas.ultimosOrcamentosEfetivados(n)
            .stream()
            .map(OrcamentoDTO::fromModel)
            .collect(Collectors.toList());
    }
}