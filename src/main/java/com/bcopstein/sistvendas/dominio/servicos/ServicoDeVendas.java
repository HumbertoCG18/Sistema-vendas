package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays; // Para a lista de locais
import java.util.Map;    // Para estrutura de locais atendidos
import java.util.HashMap; // Para estrutura de locais atendidos
import java.time.LocalDate; // Adicionar import
import java.math.BigDecimal; // Adicionar import
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;
import com.bcopstein.sistvendas.aplicacao.dtos.ItemCompradoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.PerfilClienteDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.TaxaConversaoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.VendaProdutoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.VolumeVendasDTO; // Adicionar import


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

        public PerfilClienteDTO gerarPerfilCliente(String nomeCliente, LocalDate dataInicial, LocalDate dataFinal) {
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }

        List<OrcamentoModel> orcamentosCliente;
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.isAfter(dataFinal)) {
                throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
            }
            orcamentosCliente = orcamentosRepo.findByNomeClienteAndEfetivadoIsTrueAndDataGeracaoBetweenOrderByDataGeracaoDesc(
                nomeCliente, dataInicial, dataFinal);
        } else {
            orcamentosCliente = orcamentosRepo.findByNomeClienteAndEfetivadoIsTrueOrderByDataGeracaoDesc(nomeCliente);
        }

        BigDecimal totalGastoPeloCliente = BigDecimal.ZERO;
        Map<Long, ItemCompradoDTO> itensAgregados = new HashMap<>();

        for (OrcamentoModel orcamento : orcamentosCliente) {
            totalGastoPeloCliente = totalGastoPeloCliente.add(orcamento.getCustoConsumidor());
            for (ItemPedidoModel item : orcamento.getItens()) {
                ProdutoModel produto = item.getProduto();
                if (produto == null) continue;

                long idProduto = produto.getId();
                BigDecimal valorDoItemNoOrcamento = BigDecimal.valueOf(produto.getPrecoUnitario())
                                                        .multiply(new BigDecimal(item.getQuantidade()))
                                                        .setScale(2, RoundingMode.HALF_UP);

                ItemCompradoDTO itemComprado = itensAgregados.get(idProduto);
                if (itemComprado == null) {
                    itensAgregados.put(idProduto, new ItemCompradoDTO(
                        idProduto,
                        produto.getDescricao(),
                        item.getQuantidade(),
                        valorDoItemNoOrcamento
                    ));
                } else {
                    itensAgregados.put(idProduto, new ItemCompradoDTO(
                        idProduto,
                        produto.getDescricao(),
                        itemComprado.getQuantidadeTotalComprada() + item.getQuantidade(),
                        itemComprado.getValorTotalGastoNoProduto().add(valorDoItemNoOrcamento)
                    ));
                }
            }
        }
        return new PerfilClienteDTO(nomeCliente, dataInicial, dataFinal,
                                    totalGastoPeloCliente.setScale(2, RoundingMode.HALF_UP),
                                    orcamentosCliente.size(),
                                    new ArrayList<>(itensAgregados.values()));
    }

    public VolumeVendasDTO calcularVolumeVendasPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Data inicial e data final são obrigatórias.");
        }
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
        }

        List<OrcamentoModel> orcamentosNoPeriodo = orcamentosEfetivadosPorPeriodo(dataInicial, dataFinal);

        BigDecimal totalVendas = BigDecimal.ZERO;
        for (OrcamentoModel orcamento : orcamentosNoPeriodo) {
            // Usar o getter que já retorna BigDecimal escalado
            totalVendas = totalVendas.add(orcamento.getCustoConsumidor());
        }

        return new VolumeVendasDTO(dataInicial, dataFinal, totalVendas, orcamentosNoPeriodo.size());
    }
    
    public List<VendaProdutoDTO> calcularTotalVendasPorProduto(LocalDate dataInicial, LocalDate dataFinal) {
        List<OrcamentoModel> orcamentosConsiderados;
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.isAfter(dataFinal)) {
                throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
            }
            orcamentosConsiderados = orcamentosEfetivadosPorPeriodo(dataInicial, dataFinal);
        } else {
            // Se não fornecer período, considera todos os orçamentos efetivados
            // Para isso, precisaríamos de um método no repositório como:
            // List<OrcamentoModel> findByEfetivadoIsTrueOrderByIdDesc();
            // ou adaptar o existente com Pageable muito grande.
            // Por simplicidade, vamos exigir o período por enquanto, ou você pode
            // buscar todos usando orcamentosRepo.findAll() e filtrar por efetivado.
            // Vamos assumir que o período é obrigatório para esta primeira versão.
            if (dataInicial == null || dataFinal == null) {
                // Ou buscar todos os efetivados se essa for a intenção quando as datas são nulas.
                // Para este exemplo, vamos tornar as datas obrigatórias para esta consulta,
                // alinhando com a consulta de volume de vendas.
                throw new IllegalArgumentException("Data inicial e data final são obrigatórias para esta consulta.");
            }
            // Esta linha não será alcançada se a validação acima estiver ativa.
            orcamentosConsiderados = new ArrayList<>(); // Placeholder
        }

        Map<Long, VendaProdutoDTO> vendasPorProdutoMap = new HashMap<>();

        for (OrcamentoModel orcamento : orcamentosConsiderados) {
            for (ItemPedidoModel item : orcamento.getItens()) {
                ProdutoModel produto = item.getProduto();
                if (produto == null)
                    continue;

                long idProduto = produto.getId();
                String descricao = produto.getDescricao();
                int quantidadeVendidaItem = item.getQuantidade();

                // O valor do item é (preço unitário do produto * quantidade)
                // O preço unitário é o que estava no ProdutoModel associado ao ItemPedidoModel
                // No nosso modelo atual, ItemPedidoModel tem ProdutoModel, que tem o preço.
                BigDecimal valorItem = BigDecimal.valueOf(produto.getPrecoUnitario())
                        .multiply(new BigDecimal(quantidadeVendidaItem))
                        .setScale(2, RoundingMode.HALF_UP);

                VendaProdutoDTO dto = vendasPorProdutoMap.get(idProduto);
                if (dto == null) {
                    dto = new VendaProdutoDTO(idProduto, descricao, quantidadeVendidaItem, valorItem);
                } else {
                    dto = new VendaProdutoDTO(
                            idProduto,
                            descricao,
                            dto.getQuantidadeTotalVendida() + quantidadeVendidaItem,
                            dto.getValorTotalArrecadado().add(valorItem));
                }
                vendasPorProdutoMap.put(idProduto, dto);
            }
        }
        return new ArrayList<>(vendasPorProdutoMap.values());
    }
    
        public TaxaConversaoDTO calcularTaxaConversaoPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Data inicial e data final são obrigatórias.");
        }
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
        }

        long totalCriados = orcamentosRepo.countByDataGeracaoBetween(dataInicial, dataFinal);
        long totalEfetivados = orcamentosRepo.countByEfetivadoIsTrueAndDataGeracaoBetween(dataInicial, dataFinal);
        
        return new TaxaConversaoDTO(dataInicial, dataFinal, totalCriados, totalEfetivados);
    }

    @Transactional
    // Assinatura do método atualizada para incluir paisCliente
    public OrcamentoModel criaOrcamento(PedidoModel pedido, String estadoCliente, String paisCliente, String nomeCliente) { 
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
             if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
                 throw new IllegalArgumentException("Nome do cliente não informado.");
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
    novoOrcamento.setNomeCliente(nomeCliente.trim()); // DEFINIR NOME DO CLIENTE
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

    public List<OrcamentoModel> orcamentosEfetivadosPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Data inicial e data final são obrigatórias.");
        }
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
        }
        return this.orcamentosRepo.findByEfetivadoIsTrueAndDataGeracaoBetweenOrderByIdDesc(dataInicial, dataFinal);
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