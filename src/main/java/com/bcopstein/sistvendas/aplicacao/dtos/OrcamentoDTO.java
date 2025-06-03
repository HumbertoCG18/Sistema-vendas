package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Import Locale

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class OrcamentoDTO {
    private long id;
    private List<ItemPedidoDTO> itens;
    private List<Double> precosUnitariosItens;
    private double subTotal;
    private String imposto; // Changed from double to String
    private double desconto;
    private double custoConsumidor;
    private boolean efetivado;
    private String estadoCliente; 

    public OrcamentoDTO() {
        this.itens = new ArrayList<>();
        this.precosUnitariosItens = new ArrayList<>();
    }

    // Constructor updated for String imposto
    public OrcamentoDTO(long id, List<ItemPedidoDTO> itens, List<Double> precosUnitariosItens, double subTotal, 
                       String imposto, // Changed type
                       double desconto, double custoConsumidor, boolean efetivado, String estadoCliente) {
        this.id = id;
        this.itens = itens;
        this.precosUnitariosItens = precosUnitariosItens;
        this.subTotal = subTotal;
        this.imposto = imposto; // Assign String
        this.desconto = desconto;
        this.custoConsumidor = custoConsumidor;
        this.efetivado = efetivado;
        this.estadoCliente = estadoCliente;
    }

    // Getters
    public long getId() { return id; }
    public List<ItemPedidoDTO> getItens() { return itens; }
    public List<Double> getPrecosUnitariosItens() { return precosUnitariosItens; }
    public double getSubTotal() { return subTotal; }
    public String getImposto() { return imposto; } // Returns String
    public double getDesconto() { return desconto; }
    public double getCustoConsumidor() { return custoConsumidor; }
    public boolean isEfetivado() { return efetivado; }
    public String getEstadoCliente() { return estadoCliente; }


    public static OrcamentoDTO fromModel(OrcamentoModel orcamento) {
        if (orcamento == null) {
            System.err.println("OrcamentoDTO.fromModel: Tentativa de converter OrcamentoModel nulo.");
            return new OrcamentoDTO(0, new ArrayList<>(), new ArrayList<>(), 0, "0.00 (0%)",0,0, false, null);
        }

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();
        List<Double> precosUnitarios = new ArrayList<>();

        if (orcamento.getItens() != null) {
            for (ItemPedidoModel ip : orcamento.getItens()) {
                if (ip.getProduto() == null) {
                     System.err.println("OrcamentoDTO.fromModel: ItemPedidoModel com ProdutoModel nulo no orçamento ID: " + orcamento.getId());
                     continue;
                }
                itensDTO.add(ItemPedidoDTO.fromModel(ip)); 
                ProdutoModel produto = ip.getProduto();
                precosUnitarios.add(produto.getPrecoUnitario());
            }
        }

        // Format the imposto string
        double impostoValorReal = orcamento.getImposto();
        double aliquotaReal = orcamento.getAliquotaImpostoAplicada();
        // String.format uses the default locale for number formatting (e.g., decimal separator).
        // For JSON, it's often safer to use Locale.US to ensure '.' as decimal separator.
        String impostoFormatadoParaDTO = String.format(Locale.US, "%.2f (%.0f%%)", impostoValorReal, aliquotaReal * 100);


        return new OrcamentoDTO(
                orcamento.getId(),
                itensDTO,
                precosUnitarios,
                orcamento.getCustoItens(), // This is subTotal
                impostoFormatadoParaDTO,    // Pass the formatted string
                orcamento.getDesconto(),
                orcamento.getCustoConsumidor(),
                orcamento.isEfetivado(),
                orcamento.getEstadoCliente()
        );
    }
}