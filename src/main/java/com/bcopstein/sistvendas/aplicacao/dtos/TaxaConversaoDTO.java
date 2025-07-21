package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class TaxaConversaoDTO {
    private final LocalDate dataInicialPeriodo;
    private final LocalDate dataFinalPeriodo;
    private final long totalOrcamentosCriados;
    private final long totalOrcamentosEfetivados;
    private final BigDecimal percentualConversao; // Usar BigDecimal para precisão

    public TaxaConversaoDTO(LocalDate dataInicialPeriodo, LocalDate dataFinalPeriodo, long totalOrcamentosCriados, long totalOrcamentosEfetivados) {
        this.dataInicialPeriodo = dataInicialPeriodo;
        this.dataFinalPeriodo = dataFinalPeriodo;
        this.totalOrcamentosCriados = totalOrcamentosCriados;
        this.totalOrcamentosEfetivados = totalOrcamentosEfetivados;
        if (totalOrcamentosCriados > 0) {
            this.percentualConversao = BigDecimal.valueOf(totalOrcamentosEfetivados)
                    .multiply(new BigDecimal("100.0"))
                    .divide(BigDecimal.valueOf(totalOrcamentosCriados), 2, RoundingMode.HALF_UP);
        } else {
            this.percentualConversao = BigDecimal.ZERO;
        }
    }

    // Getters
    public LocalDate getDataInicialPeriodo() { return dataInicialPeriodo;}
    public LocalDate getDataFinalPeriodo() { return dataFinalPeriodo;}
    public long getTotalOrcamentosCriados() { return totalOrcamentosCriados;}
    public long getTotalOrcamentosEfetivados() { return totalOrcamentosEfetivados;}
    public BigDecimal getPercentualConversao() { return percentualConversao;}
}