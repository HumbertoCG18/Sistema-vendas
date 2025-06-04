package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays; // Para a lista de locais
import java.util.Map; // Para estrutura de locais atendidos
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
import com.bcopstein.sistvendas.dominio.modelos.ClienteModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IClienteRepositorio;
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
    private IClienteRepositorio clienteRepo; // Adicionar


    // Definição de locais atendidos
    // No futuro, isso pode vir de um arquivo de configuração ou banco de dados
    private static final Map<String, List<String>> LOCAIS_ATENDIDOS;
    static {
        LOCAIS_ATENDIDOS = new HashMap<>();
        // Locais atendidos conforme Anexo I do PDF para o Brasil
        LOCAIS_ATENDIDOS.put("BRASIL", Arrays.asList("RS", "SP", "PE"));

    }

    @Autowired
    public ServicoDeVendas(IOrcamentoRepositorio orcamentos, ServicoDeEstoque servicoDeEstoque, IClienteRepositorio clienteRepo) {
        this.orcamentosRepo = orcamentos;
        this.servicoDeEstoque = servicoDeEstoque;
        this.clienteRepo = clienteRepo;
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
            // Atualizar para chamar o método correto do repositório se você o renomeou
            // Ex: findByClienteNomeCompletoAndEfetivadoIsTrueAndDataGeracaoBetweenOrderByDataGeracaoDesc
            orcamentosCliente = orcamentosRepo.findByClienteNomeCompletoAndEfetivadoIsTrueAndDataGeracaoBetweenOrderByDataGeracaoDesc(
                nomeCliente, dataInicial, dataFinal);
        } else {
            // Atualizar para chamar o método correto do repositório se você o renomeou
            // Ex: findByClienteNomeCompletoAndEfetivadoIsTrueOrderByDataGeracaoDesc
            orcamentosCliente = orcamentosRepo.findByClienteNomeCompletoAndEfetivadoIsTrueOrderByDataGeracaoDesc(nomeCliente);
        }

        // O restante da lógica para construir PerfilClienteDTO permanece o mesmo,
        // pois ele itera sobre os OrcamentoModel e seus ItemPedidoModel.
        // ... (lógica de agregação existente para itensAgregados e totalGastoPeloCliente) ...
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

                itensAgregados.compute(idProduto, (key, dto) -> {
                    if (dto == null) {
                        return new ItemCompradoDTO(
                            idProduto,
                            produto.getDescricao(),
                            item.getQuantidade(),
                            valorDoItemNoOrcamento
                        );
                    } else {
                        return new ItemCompradoDTO(
                            idProduto,
                            produto.getDescricao(),
                            dto.getQuantidadeTotalComprada() + item.getQuantidade(),
                            dto.getValorTotalGastoNoProduto().add(valorDoItemNoOrcamento)
                        );
                    }
                });
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

    public List<VendaProdutoDTO> calcularTotalVendasPorProduto(LocalDate dataInicial, LocalDate dataFinal,
            Long idProdutoEspecifico) { // Renomeei idProduto2 para clareza
        List<OrcamentoModel> orcamentosConsiderados;

        // Validação e obtenção de orçamentos (mantendo sua lógica original aqui)
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.isAfter(dataFinal)) {
                throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
            }
            orcamentosConsiderados = orcamentosEfetivadosPorPeriodo(dataInicial, dataFinal);
        } else {
            if (dataInicial == null || dataFinal == null) {
                throw new IllegalArgumentException(
                        "Data inicial e data final são obrigatórias para esta consulta, mesmo ao filtrar por produto específico.");
            }
            // Se quiséssemos buscar todos os tempos para um produto específico, a lógica de
            // busca de orçamentos mudaria:
            // if (idProdutoEspecifico != null && dataInicial == null && dataFinal == null)
            // {
            // orcamentosConsiderados = orcamentosRepo.findByEfetivadoIsTrueOrderByIdDesc();
            // // Precisaria deste método
            // } else ...
            orcamentosConsiderados = new ArrayList<>(); // Deveria ser preenchido pela lógica acima
        }

        Map<Long, VendaProdutoDTO> vendasPorProdutoMap = new HashMap<>();

        for (OrcamentoModel orcamento : orcamentosConsiderados) {
            for (ItemPedidoModel item : orcamento.getItens()) {
                ProdutoModel produto = item.getProduto();
                if (produto == null)
                    continue;

                // >>> APLICAR O FILTRO DO PRODUTO AQUI <<<
                if (idProdutoEspecifico != null && produto.getId() != idProdutoEspecifico.longValue()) {
                    continue; // Pula este item se não for o produto específico solicitado
                }

                long idProdutoAtual = produto.getId(); // Usar uma variável local para clareza
                String descricao = produto.getDescricao();
                int quantidadeVendidaItem = item.getQuantidade();

                BigDecimal valorItem = BigDecimal.valueOf(produto.getPrecoUnitario())
                        .multiply(new BigDecimal(quantidadeVendidaItem))
                        .setScale(2, RoundingMode.HALF_UP);

                // Agregação no Map
                vendasPorProdutoMap.compute(idProdutoAtual, (key, dto) -> {
                    if (dto == null) {
                        return new VendaProdutoDTO(idProdutoAtual, descricao, quantidadeVendidaItem, valorItem);
                    } else {
                        // Atualiza o DTO existente somando a nova quantidade e valor
                        return new VendaProdutoDTO(
                                idProdutoAtual,
                                descricao,
                                dto.getQuantidadeTotalVendida() + quantidadeVendidaItem,
                                dto.getValorTotalArrecadado().add(valorItem));
                    }
                });
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
    public OrcamentoModel criaOrcamento(PedidoModel pedido, String estadoCliente, String paisCliente,
            String nomeCliente, String nomeClienteRequest, String cpfClienteRequest) {
        
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
            throw new IllegalArgumentException(
                    "Local de entrega não atendido: País '" + paisCliente + "', Estado '" + estadoCliente + "'.");
        }

ClienteModel clienteAssociado = null;
    if (cpfClienteRequest != null && !cpfClienteRequest.trim().isEmpty()) {
        clienteAssociado = clienteRepo.findByCpf(cpfClienteRequest.trim())
                            .orElse(null);
    }

    if (clienteAssociado == null && nomeClienteRequest != null && !nomeClienteRequest.trim().isEmpty()) {
        // Tenta encontrar por nome se não achou por CPF ou CPF não foi dado
        clienteAssociado = clienteRepo.findByNomeCompletoIgnoreCase(nomeClienteRequest.trim())
                            .orElse(null);
        if (clienteAssociado == null) { // Se ainda não achou, cria um novo
            // Considerar o que fazer se só o nome foi dado e não existe. Criar sempre? Ou exigir CPF para novo?
            // Por simplicidade, criaremos se o nome for fornecido e não existir.
            // Em um sistema real, você pode querer mais dados ou um fluxo de cadastro de cliente separado.
            System.out.println("INFO: Cliente '" + nomeClienteRequest + "' não encontrado. Criando novo cliente.");
            clienteAssociado = new ClienteModel(nomeClienteRequest.trim(), 
                                                (cpfClienteRequest != null ? cpfClienteRequest.trim() : null), 
                                                null); // Email seria null aqui, precisaria vir do DTO
            clienteRepo.save(clienteAssociado);
        }
    } else if (clienteAssociado == null && (nomeClienteRequest == null || nomeClienteRequest.trim().isEmpty())) {
        // Se não tem CPF nem nome, não podemos associar/criar cliente.
        // Você pode lançar uma exceção ou permitir orçamentos sem cliente (se cliente_id for nullable)
        // Para este exemplo, vamos assumir que um nome de cliente é o mínimo.
        throw new IllegalArgumentException("Nome do cliente ou CPF deve ser fornecido para criar/associar cliente ao orçamento.");
    }


    OrcamentoModel novoOrcamento = new OrcamentoModel();
    novoOrcamento.setCliente(clienteAssociado); // ASSOCIA O CLIENTE
    novoOrcamento.setEstadoCliente(estadoCliente.trim()); 
    novoOrcamento.setPaisCliente(paisCliente.trim());
    // O campo nomeCliente no OrcamentoModel foi removido.
    novoOrcamento.addItensPedido(pedido);
    novoOrcamento.recalculaTotais();

    return this.orcamentosRepo.save(novoOrcamento);
    }

    @Transactional
    public OrcamentoModel efetivaOrcamento(long id) {
        OrcamentoModel orcamento = this.orcamentosRepo.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("Orçamento ID " + id + " não encontrado para efetivação."));

        if (orcamento.isEfetivado()) {
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " já está efetivado.");
            return orcamento;
        }

        // Verificação de validade (21 dias)
        if (orcamento.isVencido()) { //
            throw new IllegalStateException("Orçamento ID " + id
                    + " está vencido e não pode mais ser efetivado. Data Geração: " + orcamento.getDataGeracao());
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

            throw new IllegalStateException(
                    "Orçamento ID " + id + " não pode ser efetivado devido à falta de estoque para um ou mais itens.");
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

        public List<String> listarNomesClientesComCompras() {
        return orcamentosRepo.findDistinctNomesClientesComOrcamentosEfetivados();
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