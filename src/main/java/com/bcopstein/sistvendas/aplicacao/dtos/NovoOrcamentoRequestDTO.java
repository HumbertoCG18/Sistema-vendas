package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.List;

public class NovoOrcamentoRequestDTO {
    private List<ItemPedidoDTO> itens;
    private String nomeCliente;
    private String cpfCliente;  // ADICIONAR
    private String emailCliente; // ADICIONAR
    private String estadoCliente;
    private String paisCliente;

    // Construtor padrão
    public NovoOrcamentoRequestDTO() {}

    // Construtor completo (ou use setters)
    public NovoOrcamentoRequestDTO(List<ItemPedidoDTO> itens, String nomeCliente, String cpfCliente, String emailCliente, String estadoCliente, String paisCliente) {
        this.itens = itens;
        this.nomeCliente = nomeCliente;
        this.cpfCliente = cpfCliente;
        this.emailCliente = emailCliente;
        this.estadoCliente = estadoCliente;
        this.paisCliente = paisCliente;
    }

    // Getters e Setters para todos os campos...
    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getCpfCliente() { return cpfCliente; } // ADICIONAR
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; } // ADICIONAR

    public String getEmailCliente() { return emailCliente; } // ADICIONAR
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; } // ADICIONAR

    public String getEstadoCliente() { return estadoCliente; }
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente; }

    public String getPaisCliente() { return paisCliente; }
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente; }
}