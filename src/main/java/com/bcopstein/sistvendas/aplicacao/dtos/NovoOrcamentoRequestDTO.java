package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.List;

public class NovoOrcamentoRequestDTO {
    private List<ItemPedidoDTO> itens;
    private String estadoCliente;
    private String paisCliente;
    private String nomeCliente; // NOVO CAMPO

    public NovoOrcamentoRequestDTO() {}

    // Construtor atualizado
    public NovoOrcamentoRequestDTO(List<ItemPedidoDTO> itens, String estadoCliente, String paisCliente, String nomeCliente) {
        this.itens = itens;
        this.estadoCliente = estadoCliente;
        this.paisCliente = paisCliente;
        this.nomeCliente = nomeCliente; // ATUALIZAR CONSTRUTOR
    }

    // Getters e Setters existentes...
    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }

    public String getEstadoCliente() { return estadoCliente; }
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente; }

    public String getPaisCliente() { return paisCliente; }
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente; }

    public String getNomeCliente() { return nomeCliente; } // NOVO GETTER
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; } // NOVO SETTER
}