package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.List;

public class NovoOrcamentoRequestDTO {
    private List<ItemPedidoDTO> itens;
    private String nomeCliente;
    private String cpfCliente; 
    private String emailCliente; 
    private String estadoCliente;
    private String paisCliente;

    // Construtor padrão
    public NovoOrcamentoRequestDTO() {}

    // Construtor completo
    public NovoOrcamentoRequestDTO(List<ItemPedidoDTO> itens, String nomeCliente, String cpfCliente, String emailCliente, String estadoCliente, String paisCliente) {
        this.itens = itens;
        this.nomeCliente = nomeCliente;
        this.cpfCliente = cpfCliente;
        this.emailCliente = emailCliente;
        this.estadoCliente = estadoCliente;
        this.paisCliente = paisCliente;
    }

    // Getters
    public List<ItemPedidoDTO> getItens() { return itens; }
    public String getNomeCliente() { return nomeCliente; }
    public String getCpfCliente() { return cpfCliente; }
    public String getEmailCliente() { return emailCliente; }
    public String getEstadoCliente() { return estadoCliente;}
    public String getPaisCliente() { return paisCliente; }

    //Setters
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente;}
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente;}
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

}