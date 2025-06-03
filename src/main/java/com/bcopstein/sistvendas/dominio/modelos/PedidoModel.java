package com.bcopstein.sistvendas.dominio.modelos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PedidoModel {
    private long id;
    private List<ItemPedidoModel> itens;

    public PedidoModel(long id) {
        this.id = id;
        this.itens = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public List<ItemPedidoModel> getItens() {
        return new ArrayList<ItemPedidoModel>(itens);
    }

    public void addItem(ItemPedidoModel item) {
        itens.add(item);
    }
}
