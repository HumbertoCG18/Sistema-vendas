// Em NovoOrcamentoRequestDTO.java
package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.List;

public class NovoOrcamentoRequestDTO {
    private List<ItemPedidoDTO> itens;
    private String estadoCliente; // e.g., "RS", "SC", "SP"
    private String paisCliente; // NOVO CAMPO (e.g., "Brasil")

    // Jackson needs a no-arg constructor
    public NovoOrcamentoRequestDTO() {}

    public NovoOrcamentoRequestDTO(List<ItemPedidoDTO> itens, String estadoCliente, String paisCliente) { // ATUALIZAR CONSTRUTOR
        this.itens = itens;
        this.estadoCliente = estadoCliente;
        this.paisCliente = paisCliente; // ATUALIZAR CONSTRUTOR
    }

    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }

    public String getEstadoCliente() { return estadoCliente; }
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente; }

    public String getPaisCliente() { return paisCliente; } // NOVO GETTER
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente; } // NOVO SETTER
}