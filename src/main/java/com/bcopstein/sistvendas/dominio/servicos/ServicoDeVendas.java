package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
import java.util.Arrays; // Para a lista de locais
import java.util.Map;    // Para estrutura de locais atendidos
import java.util.HashMap; // Para estrutura de locais atendidos

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

    // Definição de locais atendidos
    // No futuro, isso pode vir de um arquivo de configuração ou banco de dados
    private static final Map<String, List<String>> LOCAIS_ATENDIDOS;
    static {
        LOCAIS_ATENDIDOS = new HashMap<>();
        // Locais atendidos conforme Anexo I do PDF para o Brasil
        LOCAIS_ATENDIDOS.put("BRASIL", Arrays.asList("RS", "SP", "PE")); 
        // Exemplo de como adicionar outros países/regiões:
        // LOCAIS_ATENDIDOS.put("ARGENTINA", Arrays.asList("BUENOS AIRES", "CORDOBA"));
    }

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
    // Assinatura do método atualizada para incluir paisCliente
    public OrcamentoModel criaOrcamento(PedidoModel pedido, String estadoCliente, String paisCliente) { 
        // Validações de entrada do pedido
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

        // Validações de entrada para país e estado
        if (paisCliente == null || paisCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("País do cliente não informado.");
        }
        if (estadoCliente == null || estadoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado do cliente não informado.");
        }

        // Validação de local atendido
            String paisUpper = paisCliente.trim().toUpperCase();
            String estadoUpper = estadoCliente.trim().toUpperCase();

            List<String> estadosAtendidosNoPais = LOCAIS_ATENDIDOS.get(paisUpper);

            if (estadosAtendidosNoPais == null || !estadosAtendidosNoPais.contains(estadoUpper)) {
                throw new IllegalArgumentException("Local de entrega não atendido: País '" + paisCliente + "', Estado '" + estadoCliente + "'.");
            }

        // Criação e configuração do orçamento
        OrcamentoModel novoOrcamento = new OrcamentoModel();
        novoOrcamento.setEstadoCliente(estadoCliente.trim()); 
        novoOrcamento.setPaisCliente(paisCliente.trim());     
        novoOrcamento.addItensPedido(pedido); 

        novoOrcamento.recalculaTotais();

        return this.orcamentosRepo.save(novoOrcamento); 
    }

    @Transactional
    public OrcamentoModel efetivaOrcamento(long id) {
        OrcamentoModel orcamento = this.orcamentosRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento ID " + id + " não encontrado para efetivação."));

        if (orcamento.isEfetivado()) {
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " já está efetivado.");
            return orcamento;
        }

        // Verificação de validade (21 dias)
        if (orcamento.isVencido()) { //
            throw new IllegalStateException("Orçamento ID " + id + " está vencido e não pode mais ser efetivado. Data Geração: " + orcamento.getDataGeracao());
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
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " efetivado com sucesso.");
        } else {
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " NÃO efetivado (itens indisponíveis ou erro).");
            // Considerar lançar uma exceção aqui para indicar falha na efetivação por falta de estoque
            throw new IllegalStateException("Orçamento ID " + id + " não pode ser efetivado devido à falta de estoque para um ou mais itens.");
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
                    orcamentosRepo.save(orcamento); 
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
        orcamentosRepo.deleteById(orcamentoId); 
        return true;
    }
}