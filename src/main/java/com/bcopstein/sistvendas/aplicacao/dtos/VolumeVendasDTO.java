package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VolumeVendasDTO {
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private BigDecimal valorTotalVendas;
    private long quantidadeOrcamentosConsiderados; // Adicional: quantos orçamentos formaram esse total

    public VolumeVendasDTO(LocalDate dataInicial, LocalDate dataFinal, BigDecimal valorTotalVendas, long quantidadeOrcamentosConsiderados) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.valorTotalVendas = valorTotalVendas;
        this.quantidadeOrcamentosConsiderados = quantidadeOrcamentosConsiderados;
    }

    // Getters
    public LocalDate getDataInicial() { return dataInicial; }
    public LocalDate getDataFinal() { return dataFinal; }
    public BigDecimal getValorTotalVendas() { return valorTotalVendas; }
    public long getQuantidadeOrcamentosConsiderados() { return quantidadeOrcamentosConsiderados; }

    // Setters (opcional)
    public void setDataInicial(LocalDate dataInicial) { this.dataInicial = dataInicial; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }
    public void setValorTotalVendas(BigDecimal valorTotalVendas) { this.valorTotalVendas = valorTotalVendas; }
    public void setQuantidadeOrcamentosConsiderados(long quantidadeOrcamentosConsiderados) { this.quantidadeOrcamentosConsiderados = quantidadeOrcamentosConsiderados; }
}