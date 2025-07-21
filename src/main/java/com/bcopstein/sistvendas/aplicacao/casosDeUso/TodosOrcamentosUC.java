package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;

@Component
public class TodosOrcamentosUC {
    private final IOrcamentoRepositorio orcamentoRepositorio;

    public TodosOrcamentosUC(IOrcamentoRepositorio orcamentoRepositorio) {
        this.orcamentoRepositorio = orcamentoRepositorio;
    }

    public List<OrcamentoDTO> run() {
        List<OrcamentoModel> orcamentos = orcamentoRepositorio.findAll();

        return orcamentos.stream()
                .map(OrcamentoDTO::fromModel)
                .collect(Collectors.toList());
    }
}