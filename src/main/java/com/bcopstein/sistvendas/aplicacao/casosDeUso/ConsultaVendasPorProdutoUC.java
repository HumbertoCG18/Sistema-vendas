package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.aplicacao.dtos.VendaProdutoDTO;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class ConsultaVendasPorProdutoUC {
    private ServicoDeVendas servicoDeVendas;

    @Autowired
    public ConsultaVendasPorProdutoUC(ServicoDeVendas servicoDeVendas) {
        this.servicoDeVendas = servicoDeVendas;
    }

    public List<VendaProdutoDTO> run(LocalDate dataInicial, LocalDate dataFinal, Long idProduto) {
        return servicoDeVendas.calcularTotalVendasPorProduto(dataInicial, dataFinal, idProduto);
    }

}