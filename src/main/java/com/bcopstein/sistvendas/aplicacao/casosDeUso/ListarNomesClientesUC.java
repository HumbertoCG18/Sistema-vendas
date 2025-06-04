package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ListarNomesClientesUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public ListarNomesClientesUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public List<String> run() {
        return servicoDeVendas.listarNomesClientesComCompras();
    }
}