package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.List;

public class NovoOrcamentoRequestDTO {
    private List<ItemPedidoDTO> itens;
    private String estadoCliente; // e.g., "RS", "SC", "SP"

    // Jackson needs a no-arg constructor
    public NovoOrcamentoRequestDTO() {}

    public NovoOrcamentoRequestDTO(List<ItemPedidoDTO> itens, String estadoCliente) {
        this.itens = itens;
        this.estadoCliente = estadoCliente;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }
}