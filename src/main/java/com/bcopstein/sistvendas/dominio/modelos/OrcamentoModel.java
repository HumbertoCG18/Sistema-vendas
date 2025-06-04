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
    @JsonManagedReference // Para evitar recursão infinita no JSON
    private List<ItemPedidoModel> itens;

    private BigDecimal custoItens; // Subtotal dos produtos
    private BigDecimal impostoEstadual;
    private BigDecimal aliquotaImpostoEstadualAplicada; // Alíquota efetivamente usada
    private BigDecimal impostoFederal;
    private BigDecimal valorDesconto; // Soma de todos os descontos aplicáveis
    private BigDecimal custoConsumidor; // Custo final
    private String paisCliente;
    private boolean efetivado;
    private String estadoCliente;
    private String nomeCliente; // NOVO ATRIBUTO
    private LocalDate dataGeracao;

    // Construtor padrão (sem argumentos) - Usado pelo JPA e pelo código
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

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public String getNomeCliente() {
        return nomeCliente;
    } // NOVO GETTER

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    } // NOVO SETTER

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public boolean isVencido() {
        if (this.dataGeracao == null) {
            return true; // Se não tem data de geração, considera-se inválido/vencido por precaução
        }
        // Os orçamentos possuem validade de 21 dias a partir da sua geração
        return LocalDate.now().isAfter(this.dataGeracao.plusDays(21));
    }

    // Setters
    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }

    public void setPaisCliente(String paisCliente) {
        this.paisCliente = paisCliente;
    } // NOVO SETTER

    // Getters para os campos BigDecimal (arredondados para exibição ou uso)
    public BigDecimal getCustoItens() {
        return custoItens.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getImpostoEstadual() {
        return impostoEstadual.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAliquotaImpostoEstadualAplicada() {
        return aliquotaImpostoEstadualAplicada;
    } // Geralmente não é arredondada

    public BigDecimal getImpostoFederal() {
        return impostoFederal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCustoConsumidor() {
        return custoConsumidor.setScale(2, RoundingMode.HALF_UP);
    }

    public String getPaisCliente() {
        return paisCliente;
    } // NOVO GETTER

    public String getEstadoCliente() {
        return estadoCliente;
    }

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

    public List<ItemPedidoModel> getItens() {
        return this.itens;
    }

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

    public void recalculaTotais() {
        if (this.isEfetivado()) {
            return; // Não recalcular se já efetivado
        }

        // 1. Calcular Custo dos Itens (Subtotal)
        this.custoItens = BigDecimal.ZERO;
        if (this.itens != null) {
            for (ItemPedidoModel item : this.itens) {
                if (item.getProduto() != null && item.getProduto().getPrecoUnitario() >= 0
                        && item.getQuantidade() >= 0) {
                    BigDecimal precoUnitario = BigDecimal.valueOf(item.getProduto().getPrecoUnitario());
                    BigDecimal quantidade = new BigDecimal(item.getQuantidade());
                    this.custoItens = this.custoItens.add(precoUnitario.multiply(quantidade));
                }
            }
        }
        this.custoItens = this.custoItens.setScale(2, RoundingMode.HALF_UP);

        // 2. Calcular Imposto Estadual
        calcularImpostoEstadualInterno();
        this.impostoEstadual = this.impostoEstadual.setScale(2, RoundingMode.HALF_UP);

        // 3. Calcular Imposto Federal
        // Alíquota única de 15% sobre o valor total do orçamento (custoItens)
        this.impostoFederal = this.custoItens.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);

        // 4. Calcular Descontos (Impostos são calculados ANTES dos descontos)
        calcularDescontosInterno();
        this.valorDesconto = this.valorDesconto.setScale(2, RoundingMode.HALF_UP);

        // 5. Calcular Custo Final para o Consumidor
        this.custoConsumidor = this.custoItens
                .add(this.impostoEstadual)
                .add(this.impostoFederal)
                .subtract(this.valorDesconto);
        this.custoConsumidor = this.custoConsumidor.setScale(2, RoundingMode.HALF_UP);
    }

    private void calcularImpostoEstadualInterno() {
        this.impostoEstadual = BigDecimal.ZERO;
        this.aliquotaImpostoEstadualAplicada = BigDecimal.ZERO; // Representa a alíquota média ou base

        if (this.estadoCliente == null || this.estadoCliente.trim().isEmpty()) {
            // Na Fase 2, isso deve ser tratado como erro de validação antes de chegar aqui.
            // Por ora, imposto zero se estado não informado.
            return;
        }

        String estadoUpper = this.estadoCliente.trim().toUpperCase();
        BigDecimal cem = new BigDecimal("100.00");

        switch (estadoUpper) {
            case "RS": // Rio Grande do Sul
                // Orçamentos de menos de R$ 100,00 são isentos.
                // Para orçamentos maiores que R$ 100,00 a alíquota é de 10% sobre o valor que
                // ultrapassa R$ 100,00.
                if (this.custoItens.compareTo(cem) >= 0) {
                    this.aliquotaImpostoEstadualAplicada = new BigDecimal("0.10");
                    this.impostoEstadual = (this.custoItens.subtract(cem))
                            .multiply(this.aliquotaImpostoEstadualAplicada);
                } else {
                    this.impostoEstadual = BigDecimal.ZERO; // Isento
                    this.aliquotaImpostoEstadualAplicada = BigDecimal.ZERO;
                }
                break;
            case "SP": // São Paulo
                // Alíquota única de 12%.
                this.aliquotaImpostoEstadualAplicada = new BigDecimal("0.12");
                this.impostoEstadual = this.custoItens.multiply(this.aliquotaImpostoEstadualAplicada);
                break;
            case "PE": // Pernambuco
                // Alíquota única de 15% sobre todos os produtos menos aqueles considerados
                // essenciais que tem uma alíquota de 5%.
                // Produtos essenciais tem um "*" ao final da descrição do produto.
                BigDecimal impostoEssenciais = BigDecimal.ZERO;
                BigDecimal impostoNaoEssenciais = BigDecimal.ZERO;
                BigDecimal aliquotaEssenciaisPE = new BigDecimal("0.05");
                BigDecimal aliquotaNaoEssenciaisPE = new BigDecimal("0.15");

                if (this.itens != null) {
                    for (ItemPedidoModel item : this.itens) {
                        ProdutoModel produto = item.getProduto();
                        BigDecimal precoUnit = BigDecimal.valueOf(produto.getPrecoUnitario());
                        BigDecimal qtd = new BigDecimal(item.getQuantidade());
                        BigDecimal valorItem = precoUnit.multiply(qtd);

                        if (produto.getDescricao().endsWith("*")) { // Produto essencial
                            impostoEssenciais = impostoEssenciais.add(valorItem.multiply(aliquotaEssenciaisPE));
                        } else {
                            impostoNaoEssenciais = impostoNaoEssenciais
                                    .add(valorItem.multiply(aliquotaNaoEssenciaisPE));
                        }
                    }
                }
                this.impostoEstadual = impostoEssenciais.add(impostoNaoEssenciais);

                // Calcular alíquota média ponderada para registro, se custoItens > 0
                if (this.custoItens.compareTo(BigDecimal.ZERO) > 0) {
                    this.aliquotaImpostoEstadualAplicada = this.impostoEstadual.divide(this.custoItens, 4,
                            RoundingMode.HALF_UP);
                } else {
                    this.aliquotaImpostoEstadualAplicada = BigDecimal.ZERO; // Ou uma base, ex: aliquotaNaoEssenciaisPE
                }
                break;
            default:
                // Para estados não listados, imposto estadual é zero.
                // A recusa do pedido por local não atendido será na Fase 2.
                this.impostoEstadual = BigDecimal.ZERO;
                this.aliquotaImpostoEstadualAplicada = BigDecimal.ZERO;
                throw new IllegalArgumentException(
                        "Estado '" + this.estadoCliente + "' não atendido ou sem regra de imposto definida.");
        }
        // Garante que imposto não seja negativo
        if (this.impostoEstadual.compareTo(BigDecimal.ZERO) < 0) {
            this.impostoEstadual = BigDecimal.ZERO;
        }
    }

    private void calcularDescontosInterno() {
        this.valorDesconto = BigDecimal.ZERO; // Inicializa o campo da classe
        BigDecimal descontoCalculado = BigDecimal.ZERO; // Acumulador local

        System.out.println("DEBUG: Entrando em calcularDescontosInterno()");
        System.out.println("DEBUG: CustoItens ANTES dos descontos: " + this.custoItens.toPlainString());

        // 1. Desconto por item cuja quantidade solicitada seja superior a três unidades
        // (5%)
        if (this.itens != null) {
            System.out.println("DEBUG: Verificando desconto por item. Número de itens na lista: " + this.itens.size());
            for (ItemPedidoModel item : this.itens) {
                System.out.println("DEBUG: Processando item ID: " + item.getProduto().getId() + ", Quantidade: "
                        + item.getQuantidade());
                if (item.getQuantidade() >= 3) {
                    System.out.println("DEBUG: CONDIÇÃO DE DESCONTO POR ITEM ATENDIDA para produto ID: "
                            + item.getProduto().getId());
                    BigDecimal precoUnitario = BigDecimal.valueOf(item.getProduto().getPrecoUnitario());
                    BigDecimal quantidade = new BigDecimal(item.getQuantidade());
                    BigDecimal valorOriginalItem = precoUnitario.multiply(quantidade);
                    BigDecimal descontoDoItem = valorOriginalItem.multiply(new BigDecimal("0.05"));
                    descontoCalculado = descontoCalculado.add(descontoDoItem);
                    System.out.println("DEBUG: Desconto para este item: " + descontoDoItem.toPlainString()
                            + ", Desconto Acumulado: " + descontoCalculado.toPlainString());
                } else {
                    System.out.println("DEBUG: CONDIÇÃO DE DESCONTO POR ITEM NÃO ATENDIDA para produto ID: "
                            + item.getProduto().getId());
                }
            }
        } else {
            System.out.println("DEBUG: Lista de itens é nula. Nenhum desconto por item a ser calculado.");
        }

        // 2. Desconto sobre o valor total do orçamento quando este contiver mais de dez
        // itens (10%)
        System.out.println("DEBUG: Verificando desconto por volume. Número de linhas de itens: "
                + (this.itens != null ? this.itens.size() : "N/A - lista nula"));
        if (this.itens != null && this.itens.size() > 10) {
            System.out.println(
                    "DEBUG: CONDIÇÃO DE DESCONTO POR VOLUME ATENDIDA. CustoItens: " + this.custoItens.toPlainString());
            BigDecimal descontoPorVolume = this.custoItens.multiply(new BigDecimal("0.10"));
            descontoCalculado = descontoCalculado.add(descontoPorVolume);
            System.out.println("DEBUG: Desconto por volume: " + descontoPorVolume.toPlainString()
                    + ", Desconto Acumulado Total: " + descontoCalculado.toPlainString());
        } else {
            System.out.println("DEBUG: CONDIÇÃO DE DESCONTO POR VOLUME NÃO ATENDIDA.");
        }

        this.valorDesconto = descontoCalculado;
        System.out.println("DEBUG: Saindo de calcularDescontosInterno(). ValorDesconto FINAL: "
                + this.valorDesconto.toPlainString());
    }

    // Getters e Setters básicos
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCustoItens(BigDecimal custoItens) {
        this.custoItens = custoItens;
    } // Para JPA e testes

    public void setCustoConsumidor(BigDecimal custoConsumidor) {
        this.custoConsumidor = custoConsumidor;
    } // Para JPA e testes

    public boolean isEfetivado() {
        return efetivado;
    }

    public void efetiva() {
        if (!this.efetivado) {
            this.efetivado = true;
            // Não recalcular totais após efetivação
        }
    }

    public void setItens(List<ItemPedidoModel> itens) {
        this.itens = itens;
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
                + " (15.00%)" + // Imposto Federal é 15% [cite: 82]
                ", valorDesconto="
                + (valorDesconto != null ? valorDesconto.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00") +
                ", custoConsumidor="
                + (custoConsumidor != null ? custoConsumidor.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00")
                +
                ", efetivado=" + efetivado +
                '}';
    }
}