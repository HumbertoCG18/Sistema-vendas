package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

@Component
public class ListarNomesClientesUC {
    private final ServicoDeVendas servicoDeVendas;

    public ListarNomesClientesUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public List<String> run() {
        return servicoDeVendas.listarNomesClientesComCompras();
    }
}