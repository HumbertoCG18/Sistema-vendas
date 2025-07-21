package com.bcopstein.sistvendas.interfaceAdaptadora;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bcopstein.sistvendas.aplicacao.casosDeUso.AdicionarProdutoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.BaixaEstoqueUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ConsultaOrcamentosEfetivadosUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ConsultaPerfilClienteUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ConsultaTaxaConversaoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ConsultaVendasPorProdutoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ConsultaVolumeVendasUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.CriaOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.DesativarProdutoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.EditarProdutoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.EfetivaOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.EntradaEstoqueUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ListarNomesClientesUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ProdutoPorCodigoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ProdutosDisponiveisUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.QtdadeEmEstoqueUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.RelatorioEstoqueBaixoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.RelistarProdutoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.RemoverOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.TodosOrcamentosUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.TodosProdutosStatusUC;
import com.bcopstein.sistvendas.aplicacao.dtos.NovoOrcamentoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.NovoProdutoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.PerfilClienteDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.TaxaConversaoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.VendaProdutoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.VolumeVendasDTO;

@RestController
public class Controller {
    private final ProdutosDisponiveisUC produtosDisponiveisUC;
    private final TodosProdutosStatusUC todosProdutosStatusUC;
    private final CriaOrcamentoUC criaOrcamentoUC;
    private final EfetivaOrcamentoUC efetivaOrcamentoUC;
    private final ConsultaVolumeVendasUC consultaVolumeVendasUC;
    private final ConsultaOrcamentosEfetivadosUC consultaOrcamentosEfetivadosUC;
    private final TodosOrcamentosUC todosOrcamentosUC;
    private final AdicionarProdutoUC adicionarProdutoUC;
    private final EditarProdutoUC editarProdutoUC;
    private final RemoverOrcamentoUC removerOrcamentoUC;
    private final DesativarProdutoUC desativarProdutoUC;
    private final ConsultaVendasPorProdutoUC consultaVendasPorProdutoUC;
    private final ConsultaTaxaConversaoUC consultaTaxaConversaoUC;
    private final ConsultaPerfilClienteUC consultaPerfilClienteUC;
    private final BaixaEstoqueUC baixaEstoqueUC;
    private final EntradaEstoqueUC entradaEstoqueUC;
    private final ProdutoPorCodigoUC produtoPorCodigoUC;
    private final QtdadeEmEstoqueUC qtdadeEmEstoqueUC;
    private final RelistarProdutoUC relistarProdutoUC;
    private final ListarNomesClientesUC listarNomesClientesUC;
    private final RelatorioEstoqueBaixoUC relatorioEstoqueBaixoUC;
    
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

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
            RelatorioEstoqueBaixoUC relatorioEstoqueBaixoUC,
            RelistarProdutoUC relistarProdutoUC) {
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
        this.relistarProdutoUC = relistarProdutoUC;
        this.listarNomesClientesUC = listarNomesClientesUC;
        this.relatorioEstoqueBaixoUC = relatorioEstoqueBaixoUC;
    }
    
    @GetMapping("")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> welcomeMessage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/welcome.html"))
                .build();
    }

    @GetMapping("/gerencial/clientesComCompras")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<String>> getClientesComCompras() {
        try {
            List<String> nomesClientes = listarNomesClientesUC.run();
            return ResponseEntity.ok(nomesClientes);
        } catch (RuntimeException e) {
            logger.error("Erro em /gerencial/clientesComCompras: {}", e.getMessage());
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
        } catch (RuntimeException e) {
            logger.error("Erro em /gerencial/perfilCliente: {}", e.getMessage());
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
        } catch (RuntimeException e) {
            logger.error("Erro em /gerencial/taxaConversao: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao calcular taxa de conversão.", e);
        }
    }

    @GetMapping("/gerencial/vendasPorProduto")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<VendaProdutoDTO>> getVendasPorProduto(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(required = false) Long idProduto) {
        logger.info("CONTROLLER - getVendasPorProduto - idProduto recebido: {}", idProduto);
        try {
            List<VendaProdutoDTO> vendas = consultaVendasPorProdutoUC.run(dataInicial, dataFinal, idProduto);
            return ResponseEntity.ok(vendas);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (RuntimeException e) {
            logger.error("Erro em /gerencial/vendasPorProduto: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao calcular vendas por produto.", e);
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
        } catch (RuntimeException e) {
            logger.error("Erro em /gerencial/volumeVendas: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao calcular volume de vendas.", e);
        }
    }

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
            logger.error("Erro em /novoOrcamento: {}", e.getMessage());
            // Se a RuntimeException for sobre "Produto não encontrado", um 404 faz sentido.
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar orçamento.", e);
        }
    }

    @GetMapping("/efetivaOrcamento/{id}")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO efetivaOrcamento(@PathVariable(value = "id") long idOrcamento) {
        try {
            OrcamentoDTO orcamento = efetivaOrcamentoUC.run(idOrcamento);
            if (orcamento == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento ID " + idOrcamento + " não encontrado.");
            }
            return orcamento;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (RuntimeException e) {
            logger.error("Erro em /efetivaOrcamento/{}: {}", idOrcamento, e.getMessage());
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
        } catch (RuntimeException e) {
            logger.error("Erro em /orcamentosEfetivados: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar orçamentos efetivados.", e);
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
        } catch (RuntimeException e) {
            logger.error("Erro em /orcamentos/{}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao remover orçamento.", e);
        }
    }

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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado para re-listar.");
            }
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos/{}/relistar: {}", id, e.getMessage());
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
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos (POST): {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao adicionar produto.", e);
        }
    }

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
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos/{} (PUT): {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao editar produto.", e);
        }
    }

    @DeleteMapping("/produtos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> desativarProduto(@PathVariable long id) {
        try {
            boolean desativado = desativarProdutoUC.run(id);
            if (desativado) {
                return ResponseEntity.noContent().build();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado ou falha ao desativar.");
            }
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos/{} (DELETE): {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao desativar produto.", e);
        }
    }

    @GetMapping("/produtos/{id}/qtdadeEstoque")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Integer> getQtdadeEmEstoque(@PathVariable long id) {
        try {
            int qtd = qtdadeEmEstoqueUC.run(id);
            return ResponseEntity.ok(qtd);
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos/{}/qtdadeEstoque: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar quantidade em estoque.", e);
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
        } catch (RuntimeException e) {
            logger.error("Erro em /estoque/produtosPorLista: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar quantidades em estoque por lista.", e);
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
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos/{}/baixaEstoque: {}", id, e.getMessage());
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
        } catch (RuntimeException e) {
            logger.error("Erro em /produtos/{}/entradaEstoque: {}", id, e.getMessage());
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
        } catch (RuntimeException e) {
            logger.error("Erro em /gerencial/relatorioEstoqueBaixo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar relatório de baixo estoque.");
        }
    }
}