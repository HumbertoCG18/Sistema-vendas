package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.PerfilClienteDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class ConsultaPerfilClienteUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public ConsultaPerfilClienteUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public PerfilClienteDTO run(String nomeCliente, LocalDate dataInicial, LocalDate dataFinal) {
        return servicoDeVendas.gerarPerfilCliente(nomeCliente, dataInicial, dataFinal);
    }
}