package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class ConsultaOrcamentosEfetivadosUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public ConsultaOrcamentosEfetivadosUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    // Método run atualizado para aceitar período
    public List<OrcamentoDTO> run(LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.orcamentosEfetivadosPorPeriodo(dataInicial, dataFinal)
                .stream()
                .map(OrcamentoDTO::fromModel)
                .collect(Collectors.toList());
    }
}