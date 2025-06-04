package com.bcopstein.sistvendas.aplicacao.dtos;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxaConversaoDTO {
    private LocalDate dataInicialPeriodo;
    private LocalDate dataFinalPeriodo;
    private long totalOrcamentosCriados;
    private long totalOrcamentosEfetivados;
    private BigDecimal percentualConversao; // Usar BigDecimal para precisão

    public TaxaConversaoDTO(LocalDate dataInicialPeriodo, LocalDate dataFinalPeriodo, 
                            long totalOrcamentosCriados, long totalOrcamentosEfetivados) {
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
    public LocalDate getDataInicialPeriodo() { return dataInicialPeriodo; }
    public LocalDate getDataFinalPeriodo() { return dataFinalPeriodo; }
    public long getTotalOrcamentosCriados() { return totalOrcamentosCriados; }
    public long getTotalOrcamentosEfetivados() { return totalOrcamentosEfetivados; }
    public BigDecimal getPercentualConversao() { return percentualConversao; }

    // Setters (opcional)
}