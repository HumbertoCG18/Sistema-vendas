package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.VolumeVendasDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class ConsultaVolumeVendasUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public ConsultaVolumeVendasUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public VolumeVendasDTO run(LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.calcularVolumeVendasPorPeriodo(dataInicial, dataFinal);
    }
}