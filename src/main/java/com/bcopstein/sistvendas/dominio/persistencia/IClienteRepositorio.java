package com.bcopstein.sistvendas.dominio.persistencia;

import com.bcopstein.sistvendas.dominio.modelos.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface IClienteRepositorio extends JpaRepository<ClienteModel, Long> {
    Optional<ClienteModel> findByCpf(String cpf); // Para buscar/verificar por CPF

    Optional<ClienteModel> findByNomeCompletoIgnoreCase(String nomeCompleto); // Buscar por nome

    List<ClienteModel> findByNomeCompletoContainingIgnoreCase(String nomeParte); // Para buscas parciais
}