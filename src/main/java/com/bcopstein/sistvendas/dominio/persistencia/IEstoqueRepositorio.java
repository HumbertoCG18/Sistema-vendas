package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Adicionar import
import org.springframework.stereotype.Repository;

@Repository
public interface IEstoqueRepositorio extends JpaRepository<ItemDeEstoqueModel, Long> {
    ItemDeEstoqueModel findByProdutoId(long produtoId);
    List<ItemDeEstoqueModel> findByListadoTrueAndQuantidadeGreaterThan(int quantidade);

    // A query JPQL 'e.quantidade < e.estoqueMin' compara os campos da entidade.
    @Query("SELECT e FROM ItemDeEstoqueModel e WHERE e.quantidade < e.estoqueMin ORDER BY e.produto.descricao ASC")
    List<ItemDeEstoqueModel> findItensComEstoqueBaixo();
}