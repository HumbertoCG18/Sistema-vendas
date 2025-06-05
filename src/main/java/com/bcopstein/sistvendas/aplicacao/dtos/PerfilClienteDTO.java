package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PerfilClienteDTO {
    private String nomeCliente;
    private LocalDate dataInicialFiltro; // Pode ser null se não houver filtro de data
    private LocalDate dataFinalFiltro;   // Pode ser null se não houver filtro de data
    private BigDecimal totalGastoPeloCliente;
    private int totalOrcamentosEfetivadosConsiderados;
    private List<ItemCompradoDTO> itensComprados;

    public PerfilClienteDTO(String nomeCliente, LocalDate dataInicialFiltro, LocalDate dataFinalFiltro, BigDecimal totalGastoPeloCliente, int totalOrcamentosEfetivadosConsiderados, List<ItemCompradoDTO> itensComprados) {
        this.nomeCliente = nomeCliente;
        this.dataInicialFiltro = dataInicialFiltro;
        this.dataFinalFiltro = dataFinalFiltro;
        this.totalGastoPeloCliente = totalGastoPeloCliente;
        this.totalOrcamentosEfetivadosConsiderados = totalOrcamentosEfetivadosConsiderados;
        this.itensComprados = itensComprados;
    }

    // Getters
    public String getNomeCliente() { return nomeCliente; }
    public LocalDate getDataInicialFiltro() { return dataInicialFiltro; }
    public LocalDate getDataFinalFiltro() { return dataFinalFiltro; }
    public BigDecimal getTotalGastoPeloCliente() { return totalGastoPeloCliente; }
    public int getTotalOrcamentosEfetivadosConsiderados() { return totalOrcamentosEfetivadosConsiderados; }
    public List<ItemCompradoDTO> getItensComprados() { return itensComprados; }
}