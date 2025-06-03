package com.bcopstein.sistvendas.dominio.persistencia;

import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProdutoRepositorio extends JpaRepository<ProdutoModel, Long> {
}