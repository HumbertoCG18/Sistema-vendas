package com.bcopstein.sistvendas.aplicacao.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class OrcamentoDTO {
    private long id;
    private List<ItemPedidoDTO> itens;
    private List<Double> precosUnitariosItens; // Mantido por enquanto, conforme original
    
    private BigDecimal subTotal; // Anteriormente double, agora BigDecimal (custoItens do modelo)

    private BigDecimal impostoEstadual; // Valor numérico do imposto estadual
    private String impostoEstadualFormatado; // String formatada: "Valor (Aliquota%)"
    
    private BigDecimal impostoFederal; // Valor numérico do imposto federal
    private String impostoFederalFormatado; // String formatada: "Valor (15.00%)"
    
    private BigDecimal valorDescontoTotal; // Total de descontos aplicados
    
    private BigDecimal custoConsumidor; // Custo final para o consumidor
    
    private boolean efetivado;
    private String estadoCliente; 

    // Construtor padrão para desserialização e inicialização
    public OrcamentoDTO() {
        this.itens = new ArrayList<>();
        this.precosUnitariosItens = new ArrayList<>();
        this.subTotal = BigDecimal.ZERO;
        this.impostoEstadual = BigDecimal.ZERO;
        this.impostoEstadualFormatado = "0.00 (0.00%)";
        this.impostoFederal = BigDecimal.ZERO;
        this.impostoFederalFormatado = "0.00 (0.00%)";
        this.valorDescontoTotal = BigDecimal.ZERO;
        this.custoConsumidor = BigDecimal.ZERO;
        this.efetivado = false;
    }

    // Construtor completo atualizado
    public OrcamentoDTO(long id, List<ItemPedidoDTO> itens, List<Double> precosUnitariosItens, 
                       BigDecimal subTotal, 
                       BigDecimal impostoEstadual, String impostoEstadualFormatado,
                       BigDecimal impostoFederal, String impostoFederalFormatado,
                       BigDecimal valorDescontoTotal,
                       BigDecimal custoConsumidor, boolean efetivado, String estadoCliente) {
        this.id = id;
        this.itens = itens != null ? itens : new ArrayList<>();
        this.precosUnitariosItens = precosUnitariosItens != null ? precosUnitariosItens : new ArrayList<>();
        this.subTotal = subTotal;
        this.impostoEstadual = impostoEstadual;
        this.impostoEstadualFormatado = impostoEstadualFormatado;
        this.impostoFederal = impostoFederal;
        this.impostoFederalFormatado = impostoFederalFormatado;
        this.valorDescontoTotal = valorDescontoTotal;
        this.custoConsumidor = custoConsumidor;
        this.efetivado = efetivado;
        this.estadoCliente = estadoCliente;
    }

    // Getters atualizados
    public long getId() { return id; }
    public List<ItemPedidoDTO> getItens() { return itens; }
    public List<Double> getPrecosUnitariosItens() { return precosUnitariosItens; }
    public BigDecimal getSubTotal() { return subTotal; }
    public BigDecimal getImpostoEstadual() { return impostoEstadual; }
    public String getImpostoEstadualFormatado() { return impostoEstadualFormatado; }
    public BigDecimal getImpostoFederal() { return impostoFederal; }
    public String getImpostoFederalFormatado() { return impostoFederalFormatado; }
    public BigDecimal getValorDescontoTotal() { return valorDescontoTotal; }
    public BigDecimal getCustoConsumidor() { return custoConsumidor; }
    public boolean isEfetivado() { return efetivado; }
    public String getEstadoCliente() { return estadoCliente; }

    // Setters (úteis para construção ou frameworks de mapeamento)
    public void setId(long id) { this.id = id; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
    public void setPrecosUnitariosItens(List<Double> precosUnitariosItens) { this.precosUnitariosItens = precosUnitariosItens; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
    public void setImpostoEstadual(BigDecimal impostoEstadual) { this.impostoEstadual = impostoEstadual; }
    public void setImpostoEstadualFormatado(String impostoEstadualFormatado) { this.impostoEstadualFormatado = impostoEstadualFormatado; }
    public void setImpostoFederal(BigDecimal impostoFederal) { this.impostoFederal = impostoFederal; }
    public void setImpostoFederalFormatado(String impostoFederalFormatado) { this.impostoFederalFormatado = impostoFederalFormatado; }
    public void setValorDescontoTotal(BigDecimal valorDescontoTotal) { this.valorDescontoTotal = valorDescontoTotal; }
    public void setCustoConsumidor(BigDecimal custoConsumidor) { this.custoConsumidor = custoConsumidor; }
    public void setEfetivado(boolean efetivado) { this.efetivado = efetivado; }
    public void setEstadoCliente(String estadoCliente) { this.estadoCliente = estadoCliente; }


    public static OrcamentoDTO fromModel(OrcamentoModel orcamento) {
        if (orcamento == null) {
            System.err.println("OrcamentoDTO.fromModel: Tentativa de converter OrcamentoModel nulo.");
            // Retorna um DTO com valores padrão para evitar NullPointerExceptions no consumidor
            OrcamentoDTO dtoPadrao = new OrcamentoDTO();
            dtoPadrao.setId(0);
            // Outros campos já estão com valores padrão do construtor OrcamentoDTO()
            return dtoPadrao;
        }

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();
        List<Double> precosUnitarios = new ArrayList<>(); // Mantendo a estrutura original do DTO

        if (orcamento.getItens() != null) {
            for (ItemPedidoModel ip : orcamento.getItens()) {
                if (ip.getProduto() == null) {
                     System.err.println("OrcamentoDTO.fromModel: ItemPedidoModel com ProdutoModel nulo no orçamento ID: " + orcamento.getId());
                     continue; // Pula este item problemático
                }
                itensDTO.add(ItemPedidoDTO.fromModel(ip)); 
                ProdutoModel produto = ip.getProduto();
                // Adiciona o preço unitário original do produto à lista
                precosUnitarios.add(produto.getPrecoUnitario()); 
            }
        }

        // Os getters em OrcamentoModel já retornam BigDecimal arredondado para 2 casas decimais
        BigDecimal subTotalModel = orcamento.getCustoItens();
        BigDecimal impostoEstadualModel = orcamento.getImpostoEstadual();
        BigDecimal aliquotaEstadualModel = orcamento.getAliquotaImpostoEstadualAplicada(); // Esta não é arredondada
        BigDecimal impostoFederalModel = orcamento.getImpostoFederal();
        BigDecimal valorDescontoTotalModel = orcamento.getValorDesconto();
        BigDecimal custoConsumidorModel = orcamento.getCustoConsumidor();

        String impostoEstadualFmt = String.format(Locale.US, "%.2f (%.2f%%)", 
                                                  impostoEstadualModel, 
                                                  aliquotaEstadualModel.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
        
        // Imposto federal tem alíquota fixa de 15%
        String impostoFederalFmt = String.format(Locale.US, "%.2f (15.00%%)", 
                                                 impostoFederalModel);

        return new OrcamentoDTO(
                orcamento.getId(),
                itensDTO,
                precosUnitarios,
                subTotalModel,
                impostoEstadualModel,
                impostoEstadualFmt,
                impostoFederalModel,
                impostoFederalFmt,
                valorDescontoTotalModel,
                custoConsumidorModel,
                orcamento.isEfetivado(),
                orcamento.getEstadoCliente()
        );
    }
}