package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

import com.bcopstein.sistvendas.aplicacao.dtos.ItemPedidoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
// Novo DTO para request não é necessário aqui se o Controller passa os N parâmetros.
// Mas se o Controller usar NovoOrcamentoRequestDTO, então este UC o receberia.
// For simplicity with the current structure, let's assume Controller extracts and passes.
// OR, let this UC take the NovoOrcamentoRequestDTO. Let's update it to take the DTO.
import com.bcopstein.sistvendas.aplicacao.dtos.NovoOrcamentoRequestDTO;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;

@Component
public class CriaOrcamentoUC {
    private ServicoDeVendas servicoDeVendas;
    private ServicoDeEstoque servicoDeEstoque;
    
    @Autowired
    public CriaOrcamentoUC(ServicoDeVendas servicoDeVendas,ServicoDeEstoque servicoDeEstoque){
        this.servicoDeVendas = servicoDeVendas;
        this.servicoDeEstoque = servicoDeEstoque;
    }

    // Modified to accept NovoOrcamentoRequestDTO
    public OrcamentoDTO run(NovoOrcamentoRequestDTO request){
        List<ItemPedidoDTO> itens = request.getItens();
        String estadoCliente = request.getEstadoCliente();

        // System.out.println("CriaOrcamentoUC: Recebendo itens para orçamento: " + itens + ", Estado: " + estadoCliente);
        PedidoModel pedido = new PedidoModel(0); // Pedido ID is transient here
        
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A lista de itens não pode ser vazia para criar um orçamento.");
        }

        for(ItemPedidoDTO item:itens){
            // System.out.println("CriaOrcamentoUC: Buscando produto com ID: " + item.getIdProduto());
            ProdutoModel produto = servicoDeEstoque.produtoPorCodigo(item.getIdProduto());
            // System.out.println("CriaOrcamentoUC: Produto encontrado: " + (produto != null ? produto.getId() + " - " + produto.getDescricao() : "NULL"));
            
            if (produto == null) {
                throw new RuntimeException("Produto com ID " + item.getIdProduto() + " não encontrado");
            }
            if (item.getQtdade() <= 0) {
                throw new IllegalArgumentException("Quantidade para o produto ID " + item.getIdProduto() + " deve ser positiva.");
            }
            
            ItemPedidoModel itemPedido = new ItemPedidoModel(produto, item.getQtdade());
            pedido.addItem(itemPedido);
        }
        
        // System.out.println("CriaOrcamentoUC: Pedido criado com " + pedido.getItens().size() + " item(ns)");
        OrcamentoModel orcamento = servicoDeVendas.criaOrcamento(pedido, estadoCliente); // Pass estadoCliente
        return OrcamentoDTO.fromModel(orcamento);
    }
}