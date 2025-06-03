package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEstoqueRepositorio extends JpaRepository<ItemDeEstoqueModel, Long> {
    // Encontra um item de estoque pelo ID do produto associado
    ItemDeEstoqueModel findByProdutoId(long produtoId);

    // Encontra itens de estoque que estão listados e têm quantidade maior que zero
    List<ItemDeEstoqueModel> findByListadoTrueAndQuantidadeGreaterThan(int quantidade);
}