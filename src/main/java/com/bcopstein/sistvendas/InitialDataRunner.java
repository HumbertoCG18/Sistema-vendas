package com.bcopstein.sistvendas; // Ou o pacote base da sua aplicação

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
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

    public InitialDataRunner(IOrcamentoRepositorio orcamentoRepo, IProdutoRepositorio produtoRepo) {
        this.orcamentoRepo = orcamentoRepo;
        this.produtoRepo = produtoRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("INFO: Verificando necessidade de popular dados mockados de orçamentos...");

        if (orcamentoRepo.count() == 0) {
            System.out.println("INFO: Nenhum orçamento encontrado. Populando dados mockados...");
            popularOrcamentosMock();
        } else {
            System.out.println("INFO: Dados de orçamento já existem. Pulando a população mockada.");
        }
    }

    private void popularOrcamentosMock() {
        // Buscar produtos existentes para adicionar aos orçamentos
        ProdutoModel tv = produtoRepo.findById(10L).orElse(null);
        ProdutoModel geladeira = produtoRepo.findById(20L).orElse(null);
        ProdutoModel fogao = produtoRepo.findById(30L).orElse(null);
        ProdutoModel lavaLouca = produtoRepo.findById(40L).orElse(null);
        ProdutoModel lavaRoupas = produtoRepo.findById(50L).orElse(null);
        ProdutoModel microondas = produtoRepo.findById(60L).orElse(null);

        if (tv == null || geladeira == null || fogao == null || lavaLouca == null || lavaRoupas == null
                || microondas == null) {
            System.err.println(
                    "ERRO: Produtos básicos para mock de orçamentos não foram encontrados no banco. Verifique o data.sql de produtos.");
            return;
        }

        LocalDate hoje = LocalDate.now();

        // Orçamento 1: Hoje, efetivado, SP
        criarOrcamento("Wanderley", "Brasil", "SP", hoje, true,
                new ItemPedidoSetup(tv, 1),
                new ItemPedidoSetup(geladeira, 1));

        // Orçamento 2: 5 dias atrás, efetivado, RS
        criarOrcamento("Cleiton", "Brasil", "RS", hoje.minusDays(5), true,
                new ItemPedidoSetup(fogao, 1));

        // Orçamento 3: 10 dias atrás, efetivado, RS
        criarOrcamento("Cleomar", "Brasil", "RS", hoje.minusDays(10), true,
                new ItemPedidoSetup(lavaLouca, 1));

        // Orçamento 4: 20 dias atrás, efetivado, PE
        criarOrcamento("Balduino", "Brasil", "PE", hoje.minusDays(20), true,
                new ItemPedidoSetup(lavaRoupas, 1));

        // Orçamento 5: 3 dias atrás, PENDENTE, RS
        criarOrcamento("Baltazar", "Brasil", "RS", hoje.minusDays(3), false,
                new ItemPedidoSetup(microondas, 2) // 2 microondas
        );

        // Orçamento 6: Hoje, PENDENTE, SP (para testar taxa de conversão hoje)
        criarOrcamento("Hilário", "Brasil", "SP", hoje, false,
                new ItemPedidoSetup(tv, 1));

        // Orçamento 7: 8 dias atrás, efetivado, SP (para testar relatórios
        // semanais/quinzenais)
        criarOrcamento("Glauco", "Brasil", "SP", hoje.minusDays(8), true,
                new ItemPedidoSetup(fogao, 1),
                new ItemPedidoSetup(microondas, 1));

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
    private void criarOrcamento(String nomeCliente, String pais, String estado, LocalDate dataGeracao,
            boolean efetivado, ItemPedidoSetup... itensSetup) {
        OrcamentoModel orc = new OrcamentoModel();
        orc.setNomeCliente(nomeCliente);
        orc.setPaisCliente(pais);
        orc.setEstadoCliente(estado);
        orc.setDataGeracao(dataGeracao); // Definindo a data de geração manualmente para o mock

        PedidoModel pedido = new PedidoModel(0L); // ID do PedidoModel é transiente
        for (ItemPedidoSetup itemSetup : itensSetup) {
            if (itemSetup.produto != null) {
                ItemPedidoModel item = new ItemPedidoModel(itemSetup.produto, itemSetup.quantidade);
                pedido.addItem(item);
            }
        }

        orc.addItensPedido(pedido); // Adiciona os itens e estabelece o link bidirecional
        orc.recalculaTotais(); // Calcula subtotal, impostos, descontos, etc.

        if (efetivado) {
            orc.efetiva(); // Marca como efetivado
        }

        orcamentoRepo.save(orc); // Salva o orçamento. Itens são salvos por cascata.
        System.out.println("INFO: Criado orçamento mock (ID: " + orc.getId() + ") para " + nomeCliente + " em "
                + dataGeracao + (efetivado ? " (Efetivado)" : " (Pendente)"));
    }
}