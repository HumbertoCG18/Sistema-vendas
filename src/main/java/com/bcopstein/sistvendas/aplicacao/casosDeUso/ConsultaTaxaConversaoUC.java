package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.TaxaConversaoDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class ConsultaTaxaConversaoUC {
    private final ServicoDeVendas servicoDeVendas;

    public ConsultaTaxaConversaoUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public TaxaConversaoDTO run(LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.calcularTaxaConversaoPorPeriodo(dataInicial, dataFinal);
    }
}