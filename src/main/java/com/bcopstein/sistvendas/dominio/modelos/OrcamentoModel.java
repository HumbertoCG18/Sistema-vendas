package com.bcopstein.sistvendas.dominio.modelos;

import com.bcopstein.sistvendas.dominio.servicos.impostos.CalculadorImpostoEstadualStrategy;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "orcamento")
public class OrcamentoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItemPedidoModel> itens;

    private BigDecimal custoItens;
    private BigDecimal impostoEstadual;
    private BigDecimal aliquotaImpostoEstadualAplicada;
    private BigDecimal impostoFederal;
    private BigDecimal valorDesconto;
    private BigDecimal custoConsumidor;
    private String paisCliente;
    private boolean efetivado;
    private String estadoCliente;
    private String nomeCliente;
    private LocalDate dataGeracao;

    @Transient // Para não ser persistido pelo JPA
    private CalculadorImpostoEstadualStrategy impostoStrategy;


    public OrcamentoModel() {
        this.itens = new LinkedList<>();
        this.efetivado = false;
        this.custoItens = BigDecimal.ZERO;
        this.impostoEstadual = BigDecimal.ZERO;
        this.aliquotaImpostoEstadualAplicada = BigDecimal.ZERO;
        this.impostoFederal = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
        this.custoConsumidor = BigDecimal.ZERO;
        this.dataGeracao = LocalDate.now();
    }

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = true) 
    private ClienteModel cliente;
    
    // Setter para injetar a estratégia
    public void setImpostoStrategy(CalculadorImpostoEstadualStrategy impostoStrategy) {
        this.impostoStrategy = impostoStrategy;
    }

    // Getters
    public ClienteModel getCliente() {return cliente;}
    public String getNomeClienteDoOrcamento() { return (this.cliente != null) ? this.cliente.getNomeCompleto() : null; }
    public LocalDate getDataGeracao() {return dataGeracao;}
    public String getNomeCliente() { return nomeCliente;}
    public BigDecimal getCustoItens() {return custoItens.setScale(2, RoundingMode.HALF_UP);}
    public BigDecimal getImpostoEstadual() {return impostoEstadual.setScale(2, RoundingMode.HALF_UP);}
    public BigDecimal getAliquotaImpostoEstadualAplicada() {return aliquotaImpostoEstadualAplicada;} 
    public BigDecimal getImpostoFederal() {return impostoFederal.setScale(2, RoundingMode.HALF_UP);}
    public BigDecimal getValorDesconto() {return valorDesconto.setScale(2, RoundingMode.HALF_UP);}
    public BigDecimal getCustoConsumidor() {return custoConsumidor.setScale(2, RoundingMode.HALF_UP);}
    public String getPaisCliente() {return paisCliente;}
    public String getEstadoCliente() { return estadoCliente;}
    public List<ItemPedidoModel> getItens() {return this.itens;}
    
    // Setters
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente;} 
    public void setDataGeracao(LocalDate dataGeracao) { this.dataGeracao = dataGeracao;}
    public void setCliente(ClienteModel cliente) {this.cliente = cliente;}
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente;}
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente;}
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public void setCustoItens(BigDecimal custoItens) { this.custoItens = custoItens;}
    public void setCustoConsumidor(BigDecimal custoConsumidor) { this.custoConsumidor = custoConsumidor;}
    public void setItens(List<ItemPedidoModel> itens) { this.itens = itens;}
    
    public boolean isVencido() {
        if (this.dataGeracao == null) {
            return true;
        }
        return LocalDate.now().isAfter(this.dataGeracao.plusDays(21));
    }

    public void addItensPedido(PedidoModel pedido) {
        if (pedido != null && pedido.getItens() != null) {
            for (ItemPedidoModel itemPedido : pedido.getItens()) {
                if (itemPedido != null && itemPedido.getProduto() != null) {
                    itemPedido.setOrcamento(this);
                    this.itens.add(itemPedido);
                }
            }
        }
    }

    public boolean removeItemPorProdutoId(long produtoId) {
        if (this.isEfetivado()) {
            return false;
        }
        boolean removido = this.itens.removeIf(item -> item.getProduto() != null && item.getProduto().getId() == produtoId);
        if (removido) {
            recalculaTotais();
        }
        return removido;
    }
    
    public void recalculaTotais() {
        if (this.isEfetivado()) {
            return;
        }

        this.custoItens = this.itens.stream()
                .map(item -> BigDecimal.valueOf(item.getProduto().getPrecoUnitario())
                        .multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // 2. Calcular Imposto Estadual e Alíquota via Strategy
        if (this.impostoStrategy != null) {
            this.impostoEstadual = this.impostoStrategy.calcular(this.itens).setScale(2, RoundingMode.HALF_UP);
            this.aliquotaImpostoEstadualAplicada = this.impostoStrategy.getAliguotaEfetiva(this.itens);
        } else {
             this.impostoEstadual = BigDecimal.ZERO;
             this.aliquotaImpostoEstadualAplicada = BigDecimal.ZERO;
        }

        // 3. Calcular Imposto Federal
        this.impostoFederal = this.custoItens.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);

        // 4. Calcular Descontos
        calcularDescontosInterno();
        this.valorDesconto = this.valorDesconto.setScale(2, RoundingMode.HALF_UP);

        // 5. Calcular Custo Final para o Consumidor
        this.custoConsumidor = this.custoItens
                .add(this.impostoEstadual)
                .add(this.impostoFederal)
                .subtract(this.valorDesconto)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void calcularDescontosInterno() {
        this.valorDesconto = BigDecimal.ZERO;
        BigDecimal descontoCalculado = BigDecimal.ZERO;

        if (this.itens != null) {
            for (ItemPedidoModel item : this.itens) {
                if (item.getQuantidade() >= 3) {
                    BigDecimal valorOriginalItem = BigDecimal.valueOf(item.getProduto().getPrecoUnitario())
                            .multiply(new BigDecimal(item.getQuantidade()));
                    descontoCalculado = descontoCalculado.add(valorOriginalItem.multiply(new BigDecimal("0.05")));
                }
            }
        }

        if (this.itens != null && this.itens.size() > 10) {
            descontoCalculado = descontoCalculado.add(this.custoItens.multiply(new BigDecimal("0.10")));
        }

        this.valorDesconto = descontoCalculado;
    }

    public boolean isEfetivado() {
        return efetivado;
    }

    public void efetiva() {
        if (!this.efetivado) {
            this.efetivado = true;
        }
    }

    @Override
    public String toString() {
        return "OrcamentoModel{" +
                "id=" + id +
                "nomeCliente='" + nomeCliente + '\'' +
                ", dataGeracao=" + dataGeracao +
                ", itens=" + (itens != null ? itens.size() : 0) + " itens" +
                "paisCliente='" + paisCliente + '\'' +
                ", estadoCliente='" + estadoCliente + '\'' +
                ", custoItens="
                + (custoItens != null ? custoItens.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00") +
                ", impostoEstadual="
                + (impostoEstadual != null ? impostoEstadual.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00")
                +
                " (aliquotaAplicada="
                + (aliquotaImpostoEstadualAplicada != null
                        ? aliquotaImpostoEstadualAplicada.multiply(new BigDecimal("100"))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString() + "%"
                        : "0.00%")
                + ")" +
                ", impostoFederal="
                + (impostoFederal != null ? impostoFederal.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00")
                + " (15.00%)" +
                ", valorDesconto="
                + (valorDesconto != null ? valorDesconto.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00") +
                ", custoConsumidor="
                + (custoConsumidor != null ? custoConsumidor.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00")
                +
                ", efetivado=" + efetivado +
                '}';
    }
}