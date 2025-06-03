package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
//import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;

@Service
public class ServicoDeVendas {
    private IOrcamentoRepositorio orcamentosRepo;
    private ServicoDeEstoque servicoDeEstoque; // Injeta ServicoDeEstoque

    @Autowired
    public ServicoDeVendas(IOrcamentoRepositorio orcamentos, ServicoDeEstoque servicoDeEstoque) {
        this.orcamentosRepo = orcamentos;
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public List<ProdutoModel> produtosDisponiveis() {
        return servicoDeEstoque.produtosDisponiveis();
    }

    public OrcamentoModel recuperaOrcamentoPorId(long id) {
        return this.orcamentosRepo.findById(id).orElse(null);
    }

    @Transactional
    public OrcamentoModel criaOrcamento(PedidoModel pedido, String estadoCliente) {
        if (pedido == null || pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido inválido: não pode ser nulo ou vazio.");
        }
        for (ItemPedidoModel item : pedido.getItens()) {
            if (item.getProduto() == null) {
                throw new IllegalArgumentException("Item de pedido inválido: produto não pode ser nulo.");
            }
            if (item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Item de pedido inválido: quantidade deve ser positiva.");
            }
        }

        OrcamentoModel novoOrcamento = new OrcamentoModel();
        novoOrcamento.setEstadoCliente(estadoCliente);
        novoOrcamento.addItensPedido(pedido); // addItensPedido now sets the orcamento link
        novoOrcamento.recalculaTotais();

        return this.orcamentosRepo.save(novoOrcamento); // Usa save do JpaRepository
    }

    @Transactional
    public OrcamentoModel efetivaOrcamento(long id) {
        OrcamentoModel orcamento = this.orcamentosRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento ID " + id + " não encontrado para efetivação."));

        if (orcamento.isEfetivado()) {
            return orcamento;
        }

        if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            throw new IllegalStateException("Orçamento ID " + id + " não pode ser efetivado pois não contém itens.");
        }

        boolean todosDisponiveis = true;
        for (var item : orcamento.getItens()) {
            if (item.getProduto() == null) {
                todosDisponiveis = false;
                break;
            }
            int quantidadeEmEstoque = servicoDeEstoque.qtdadeEmEstoque(item.getProduto().getId());
            if (quantidadeEmEstoque < item.getQuantidade()) {
                todosDisponiveis = false;
                break;
            }
        }

        if (todosDisponiveis) {
            for (var item : orcamento.getItens()) {
                servicoDeEstoque.baixaEstoque(item.getProduto().getId(), item.getQuantidade());
            }
            orcamento.efetiva();
            orcamentosRepo.save(orcamento); // Salva o orçamento atualizado
        } else {
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " NÃO efetivado (itens indisponíveis ou erro).");
            // Não salva, mas retorna o estado atual (não efetivado)
        }
        return orcamento;
    }

    public List<OrcamentoModel> ultimosOrcamentosEfetivados(int n) {
        return this.orcamentosRepo.findByEfetivadoIsTrueOrderByIdDesc(PageRequest.of(0, n));
    }

    @Transactional
    public void atualizarOrcamentosAposRemocaoProduto(long produtoIdRemovido) {
        List<OrcamentoModel> todosOrcamentos = orcamentosRepo.findAll();
        for (OrcamentoModel orcamento : todosOrcamentos) {
            if (!orcamento.isEfetivado()) {
                boolean itemFoiRemovido = orcamento.removeItemPorProdutoId(produtoIdRemovido);
                if (itemFoiRemovido) {
                    orcamentosRepo.save(orcamento); // Salva o orçamento modificado
                }
            }
        }
    }

    @Transactional
    public boolean removerOrcamento(long orcamentoId) {
        OrcamentoModel orcamentoExistente = orcamentosRepo.findById(orcamentoId).orElse(null);
        if (orcamentoExistente == null) {
            return false;
        }
        orcamentosRepo.deleteById(orcamentoId); // Usa deleteById do JpaRepository
        return true;
    }
}