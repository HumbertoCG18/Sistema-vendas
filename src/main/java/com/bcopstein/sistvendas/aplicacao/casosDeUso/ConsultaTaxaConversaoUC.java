package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.TaxaConversaoDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class ConsultaTaxaConversaoUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public ConsultaTaxaConversaoUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public TaxaConversaoDTO run(LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.calcularTaxaConversaoPorPeriodo(dataInicial, dataFinal);
    }
}