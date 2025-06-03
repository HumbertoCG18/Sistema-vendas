package com.bcopstein.sistvendas.dominio.modelos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;//import java.util.stream.Collectors;

@Entity
@Table(name = "orcamento")
public class OrcamentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference // Para evitar recursão infinita no JSON
    private List<ItemPedidoModel> itens;

    private double custoItens;
    private double imposto;
    private double desconto;
    private double custoConsumidor;
    private boolean efetivado;
    private String estadoCliente;
    private double aliquotaImpostoAplicada;

    // Construtor padrão (sem argumentos) - Usado pelo JPA e pelo código
    public OrcamentoModel() {
        this.itens = new LinkedList<>();
        this.efetivado = false;
    }

    // Getter and Setter for estadoCliente
    public String getEstadoCliente() { return estadoCliente; }
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente; }

    // Getter for aliquotaImpostoAplicada
    public double getAliquotaImpostoAplicada() { return aliquotaImpostoAplicada; }

    public void addItensPedido(PedidoModel pedido) {
        if (pedido != null && pedido.getItens() != null) {
            for (ItemPedidoModel itemPedido : pedido.getItens()) {
                if (itemPedido != null && itemPedido.getProduto() != null) {
                    itemPedido.setOrcamento(this); // Link back
                    this.itens.add(itemPedido);
                }
            }
        }
    }

    public List<ItemPedidoModel> getItens() { return this.itens; }

    public boolean removeItemPorProdutoId(long produtoId) {
        if (this.isEfetivado()) {
            return false;
        }
        int tamanhoOriginal = this.itens.size();
        this.itens.removeIf(item -> item.getProduto() != null && item.getProduto().getId() == produtoId);

        boolean removido = this.itens.size() < tamanhoOriginal;
        if (removido) {
            recalculaTotais();
        }
        return removido;
    }

    public double calculaAliquotaPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return 0.10;
        }
        switch (estado.trim().toUpperCase()) {
            case "RS": return 0.12;
            case "SC": return 0.07;
            case "PR": return 0.11;
            case "SP": return 0.18;
            default: return 0.10;
        }
    }

    public void recalculaTotais() {
        if (this.isEfetivado()) {
            return;
        }

        this.custoItens = 0;
        if (this.itens != null) {
            this.custoItens = this.itens.stream()
                .filter(it -> it.getProduto() != null && it.getProduto().getPrecoUnitario() >= 0 && it.getQuantidade() >= 0)
                .mapToDouble(it -> it.getProduto().getPrecoUnitario() * it.getQuantidade())
                .sum();
        }

        this.aliquotaImpostoAplicada = calculaAliquotaPorEstado(this.estadoCliente);
        this.imposto = this.custoItens * this.aliquotaImpostoAplicada;

        this.desconto = (this.itens != null && this.itens.size() > 5) ? this.custoItens * 0.05 : 0.0;
        this.custoConsumidor = this.custoItens + this.imposto - this.desconto;
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public double getCustoItens() { return custoItens; }
    public void setCustoItens(double custoItens) { this.custoItens = custoItens; }
    public double getImposto() { return imposto; }
    public void setImposto(double imposto) { this.imposto = imposto; }
    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }
    public double getCustoConsumidor() { return custoConsumidor; }
    public void setCustoConsumidor(double custoConsumidor) { this.custoConsumidor = custoConsumidor; }
    public boolean isEfetivado() { return efetivado; }
    public void efetiva() {
        if (!this.efetivado) {
            this.efetivado = true;
        }
    }
     public void setItens(List<ItemPedidoModel> itens) { this.itens = itens; } // Added Setter for itens


    @Override
    public String toString() {
        return "OrcamentoModel{" +
                "id=" + id +
                ", itens=" + (itens != null ? itens.size() : 0) + " itens" +
                ", estadoCliente='" + estadoCliente + '\'' +
                ", custoItens=" + custoItens +
                ", imposto=" + String.format(Locale.US, "%.2f (%.0f%%)", imposto, aliquotaImpostoAplicada * 100) +
                ", aliquotaImpostoAplicada=" + aliquotaImpostoAplicada +
                ", desconto=" + desconto +
                ", custoConsumidor=" + custoConsumidor +
                ", efetivado=" + efetivado +
                '}';
    }
}