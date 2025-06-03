package com.bcopstein.sistvendas.interfaceAdaptadora;

import java.util.List;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.bcopstein.sistvendas.aplicacao.casosDeUso.*; 
import com.bcopstein.sistvendas.aplicacao.dtos.*; 

@RestController
public class Controller {
    // UCs existentes
    private ProdutosDisponiveisUC produtosDisponiveisUC;
    private TodosProdutosStatusUC todosProdutosStatusUC;
    private CriaOrcamentoUC criaOrcamentoUC;
    private EfetivaOrcamentoUC efetivaOrcamentoUC;
    private UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivadosUC;
    private TodosOrcamentosUC todosOrcamentosUC;
    private AdicionarProdutoUC adicionarProdutoUC;
    private EditarProdutoUC editarProdutoUC;
    private RemoverOrcamentoUC removerOrcamentoUC;
    private DesativarProdutoUC desativarProdutoUC;

    // Novos UCs
    private BaixaEstoqueUC baixaEstoqueUC;
    private EntradaEstoqueUC entradaEstoqueUC;
    private ProdutoPorCodigoUC produtoPorCodigoUC;
    private QtdadeEmEstoqueUC qtdadeEmEstoqueUC;

    @Autowired
    public Controller(
            ProdutosDisponiveisUC produtosDisponiveisUC,
            TodosProdutosStatusUC todosProdutosStatusUC,
            CriaOrcamentoUC criaOrcamentoUC,
            EfetivaOrcamentoUC efetivaOrcamentoUC,
            UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivadosUC,
            TodosOrcamentosUC todosOrcamentosUC,
            AdicionarProdutoUC adicionarProdutoUC,
            EditarProdutoUC editarProdutoUC,
            RemoverOrcamentoUC removerOrcamentoUC,
            DesativarProdutoUC desativarProdutoUC,
            // Injeta os novos UCs
            BaixaEstoqueUC baixaEstoqueUC,
            EntradaEstoqueUC entradaEstoqueUC,
            ProdutoPorCodigoUC produtoPorCodigoUC,
            QtdadeEmEstoqueUC qtdadeEmEstoqueUC
            ) {
        this.produtosDisponiveisUC = produtosDisponiveisUC;
        this.todosProdutosStatusUC = todosProdutosStatusUC;
        this.criaOrcamentoUC = criaOrcamentoUC;
        this.efetivaOrcamentoUC = efetivaOrcamentoUC;
        this.ultimosOrcamentosEfetivadosUC = ultimosOrcamentosEfetivadosUC;
        this.todosOrcamentosUC = todosOrcamentosUC;
        this.adicionarProdutoUC = adicionarProdutoUC;
        this.editarProdutoUC = editarProdutoUC;
        this.removerOrcamentoUC = removerOrcamentoUC;
        this.desativarProdutoUC = desativarProdutoUC;
        // Atribui os novos UCs
        this.baixaEstoqueUC = baixaEstoqueUC;
        this.entradaEstoqueUC = entradaEstoqueUC;
        this.produtoPorCodigoUC = produtoPorCodigoUC;
        this.qtdadeEmEstoqueUC = qtdadeEmEstoqueUC;
    }

    @GetMapping("")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> welcomeMessage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/welcome.html"))
                .build();
    }

    // --- Endpoints de Orçamento (Mantidos) ---
    // ... (Mantenha os endpoints existentes de orçamento) ...
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
        }
         catch (Exception e) {
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento ID " + idOrcamento + " não encontrado.");
            }
            return orcamento;
        } catch (IllegalStateException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
         catch (Exception e) {
            System.err.println("Controller Erro: /efetivaOrcamento/" + idOrcamento + " -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao efetivar orçamento.", e);
        }
    }

    @GetMapping("/orcamentosEfetivados")
    @CrossOrigin(origins = "*")
    public List<OrcamentoDTO> ultimosOrcamentosEfetivados(@RequestParam(defaultValue = "10") int n) {
        return ultimosOrcamentosEfetivadosUC.run(n);
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

    // --- Endpoints de Produto (Mantidos e Novos) ---
    @GetMapping("/produtosDisponiveis")
    @CrossOrigin(origins = "*")
    public List<ProdutoDTO> produtosDisponiveis() {
        return produtosDisponiveisUC.run();
    }

    @GetMapping("/todosProdutosStatus")
    @CrossOrigin(origins = "*")
    public List<ProdutoEstoqueDTO> todosProdutosStatus() {
        return todosProdutosStatusUC.run();
    }

    // NOVO Endpoint: Buscar produto por código
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
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (DELETE) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao desativar produto.", e);
        }
    }

    // --- Endpoints de Estoque (Novos) ---

    @GetMapping("/produtos/{id}/qtdadeEstoque")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Integer> getQtdadeEmEstoque(@PathVariable long id) {
        try {
            int qtd = qtdadeEmEstoqueUC.run(id);
            return ResponseEntity.ok(qtd);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + "/qtdadeEstoque -> " + e.getMessage());
            // Decide se retorna 404 se não achar ou 500 para outros erros.
            // Por enquanto, vamos assumir 500 para simplicidade.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar quantidade em estoque.", e);
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
}