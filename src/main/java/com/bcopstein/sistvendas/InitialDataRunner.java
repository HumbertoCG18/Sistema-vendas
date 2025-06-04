package com.bcopstein.sistvendas; // Ou o pacote base da sua aplicação

import com.bcopstein.sistvendas.dominio.modelos.ClienteModel; // Importar ClienteModel
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IClienteRepositorio; // Importar IClienteRepositorio
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
// Removido import de java.util.ArrayList; e java.util.List; se não forem mais usados diretamente aqui
// para criar a lista de itens, pois o método auxiliar fará isso.

@Component
public class InitialDataRunner implements CommandLineRunner {

    private final IOrcamentoRepositorio orcamentoRepo;
    private final IProdutoRepositorio produtoRepo;
    private final IClienteRepositorio clienteRepo; // Adicionar IClienteRepositorio

    public InitialDataRunner(IOrcamentoRepositorio orcamentoRepo,
            IProdutoRepositorio produtoRepo,
            IClienteRepositorio clienteRepo) { // Adicionar ao construtor
        this.orcamentoRepo = orcamentoRepo;
        this.produtoRepo = produtoRepo;
        this.clienteRepo = clienteRepo; // Atribuir
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("INFO: Verificando necessidade de popular dados mockados...");

        // Popular clientes primeiro, se não existirem
        if (clienteRepo.count() == 0) {
            System.out.println("INFO: Nenhum cliente encontrado. Populando clientes mockados...");
            popularClientesMock();
        } else {
            System.out.println("INFO: Dados de cliente já existem. Pulando a população mockada de clientes.");
        }

        // Popular orçamentos se não existirem
        if (orcamentoRepo.count() == 0 && clienteRepo.count() > 0) { // Só popula orçamentos se houver clientes
            System.out.println("INFO: Nenhum orçamento encontrado. Populando dados mockados de orçamentos...");
            popularOrcamentosMock();
        } else if (orcamentoRepo.count() > 0) {
            System.out.println("INFO: Dados de orçamento já existem. Pulando a população mockada de orçamentos.");
        } else {
            System.out.println("AVISO: Orçamentos mockados não foram populados porque não há clientes no banco.");
        }
    }

    private void popularClientesMock() {
        // Criar e salvar alguns clientes
        ClienteModel clienteA = new ClienteModel("Cliente Feliz SP", "11122233344", "felizsp@exemplo.com");
        clienteRepo.save(clienteA);

        ClienteModel clienteB = new ClienteModel("Cliente Gaúcho", "22233344455", "gaucho@exemplo.com");
        clienteRepo.save(clienteB);

        ClienteModel clienteC = new ClienteModel("Outro Cliente RS", "33344455566", "outrors@exemplo.com");
        clienteRepo.save(clienteC);

        ClienteModel clienteD = new ClienteModel("Cliente Pernambucano", "44455566677", "pe@exemplo.com");
        clienteRepo.save(clienteD);

        ClienteModel clienteE = new ClienteModel("Cliente Indeciso", "55566677788", "indeciso@exemplo.com");
        clienteRepo.save(clienteE);

        ClienteModel clienteF = new ClienteModel("Novo Cliente SP", "66677788899", "novosp@exemplo.com");
        clienteRepo.save(clienteF);

        ClienteModel clienteG = new ClienteModel("Comprador Semanal SP", "77788899900", "semanalsp@exemplo.com");
        clienteRepo.save(clienteG);

        System.out.println("INFO: Clientes mockados populados.");
    }

    private void popularOrcamentosMock() {
        // Buscar produtos existentes
        ProdutoModel tv = produtoRepo.findById(10L).orElse(null);
        ProdutoModel geladeira = produtoRepo.findById(20L).orElse(null);
        ProdutoModel fogao = produtoRepo.findById(30L).orElse(null);
        ProdutoModel lavaLouca = produtoRepo.findById(40L).orElse(null);
        ProdutoModel lavaRoupas = produtoRepo.findById(50L).orElse(null);
        ProdutoModel microondas = produtoRepo.findById(60L).orElse(null);

        if (tv == null || geladeira == null || fogao == null /* etc. adicione todos */) {
            System.err.println(
                    "ERRO: Produtos básicos para mock de orçamentos não foram encontrados. Verifique o data.sql de produtos.");
            return;
        }

        // Buscar clientes mockados
        ClienteModel clienteFelizSP = clienteRepo.findByCpf("11122233344").orElse(null);
        ClienteModel clienteGaucho = clienteRepo.findByCpf("22233344455").orElse(null);
        ClienteModel outroClienteRS = clienteRepo.findByCpf("33344455566").orElse(null);
        ClienteModel clientePernambucano = clienteRepo.findByCpf("44455566677").orElse(null);
        ClienteModel clienteIndeciso = clienteRepo.findByCpf("55566677788").orElse(null);
        ClienteModel novoClienteSP = clienteRepo.findByCpf("66677788899").orElse(null);
        ClienteModel compradorSemanalSP = clienteRepo.findByCpf("77788899900").orElse(null);

        if (clienteFelizSP == null || clienteGaucho == null /* etc. verifique todos os clientes */) {
            System.err.println(
                    "ERRO: Clientes mock para orçamentos não foram encontrados. Verifique a população de clientes.");
            return;
        }

        LocalDate hoje = LocalDate.now();

        // Orçamento 1: Hoje, efetivado, SP
        if (clienteFelizSP != null) {
            criarOrcamento(clienteFelizSP, "Brasil", "SP", hoje, true,
                    new ItemPedidoSetup(tv, 1),
                    new ItemPedidoSetup(geladeira, 1));
        }

        // Orçamento 2: 5 dias atrás, efetivado, RS
        if (clienteGaucho != null) {
            criarOrcamento(clienteGaucho, "Brasil", "RS", hoje.minusDays(5), true,
                    new ItemPedidoSetup(fogao, 1));
        }

        // Orçamento 3: 10 dias atrás, efetivado, RS
        if (outroClienteRS != null) {
            criarOrcamento(outroClienteRS, "Brasil", "RS", hoje.minusDays(10), true,
                    new ItemPedidoSetup(lavaLouca, 1));
        }

        // Orçamento 4: 20 dias atrás, efetivado, PE
        if (clientePernambucano != null) {
            criarOrcamento(clientePernambucano, "Brasil", "PE", hoje.minusDays(20), true,
                    new ItemPedidoSetup(lavaRoupas, 1));
        }

        // Orçamento 5: 3 dias atrás, PENDENTE, RS
        if (clienteIndeciso != null) {
            criarOrcamento(clienteIndeciso, "Brasil", "RS", hoje.minusDays(3), false,
                    new ItemPedidoSetup(microondas, 2));
        }

        // Orçamento 6: Hoje, PENDENTE, SP
        if (novoClienteSP != null) {
            criarOrcamento(novoClienteSP, "Brasil", "SP", hoje, false,
                    new ItemPedidoSetup(tv, 1));
        }

        // Orçamento 7: 8 dias atrás, efetivado, SP
        if (compradorSemanalSP != null) {
            criarOrcamento(compradorSemanalSP, "Brasil", "SP", hoje.minusDays(8), true,
                    new ItemPedidoSetup(fogao, 1),
                    new ItemPedidoSetup(microondas, 1));
        }

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
            System.err
                    .println("ERRO: Tentativa de criar orçamento com cliente nulo. Abortando criação deste orçamento.");
            return;
        }
        OrcamentoModel orc = new OrcamentoModel();
        orc.setCliente(cliente); // Associa a instância de ClienteModel
        orc.setPaisCliente(pais);
        orc.setEstadoCliente(estado);
        orc.setDataGeracao(dataGeracao);

        PedidoModel pedido = new PedidoModel(0L);
        for (ItemPedidoSetup itemSetup : itensSetup) {
            if (itemSetup.produto != null) {
                ItemPedidoModel item = new ItemPedidoModel(itemSetup.produto, itemSetup.quantidade);
                pedido.addItem(item);
            } else {
                System.err.println(
                        "AVISO: Produto nulo encontrado ao tentar adicionar item ao orçamento mock para cliente "
                                + cliente.getNomeCompleto());
            }
        }

        orc.addItensPedido(pedido);
        orc.recalculaTotais();

        if (efetivado) {
            orc.efetiva();
        }

        orcamentoRepo.save(orc);
        System.out.println("INFO: Criado orçamento mock (ID: " + orc.getId() + ") para Cliente: "
                + cliente.getNomeCompleto() + " (ID: " + cliente.getId() + ") em " + dataGeracao
                + (efetivado ? " (Efetivado)" : " (Pendente)"));
    }
}