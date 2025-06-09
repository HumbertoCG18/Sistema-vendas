package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

import com.bcopstein.sistvendas.aplicacao.dtos.ItemPedidoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
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
    public CriaOrcamentoUC(ServicoDeVendas servicoDeVendas, ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeVendas = servicoDeVendas;
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public OrcamentoDTO run(NovoOrcamentoRequestDTO request) { 
        List<ItemPedidoDTO> itens = request.getItens();
        String nomeCliente = request.getNomeCliente();
        String cpfCliente = request.getCpfCliente();
        String emailCliente = request.getEmailCliente(); 
        String estadoCliente = request.getEstadoCliente();
        String paisCliente = request.getPaisCliente();

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A lista de itens não pode ser vazia para criar um orçamento.");
        }

        PedidoModel pedido = new PedidoModel(0);
        for (ItemPedidoDTO item : itens) {
            ProdutoModel produto = servicoDeEstoque.produtoPorCodigo(item.getIdProduto());
            if (produto == null) {
                throw new RuntimeException("Produto com ID " + item.getIdProduto() + " não encontrado");
            }
            if (item.getQtdade() <= 0) {
                throw new IllegalArgumentException(
                        "Quantidade para o produto ID " + item.getIdProduto() + " deve ser positiva.");
            }
            ItemPedidoModel itemPedido = new ItemPedidoModel(produto, item.getQtdade());
            pedido.addItem(itemPedido);
        }

        // Envia os novos dados do cliente para o serviço
        OrcamentoModel orcamento = servicoDeVendas.criaOrcamento(pedido, estadoCliente, paisCliente, nomeCliente,
                cpfCliente, emailCliente);
        return OrcamentoDTO.fromModel(orcamento);
    }
}