package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PerfilClienteDTO {
    private final String nomeCliente;
    private final String cpf;
    private final String email;
    private final LocalDate dataInicialFiltro;
    private final LocalDate dataFinalFiltro;
    private final BigDecimal totalGastoPeloCliente;
    private final int totalOrcamentosEfetivadosConsiderados;
    private final List<ItemCompradoDTO> itensComprados;

    public PerfilClienteDTO(String nomeCliente, String cpf, String email,
                            LocalDate dataInicialFiltro, LocalDate dataFinalFiltro,
                            BigDecimal totalGastoPeloCliente, int totalOrcamentosEfetivadosConsiderados,
                            List<ItemCompradoDTO> itensComprados) {
        this.nomeCliente = nomeCliente;
        this.cpf = cpf;
        this.email = email;
        this.dataInicialFiltro = dataInicialFiltro;
        this.dataFinalFiltro = dataFinalFiltro;
        this.totalGastoPeloCliente = totalGastoPeloCliente;
        this.totalOrcamentosEfetivadosConsiderados = totalOrcamentosEfetivadosConsiderados;
        this.itensComprados = itensComprados;
    }

    // Getters
    public String getNomeCliente() { return nomeCliente; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public LocalDate getDataInicialFiltro() { return dataInicialFiltro; }
    public LocalDate getDataFinalFiltro() { return dataFinalFiltro; }
    public BigDecimal getTotalGastoPeloCliente() { return totalGastoPeloCliente; }
    public int getTotalOrcamentosEfetivadosConsiderados() { return totalOrcamentosEfetivadosConsiderados; }
    public List<ItemCompradoDTO> getItensComprados() { return itensComprados; }
}
