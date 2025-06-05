package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;

@Component
public class TodosOrcamentosUC {
    private IOrcamentoRepositorio orcamentoRepositorio;

    @Autowired
    public TodosOrcamentosUC(IOrcamentoRepositorio orcamentoRepositorio) {
        this.orcamentoRepositorio = orcamentoRepositorio;
    }

    public List<OrcamentoDTO> run() {
        // Altera a chamada de todos() para findAll()
        List<OrcamentoModel> orcamentos = orcamentoRepositorio.findAll();

        return orcamentos.stream()
                .map(OrcamentoDTO::fromModel) // Usa o fromModel atualizado
                .collect(Collectors.toList());
    }
}