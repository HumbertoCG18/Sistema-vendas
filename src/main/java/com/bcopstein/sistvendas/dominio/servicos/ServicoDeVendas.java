package com.bcopstein.sistvendas.dominio.servicos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.sistvendas.aplicacao.dtos.ItemCompradoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.PerfilClienteDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.TaxaConversaoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.VendaProdutoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.VolumeVendasDTO;
import com.bcopstein.sistvendas.dominio.modelos.ClienteModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IClienteRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;
import com.bcopstein.sistvendas.dominio.servicos.impostos.CalculadorImpostoEstadualStrategy;
import com.bcopstein.sistvendas.dominio.servicos.impostos.ImpostoStrategyFactory;

@Service
public class ServicoDeVendas {
    private final IOrcamentoRepositorio orcamentosRepo;
    private final ServicoDeEstoque servicoDeEstoque;
    private final IClienteRepositorio clienteRepo;

    private static final Map<String, List<String>> LOCAIS_ATENDIDOS;
    static {
        LOCAIS_ATENDIDOS = new HashMap<>();
        LOCAIS_ATENDIDOS.put("BRASIL", Arrays.asList("RS", "SP", "PE"));
    }

    public ServicoDeVendas(IOrcamentoRepositorio orcamentos, ServicoDeEstoque servicoDeEstoque,
            IClienteRepositorio clienteRepo) {
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

    @Transactional
    public OrcamentoModel criaOrcamento(PedidoModel pedido, String estadoCliente, String paisCliente,
            String nomeClienteRequest, String cpfClienteRequest, String emailClienteRequest) {

        if (pedido == null || pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido inválido: não pode ser nulo ou vazio.");
        }
        for (ItemPedidoModel item : pedido.getItens()) {
            if (item.getProduto() == null)
                throw new IllegalArgumentException("Item de pedido inválido: produto não pode ser nulo.");
            if (item.getQuantidade() <= 0)
                throw new IllegalArgumentException("Item de pedido inválido: quantidade deve ser positiva.");
        }
        if (nomeClienteRequest == null || nomeClienteRequest.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não informado.");
        }
        if (paisCliente == null || paisCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("País do cliente não informado.");
        }
        if (estadoCliente == null || estadoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado do cliente não informado.");
        }

        String paisUpper = paisCliente.trim().toUpperCase();
        String estadoUpper = estadoCliente.trim().toUpperCase();
        List<String> estadosAtendidosNoPais = LOCAIS_ATENDIDOS.get(paisUpper);
        if (estadosAtendidosNoPais == null || !estadosAtendidosNoPais.contains(estadoUpper)) {
            throw new IllegalArgumentException(
                    "Local de entrega não atendido: País '" + paisCliente + "', Estado '" + estadoCliente + "'.");
        }

        CalculadorImpostoEstadualStrategy impostoStrategy = ImpostoStrategyFactory.getStrategy(estadoUpper);

        ClienteModel clienteAssociado = null;
        String cpfLimpo = (cpfClienteRequest != null && !cpfClienteRequest.trim().isEmpty())
                ? cpfClienteRequest.trim().replaceAll("[^0-9]", "")
                : null;
        String emailLimpo = (emailClienteRequest != null && !emailClienteRequest.trim().isEmpty())
                ? emailClienteRequest.trim().toLowerCase()
                : null;

        if (cpfLimpo != null) {
            Optional<ClienteModel> clientePorCpf = clienteRepo.findByCpf(cpfLimpo);
            if (clientePorCpf.isPresent()) {
                clienteAssociado = clientePorCpf.get();
                boolean precisaAtualizar = false;
                if (!clienteAssociado.getNomeCompleto().equalsIgnoreCase(nomeClienteRequest.trim())) {
                    clienteAssociado.setNomeCompleto(nomeClienteRequest.trim());
                    precisaAtualizar = true;
                }
                if (emailLimpo != null && (clienteAssociado.getEmail() == null
                        || !clienteAssociado.getEmail().equalsIgnoreCase(emailLimpo))) {
                    clienteAssociado.setEmail(emailLimpo);
                    precisaAtualizar = true;
                }
                if (precisaAtualizar)
                    clienteRepo.save(clienteAssociado);
            }
        }

        if (clienteAssociado == null) {
            System.out.println("INFO: Cliente '" + nomeClienteRequest + (cpfLimpo != null ? ", CPF: " + cpfLimpo : "")
                    + "' não encontrado por identificadores únicos. Criando novo cliente.");
            clienteAssociado = new ClienteModel(nomeClienteRequest.trim(), cpfLimpo, emailLimpo);
            clienteRepo.save(clienteAssociado);
        }

        OrcamentoModel novoOrcamento = new OrcamentoModel();
        novoOrcamento.setCliente(clienteAssociado);
        novoOrcamento.setEstadoCliente(estadoCliente.trim());
        novoOrcamento.setPaisCliente(paisCliente.trim());
        novoOrcamento.setImpostoStrategy(impostoStrategy);
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
        if (orcamento.isVencido()) {
            throw new IllegalStateException("Orçamento ID " + id
                    + " está vencido e não pode mais ser efetivado. Data Geração: " + orcamento.getDataGeracao());
        }
        if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            throw new IllegalStateException("Orçamento ID " + id + " não pode ser efetivado pois não contém itens.");
        }

        boolean todosDisponiveis = true;
        // CORREÇÃO: Substituído 'var' por 'ItemPedidoModel'
        for (ItemPedidoModel item : orcamento.getItens()) {
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
            // CORREÇÃO: Substituído 'var' por 'ItemPedidoModel'
            for (ItemPedidoModel item : orcamento.getItens()) {
                servicoDeEstoque.baixaEstoque(item.getProduto().getId(), item.getQuantidade());
            }
            orcamento.efetiva();
            orcamentosRepo.save(orcamento);
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

    public VolumeVendasDTO calcularVolumeVendasPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException(
                    "Data inicial e data final são obrigatórias para calcular o volume de vendas.");
        }
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException(
                    "Data inicial não pode ser posterior à data final para calcular o volume de vendas.");
        }
        List<OrcamentoModel> orcamentosNoPeriodo = orcamentosEfetivadosPorPeriodo(dataInicial, dataFinal);
        BigDecimal totalVendas = BigDecimal.ZERO;
        for (OrcamentoModel orcamento : orcamentosNoPeriodo) {
            totalVendas = totalVendas.add(orcamento.getCustoConsumidor());
        }
        return new VolumeVendasDTO(dataInicial, dataFinal, totalVendas.setScale(2, RoundingMode.HALF_UP),
                orcamentosNoPeriodo.size());
    }

    public List<VendaProdutoDTO> calcularTotalVendasPorProduto(LocalDate dataInicial, LocalDate dataFinal,
            Long idProdutoEspecifico) {
        List<OrcamentoModel> orcamentosConsiderados;
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException(
                    "Data inicial e data final são obrigatórias para consulta de vendas por produto.");
        }
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
        }
        orcamentosConsiderados = orcamentosEfetivadosPorPeriodo(dataInicial, dataFinal);

        Map<Long, VendaProdutoDTO> vendasPorProdutoMap = new HashMap<>();
        for (OrcamentoModel orcamento : orcamentosConsiderados) {
            for (ItemPedidoModel item : orcamento.getItens()) {
                ProdutoModel produto = item.getProduto();
                if (produto == null)
                    continue;
                if (idProdutoEspecifico != null && produto.getId() != idProdutoEspecifico) {
                    continue;
                }
                long idProdutoAtual = produto.getId();
                String descricao = produto.getDescricao();
                int quantidadeVendidaItem = item.getQuantidade();
                BigDecimal valorItem = BigDecimal.valueOf(produto.getPrecoUnitario())
                        .multiply(new BigDecimal(quantidadeVendidaItem))
                        .setScale(2, RoundingMode.HALF_UP);
                vendasPorProdutoMap.compute(idProdutoAtual, (key, dto) -> {
                    if (dto == null) {
                        return new VendaProdutoDTO(idProdutoAtual, descricao, quantidadeVendidaItem, valorItem);
                    } else {
                        return new VendaProdutoDTO(idProdutoAtual, descricao,
                                dto.getQuantidadeTotalVendida() + quantidadeVendidaItem,
                                dto.getValorTotalArrecadado().add(valorItem));
                    }
                });
            }
        }
        return new ArrayList<>(vendasPorProdutoMap.values());
    }

    public PerfilClienteDTO gerarPerfilCliente(String nomeCliente, LocalDate dataInicial, LocalDate dataFinal) {
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }

        List<OrcamentoModel> orcamentosCliente;
        if (dataInicial != null && dataFinal != null) {
            orcamentosCliente = orcamentosRepo
                    .findByClienteNomeCompletoAndEfetivadoIsTrueAndDataGeracaoBetweenOrderByDataGeracaoDesc(
                            nomeCliente.trim(), dataInicial, dataFinal);
        } else {
            orcamentosCliente = orcamentosRepo
                    .findByClienteNomeCompletoAndEfetivadoIsTrueOrderByDataGeracaoDesc(nomeCliente.trim());
        }

        if (orcamentosCliente.isEmpty()) {
            ClienteModel cliente = clienteRepo.findByNomeCompletoIgnoreCase(nomeCliente.trim()).orElse(null);
            if (cliente != null) {
                return new PerfilClienteDTO(cliente.getNomeCompleto(), cliente.getCpf(), cliente.getEmail(),
                        dataInicial, dataFinal, BigDecimal.ZERO, 0, new ArrayList<>());
            }
            return new PerfilClienteDTO(nomeCliente, null, null, dataInicial, dataFinal, BigDecimal.ZERO, 0,
                    new ArrayList<>());
        }

        ClienteModel cliente = orcamentosCliente.get(0).getCliente();
        String cpfCliente = (cliente != null) ? cliente.getCpf() : null;
        String emailCliente = (cliente != null) ? cliente.getEmail() : null;

        BigDecimal totalGastoPeloCliente = BigDecimal.ZERO;
        Map<Long, ItemCompradoDTO> itensAgregados = new HashMap<>();

        for (OrcamentoModel orcamento : orcamentosCliente) {
            totalGastoPeloCliente = totalGastoPeloCliente.add(orcamento.getCustoConsumidor());
            for (ItemPedidoModel item : orcamento.getItens()) {
                ProdutoModel produto = item.getProduto();
                if (produto == null)
                    continue;

                long idProduto = produto.getId();
                BigDecimal valorDoItemNoOrcamento = BigDecimal.valueOf(produto.getPrecoUnitario())
                        .multiply(new BigDecimal(item.getQuantidade()))
                        .setScale(2, RoundingMode.HALF_UP);

                itensAgregados.compute(idProduto,
                        (key, dto) -> (dto == null)
                                ? new ItemCompradoDTO(idProduto, produto.getDescricao(), item.getQuantidade(),
                                        valorDoItemNoOrcamento)
                                : new ItemCompradoDTO(idProduto, produto.getDescricao(),
                                        dto.getQuantidadeTotalComprada() + item.getQuantidade(),
                                        dto.getValorTotalGastoNoProduto().add(valorDoItemNoOrcamento)));
            }
        }

        return new PerfilClienteDTO(nomeCliente, cpfCliente, emailCliente,
                dataInicial, dataFinal,
                totalGastoPeloCliente.setScale(2, RoundingMode.HALF_UP),
                orcamentosCliente.size(),
                new ArrayList<>(itensAgregados.values()));
    }

    public List<String> listarNomesClientesComCompras() {
        return orcamentosRepo.findDistinctNomesClientesComOrcamentosEfetivados();
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