package com.bcopstein.sistvendas.interfaceAdaptadora;

import java.util.List;
import java.util.ArrayList;
import java.net.URI;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import com.bcopstein.sistvendas.aplicacao.casosDeUso.*;
import com.bcopstein.sistvendas.aplicacao.dtos.*;

@RestController
public class Controller {
    private ProdutosDisponiveisUC produtosDisponiveisUC;
    private TodosProdutosStatusUC todosProdutosStatusUC;
    private CriaOrcamentoUC criaOrcamentoUC;
    private EfetivaOrcamentoUC efetivaOrcamentoUC;
    private ConsultaVolumeVendasUC consultaVolumeVendasUC;
    private ConsultaOrcamentosEfetivadosUC consultaOrcamentosEfetivadosUC;
    private TodosOrcamentosUC todosOrcamentosUC;
    private AdicionarProdutoUC adicionarProdutoUC;
    private EditarProdutoUC editarProdutoUC;
    private RemoverOrcamentoUC removerOrcamentoUC;
    private DesativarProdutoUC desativarProdutoUC;
    private ConsultaVendasPorProdutoUC consultaVendasPorProdutoUC;
    private ConsultaTaxaConversaoUC consultaTaxaConversaoUC;
    private ConsultaPerfilClienteUC consultaPerfilClienteUC;
    private BaixaEstoqueUC baixaEstoqueUC;
    private EntradaEstoqueUC entradaEstoqueUC;
    private ProdutoPorCodigoUC produtoPorCodigoUC;
    private QtdadeEmEstoqueUC qtdadeEmEstoqueUC;
    private RelistarProdutoUC relistarProdutoUC;
    private ListarNomesClientesUC listarNomesClientesUC;
    private RelatorioEstoqueBaixoUC relatorioEstoqueBaixoUC;

    @Autowired
    public Controller(
            ProdutosDisponiveisUC produtosDisponiveisUC,
            TodosProdutosStatusUC todosProdutosStatusUC,
            CriaOrcamentoUC criaOrcamentoUC,
            EfetivaOrcamentoUC efetivaOrcamentoUC,
            ConsultaOrcamentosEfetivadosUC consultaOrcamentosEfetivadosUC,
            TodosOrcamentosUC todosOrcamentosUC,
            AdicionarProdutoUC adicionarProdutoUC,
            EditarProdutoUC editarProdutoUC,
            RemoverOrcamentoUC removerOrcamentoUC,
            DesativarProdutoUC desativarProdutoUC,
            BaixaEstoqueUC baixaEstoqueUC,
            EntradaEstoqueUC entradaEstoqueUC,
            ProdutoPorCodigoUC produtoPorCodigoUC,
            ConsultaVolumeVendasUC consultaVolumeVendasUC,
            ConsultaVendasPorProdutoUC consultaVendasPorProdutoUC,
            ConsultaTaxaConversaoUC consultaTaxaConversaoUC,
            QtdadeEmEstoqueUC qtdadeEmEstoqueUC,
            ConsultaPerfilClienteUC consultaPerfilClienteUC,
            ListarNomesClientesUC listarNomesClientesUC,
            RelatorioEstoqueBaixoUC relatorioEstoqueBaixoUC) {
        this.produtosDisponiveisUC = produtosDisponiveisUC;
        this.todosProdutosStatusUC = todosProdutosStatusUC;
        this.criaOrcamentoUC = criaOrcamentoUC;
        this.efetivaOrcamentoUC = efetivaOrcamentoUC;
        this.consultaOrcamentosEfetivadosUC = consultaOrcamentosEfetivadosUC;
        this.todosOrcamentosUC = todosOrcamentosUC;
        this.adicionarProdutoUC = adicionarProdutoUC;
        this.editarProdutoUC = editarProdutoUC;
        this.removerOrcamentoUC = removerOrcamentoUC;
        this.desativarProdutoUC = desativarProdutoUC;
        this.baixaEstoqueUC = baixaEstoqueUC;
        this.entradaEstoqueUC = entradaEstoqueUC;
        this.produtoPorCodigoUC = produtoPorCodigoUC;
        this.qtdadeEmEstoqueUC = qtdadeEmEstoqueUC;
        this.consultaVolumeVendasUC = consultaVolumeVendasUC;
        this.consultaVendasPorProdutoUC = consultaVendasPorProdutoUC;
        this.consultaTaxaConversaoUC = consultaTaxaConversaoUC;
        this.consultaPerfilClienteUC = consultaPerfilClienteUC;
        this.listarNomesClientesUC = listarNomesClientesUC;
        this.relatorioEstoqueBaixoUC = relatorioEstoqueBaixoUC;
    }
    
// --- Endpoint da página Redirecionadora ---
    @GetMapping("")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> welcomeMessage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/welcome.html"))
                .build();
    }
// -------------------------------------------------------------------------------------------------------------------------

// --- Endpoints das Consultas ---
    @GetMapping("/gerencial/clientesComCompras")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<String>> getClientesComCompras() {
        try {
            List<String> nomesClientes = listarNomesClientesUC.run();
            return ResponseEntity.ok(nomesClientes);
        } catch (Exception e) {
            System.err.println("Controller Erro: /gerencial/clientesComCompras -> " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar nomes de clientes.", e);
        }
    }

    @GetMapping("/gerencial/perfilCliente")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PerfilClienteDTO> getPerfilCliente(
            @RequestParam String nomeCliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        try {
            PerfilClienteDTO perfil = consultaPerfilClienteUC.run(nomeCliente, dataInicial, dataFinal);
            return ResponseEntity.ok(perfil);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /gerencial/perfilCliente -> " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar perfil do cliente.", e);
        }
    }

    @GetMapping("/gerencial/taxaConversao")
    @CrossOrigin(origins = "*")
    public ResponseEntity<TaxaConversaoDTO> getTaxaConversao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        try {
            TaxaConversaoDTO taxaConversao = consultaTaxaConversaoUC.run(dataInicial, dataFinal);
            return ResponseEntity.ok(taxaConversao);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /gerencial/taxaConversao -> " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao calcular taxa de conversão.",
                    e);
        }
    }

    @GetMapping("/gerencial/vendasPorProduto")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<VendaProdutoDTO>> getVendasPorProduto(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(required = false) Long idProduto) { // Parâmetro para ID do produto

        System.out.println("CONTROLLER - getVendasPorProduto - idProduto recebido: " + idProduto);

        try {
            // Passa o idProduto para o UC
            List<VendaProdutoDTO> vendas = consultaVendasPorProdutoUC.run(dataInicial, dataFinal, idProduto);
            return ResponseEntity.ok(vendas);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /gerencial/vendasPorProduto -> " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao calcular vendas por produto.",
                    e);
        }
    }

    @GetMapping("/gerencial/volumeVendas")
    @CrossOrigin(origins = "*")
    public ResponseEntity<VolumeVendasDTO> getVolumeVendas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        try {
            VolumeVendasDTO volumeVendas = consultaVolumeVendasUC.run(dataInicial, dataFinal);
            return ResponseEntity.ok(volumeVendas);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /gerencial/volumeVendas -> " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao calcular volume de vendas.",
                    e);
        }
    }
// -------------------------------------------------------------------------------------------------------------------------

// --- Endpoints dos Orçamentos ---
    @GetMapping("/todosOrcamentos")
    @CrossOrigin(origins = "*")
    public List<OrcamentoDTO> todosOrcamentos() {
        return todosOrcamentosUC.run();
    }

    @PostMapping("/novoOrcamento")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO novoOrcamento(@RequestBody NovoOrcamentoRequestDTO request) {
        try {
            return criaOrcamentoUC.run(request);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Controller Erro: /novoOrcamento -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /novoOrcamento -> " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar orçamento.", e);
        }
    }

    @GetMapping("/efetivaOrcamento/{id}")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO efetivaOrcamento(@PathVariable(value = "id") long idOrcamento) {
        try {
            OrcamentoDTO orcamento = efetivaOrcamentoUC.run(idOrcamento);
            if (orcamento == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Orçamento ID " + idOrcamento + " não encontrado ou não pôde ser efetivado.");
            }
            return orcamento;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /efetivaOrcamento/" + idOrcamento + " -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao efetivar orçamento.", e);
        }
    }

    @GetMapping("/orcamentosEfetivados")
    @CrossOrigin(origins = "*")
    public List<OrcamentoDTO> consultaOrcamentosEfetivados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        try {
            return consultaOrcamentosEfetivadosUC.run(dataInicial, dataFinal);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /orcamentosEfetivados -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar orçamentos efetivados.",
                    e);
        }
    }

    @DeleteMapping("/orcamentos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> removerOrcamento(@PathVariable long id) {
        try {
            boolean removido = removerOrcamentoUC.run(id);
            if (removido) {
                return ResponseEntity.noContent().build();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento ID " + id + " não encontrado.");
            }
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /orcamentos/" + id + " -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao remover orçamento.", e);
        }
    }
// -------------------------------------------------------------------------------------------------------------------------


// --- Endpoints de Produto ---
    @GetMapping("/produtosDisponiveis")
    @CrossOrigin(origins = "*")
    public List<ProdutoDTO> produtosDisponiveis() {
        return produtosDisponiveisUC.run();
    }

    @PostMapping("/produtos/{id}/relistar")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> relistarProduto(@PathVariable long id) {
        try {
            boolean relistado = relistarProdutoUC.run(id);
            if (relistado) {
                return ResponseEntity.ok().build();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto ID " + id + " não encontrado para re-listar.");
            }
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + "/relistar -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao re-listar produto.", e);
        }
    }

    @GetMapping("/todosProdutosStatus")
    @CrossOrigin(origins = "*")
    public List<ProdutoEstoqueDTO> todosProdutosStatus() {
        return todosProdutosStatusUC.run();
    }

    @GetMapping("/produtos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProdutoDTO> getProdutoPorCodigo(@PathVariable long id) {
        ProdutoDTO produto = produtoPorCodigoUC.run(id);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/produtos")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProdutoDTO> adicionarProduto(@RequestBody NovoProdutoRequestDTO novoProdutoDTO) {
        try {
            ProdutoDTO produtoAdicionado = adicionarProdutoUC.run(novoProdutoDTO);
            return ResponseEntity.created(URI.create("/produtos/" + produtoAdicionado.getId()))
                    .body(produtoAdicionado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos (POST) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao adicionar produto.", e);
        }
    }

    // Editar Produto
    @PutMapping("/produtos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProdutoDTO> editarProduto(@PathVariable long id, @RequestBody ProdutoDTO produtoDTO) {
        try {
            ProdutoDTO produtoEditado = editarProdutoUC.run(id, produtoDTO);
            if (produtoEditado == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado.");
            }
            return ResponseEntity.ok(produtoEditado);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (PUT) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao editar produto.", e);
        }
    }

    // Delistar Produto
    @DeleteMapping("/produtos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> desativarProduto(@PathVariable long id) {
        try {
            boolean desativado = desativarProdutoUC.run(id);
            if (desativado) {
                return ResponseEntity.noContent().build();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto ID " + id + " não encontrado ou falha ao desativar.");
            }
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (DELETE) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao desativar produto.", e);
        }
    }
// -------------------------------------------------------------------------------------------------------------------------

// --- Endpoints de Estoque ---
    // Endpoint para quantidade de um único produto
    @GetMapping("/produtos/{id}/qtdadeEstoque")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Integer> getQtdadeEmEstoque(@PathVariable long id) {
        try {
            int qtd = qtdadeEmEstoqueUC.run(id);
            return ResponseEntity.ok(qtd);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + "/qtdadeEstoque -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar quantidade em estoque.",
                    e);
        }
    }

    @GetMapping("/estoque/produtosPorLista")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<ProdutoEstoqueDTO>> getQtdadeEmEstoquePorLista(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        try {
            List<ProdutoEstoqueDTO> quantidades = qtdadeEmEstoqueUC.run(ids);
            return ResponseEntity.ok(quantidades);
        } catch (Exception e) {
            System.err.println("Controller Erro: /estoque/produtosPorLista -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao buscar quantidades em estoque por lista.", e);
        }
    }

    @PostMapping("/produtos/{id}/baixaEstoque")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> darBaixaEstoque(@PathVariable long id, @RequestParam int qtdade) {
        try {
            baixaEstoqueUC.run(id, qtdade);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + "/baixaEstoque -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao dar baixa no estoque.", e);
        }
    }

    @PostMapping("/produtos/{id}/entradaEstoque")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> darEntradaEstoque(@PathVariable long id, @RequestParam int qtdade) {
        try {
            entradaEstoqueUC.run(id, qtdade);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + "/entradaEstoque -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao dar entrada no estoque.", e);
        }
    }

    @GetMapping(value = "/gerencial/relatorioEstoqueBaixo", produces = MediaType.TEXT_PLAIN_VALUE)
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getRelatorioEstoqueBaixo() {
        try {
            String relatorio = relatorioEstoqueBaixoUC.run();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"relatorio_estoque_baixo.txt\"")
                    .body(relatorio);
        } catch (Exception e) {
            System.err.println("Controller Erro: /gerencial/relatorioEstoqueBaixo -> " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar relatório de baixo estoque.");
        }
    }
// -------------------------------------------------------------------------------------------------------------------------
}