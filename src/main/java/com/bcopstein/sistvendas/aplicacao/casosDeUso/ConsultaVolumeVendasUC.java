package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.VolumeVendasDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class ConsultaVolumeVendasUC {
    private final ServicoDeVendas servicoDeVendas;

    public ConsultaVolumeVendasUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public VolumeVendasDTO run(LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.calcularVolumeVendasPorPeriodo(dataInicial, dataFinal);
    }
}