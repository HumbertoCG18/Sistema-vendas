package com.bcopstein.sistvendas;

import com.bcopstein.sistvendas.dominio.modelos.ClienteModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IClienteRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class InitialDataRunner implements CommandLineRunner {

    private final IOrcamentoRepositorio orcamentoRepo;
    private final IProdutoRepositorio produtoRepo;
    private final IClienteRepositorio clienteRepo;

    public InitialDataRunner(IOrcamentoRepositorio orcamentoRepo,
                           IProdutoRepositorio produtoRepo,
                           IClienteRepositorio clienteRepo) {
        this.orcamentoRepo = orcamentoRepo;
        this.produtoRepo = produtoRepo;
        this.clienteRepo = clienteRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("INFO: Verificando necessidade de popular dados mockados...");

        if (clienteRepo.count() == 0) {
            System.out.println("INFO: Nenhum cliente encontrado. Populando clientes mockados...");
            popularClientesMock();
        }
        
        if (orcamentoRepo.count() == 0 && clienteRepo.count() > 0) {
            System.out.println("INFO: Nenhum orçamento encontrado. Populando dados mockados de orçamentos...");
            popularOrcamentosMock();
        }
    }

    private void popularClientesMock() {
        clienteRepo.save(new ClienteModel("Cliente Feliz SP", "11122233344", "felizsp@exemplo.com"));
        clienteRepo.save(new ClienteModel("Cliente Gaúcho", "22233344455", "gaucho@exemplo.com"));
        clienteRepo.save(new ClienteModel("Outro Cliente RS", "33344455566", "outrors@exemplo.com"));
        clienteRepo.save(new ClienteModel("Cliente Pernambucano", "44455566677", "pe@exemplo.com"));
        clienteRepo.save(new ClienteModel("Cliente Indeciso", "55566677788", "indeciso@exemplo.com"));
        clienteRepo.save(new ClienteModel("Novo Cliente SP", "66677788899", "novosp@exemplo.com"));
        clienteRepo.save(new ClienteModel("Comprador Semanal SP", "77788899900", "semanalsp@exemplo.com"));
        // NOVO CLIENTE PARA TESTE DE DESCONTO
        clienteRepo.save(new ClienteModel("Cliente Com Desconto", "88899900011", "desconto@exemplo.com"));
        System.out.println("INFO: Clientes mockados populados.");
    }

    private void popularOrcamentosMock() {
        // Buscar produtos existentes
        ProdutoModel tv = produtoRepo.findById(10L).orElse(null);
        ProdutoModel fogao = produtoRepo.findById(30L).orElse(null);
        ProdutoModel microondas = produtoRepo.findById(60L).orElse(null);

        // Buscar clientes mockados
        ClienteModel clienteGaucho = clienteRepo.findByCpf("22233344455").orElse(null);
        ClienteModel clienteIndeciso = clienteRepo.findByCpf("55566677788").orElse(null);
        ClienteModel clienteComDesconto = clienteRepo.findByCpf("88899900011").orElse(null);

        if (tv == null || fogao == null || clienteGaucho == null || clienteComDesconto == null) {
            System.err.println("ERRO: Entidades básicas para mock (produtos/clientes) não foram encontradas.");
            return;
        }
        
        LocalDate hoje = LocalDate.now();

        // Cenário 1: Orçamento Simples, Efetivado
        criarOrcamento(clienteGaucho, "Brasil", "RS", hoje.minusDays(5), true,
                new ItemPedidoSetup(fogao, 1)
        );

        // Cenário 2: Orçamento PENDENTE
        criarOrcamento(clienteIndeciso, "Brasil", "RS", hoje.minusDays(3), false,
                new ItemPedidoSetup(microondas, 2)
        );

        // Cenário 3: Orçamento com DESCONTO POR ITEM (quantidade > 3)
        criarOrcamento(clienteComDesconto, "Brasil", "PE", hoje.minusDays(2), true,
                new ItemPedidoSetup(tv, 4), // Quantidade 4 vai acionar o desconto de 5%
                new ItemPedidoSetup(microondas, 1)
        );
        
        System.out.println("INFO: População de orçamentos mockados concluída.");
    }

    // Classe auxiliar interna para facilitar a criação de itens
    private static class ItemPedidoSetup {
        ProdutoModel produto;
        int quantidade;
        ItemPedidoSetup(ProdutoModel produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }
    }

    // Método auxiliar para criar e salvar um orçamento
    private void criarOrcamento(ClienteModel cliente, String pais, String estado, LocalDate dataGeracao,
                              boolean efetivado, ItemPedidoSetup... itensSetup) {
        if (cliente == null) {
            System.err.println("ERRO: Tentativa de criar orçamento com cliente nulo. Abortando.");
            return;
        }
        OrcamentoModel orc = new OrcamentoModel();
        orc.setCliente(cliente);
        orc.setPaisCliente(pais);
        orc.setEstadoCliente(estado);
        orc.setDataGeracao(dataGeracao);

        PedidoModel pedido = new PedidoModel(0L);
        for (ItemPedidoSetup itemSetup : itensSetup) {
            if (itemSetup.produto != null) {
                ItemPedidoModel item = new ItemPedidoModel(itemSetup.produto, itemSetup.quantidade);
                pedido.addItem(item);
            }
        }

        orc.addItensPedido(pedido);
        orc.recalculaTotais(); // Calcula tudo, incluindo descontos

        if (efetivado) {
            orc.efetiva();
        }

        // Logs para depuração
        System.out.println("-----------------------------------------------------");
        System.out.println("CRIANDO ORÇAMENTO MOCK PARA: " + cliente.getNomeCompleto());
        System.out.println("  - Itens: " + pedido.getItens().size());
        System.out.println("  - Subtotal Calculado: " + orc.getCustoItens());
        System.out.println("  - Desconto Calculado: " + orc.getValorDesconto());
        System.out.println("  - Custo Consumidor: " + orc.getCustoConsumidor());
        System.out.println("-----------------------------------------------------");

        orcamentoRepo.save(orc);
    }
}
