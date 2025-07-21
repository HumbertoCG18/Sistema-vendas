package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.PerfilClienteDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class ConsultaPerfilClienteUC {
    private final ServicoDeVendas servicoDeVendas;

    public ConsultaPerfilClienteUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public PerfilClienteDTO run(String nomeCliente, LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.gerarPerfilCliente(nomeCliente, dataInicial, dataFinal);
    }
}