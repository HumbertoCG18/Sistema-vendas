package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PerfilClienteDTO {
    private String nomeCliente;
    private String cpf; // NOVO CAMPO
    private String email; // NOVO CAMPO
    private LocalDate dataInicialFiltro;
    private LocalDate dataFinalFiltro;
    private BigDecimal totalGastoPeloCliente;
    private int totalOrcamentosEfetivadosConsiderados;
    private List<ItemCompradoDTO> itensComprados;

    // Construtor ATUALIZADO
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
    public String getCpf() { return cpf; } // NOVO GETTER
    public String getEmail() { return email; } // NOVO GETTER
    public LocalDate getDataInicialFiltro() { return dataInicialFiltro; }
    public LocalDate getDataFinalFiltro() { return dataFinalFiltro; }
    public BigDecimal getTotalGastoPeloCliente() { return totalGastoPeloCliente; }
    public int getTotalOrcamentosEfetivadosConsiderados() { return totalOrcamentosEfetivadosConsiderados; }
    public List<ItemCompradoDTO> getItensComprados() { return itensComprados; }
}
