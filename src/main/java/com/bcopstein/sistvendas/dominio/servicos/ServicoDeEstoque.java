package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.aplicacao.dtos.NovoProdutoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO;

@Service
public class ServicoDeEstoque {
    private IEstoqueRepositorio estoqueRepo;
    private IProdutoRepositorio produtosRepo;

    @Autowired
    public ServicoDeEstoque(IProdutoRepositorio produtos, IEstoqueRepositorio estoque) {
        this.produtosRepo = produtos;
        this.estoqueRepo = estoque;
    }

    public List<ProdutoModel> produtosDisponiveis() {
        return estoqueRepo.findByListadoTrueAndQuantidadeGreaterThan(0)
                .stream()
                .map(ItemDeEstoqueModel::getProduto)
                .collect(Collectors.toList());
    }

    public List<ProdutoEstoqueDTO> getTodosProdutosComStatusEstoque() {
        List<ProdutoModel> todosProdutosCatalogados = produtosRepo.findAll();
        return todosProdutosCatalogados.stream()
                .map(produto -> {
                    ItemDeEstoqueModel itemEstoque = estoqueRepo.findByProdutoId(produto.getId());
                    if (itemEstoque == null) {
                        ItemDeEstoqueModel dummyItem = new ItemDeEstoqueModel(produto, 0, 0, 0);
                        dummyItem.setListado(false);
                        return ProdutoEstoqueDTO.fromModels(produto, dummyItem);
                    }
                    return ProdutoEstoqueDTO.fromModels(produto, itemEstoque);
                })
                .collect(Collectors.toList());
    }

    public ProdutoModel produtoPorCodigo(long id) {
        return produtosRepo.findById(id).orElse(null);
    }

    public int qtdadeEmEstoque(long idProduto) {
        ItemDeEstoqueModel item = estoqueRepo.findByProdutoId(idProduto);
        return (item != null && item.isListado()) ? item.getQuantidade() : 0;
    }

    @Transactional
    public void baixaEstoque(long idProduto, int qtdade) {
        ItemDeEstoqueModel item = estoqueRepo.findByProdutoId(idProduto);
        if (item == null || !item.isListado()) {
            throw new IllegalArgumentException("Produto com ID " + idProduto + " não encontrado ou não listado no estoque para dar baixa.");
        }
        if (item.getQuantidade() < qtdade) {
            throw new IllegalArgumentException("Quantidade em estoque (" + item.getQuantidade() + ") insuficiente para o produto ID " + idProduto + " (solicitado: " + qtdade + ").");
        }
        item.setQuantidade(item.getQuantidade() - qtdade);
        estoqueRepo.save(item);
    }

    // ==========================================================
    // == GARANTA QUE ESTE MÉTODO ESTEJA PRESENTE E CORRETO ==
    // ==========================================================
    @Transactional
    public void entradaEstoque(long idProduto, int qtdade) {
        ItemDeEstoqueModel item = estoqueRepo.findByProdutoId(idProduto);
        if (item == null) {
            throw new IllegalArgumentException("Produto com ID " + idProduto + " não encontrado no estoque para dar entrada.");
        }
         if (qtdade <= 0) {
            throw new IllegalArgumentException("A quantidade de entrada deve ser positiva.");
        }
        item.setQuantidade(item.getQuantidade() + qtdade);
        // Garante que o item fique listado ao dar entrada (opcional, mas faz sentido)
        item.setListado(true); 
        estoqueRepo.save(item);
    }
    // ==========================================================

    @Transactional
    public ProdutoModel adicionarNovoProduto(NovoProdutoRequestDTO novoProdutoInfo) {
        if (novoProdutoInfo == null || novoProdutoInfo.getDescricao() == null || novoProdutoInfo.getDescricao().isEmpty() || novoProdutoInfo.getPrecoUnitario() <= 0) {
             throw new IllegalArgumentException("Dados inválidos para novo produto.");
        }
        
        ProdutoModel novoProduto = new ProdutoModel(
                novoProdutoInfo.getDescricao(),
                novoProdutoInfo.getPrecoUnitario());
        ProdutoModel produtoCadastrado = produtosRepo.save(novoProduto);

        ItemDeEstoqueModel novoItemEstoque = new ItemDeEstoqueModel(
                produtoCadastrado,
                novoProdutoInfo.getQuantidadeInicialEstoque(),
                novoProdutoInfo.getEstoqueMin(),
                novoProdutoInfo.getEstoqueMax()
        );
        estoqueRepo.save(novoItemEstoque);
        return produtoCadastrado;
    }

    @Transactional
    public ProdutoModel editarProduto(long produtoId, ProdutoDTO produtoEditadoInfo) {
        ProdutoModel produtoExistente = produtosRepo.findById(produtoId)
            .orElseThrow(() -> new IllegalArgumentException("Produto com ID " + produtoId + " não encontrado para edição."));
            
        produtoExistente.setDescricao(produtoEditadoInfo.getDescricao());
        produtoExistente.setPrecoUnitario(produtoEditadoInfo.getPrecoUnitario());
        
        return produtosRepo.save(produtoExistente);
    }

    @Transactional
    public boolean desativarProduto(long produtoId) {
        ItemDeEstoqueModel item = estoqueRepo.findByProdutoId(produtoId);
        if (item != null) {
            item.setListado(false);
            estoqueRepo.save(item);
            return true;
        }
        return false;
    }
}