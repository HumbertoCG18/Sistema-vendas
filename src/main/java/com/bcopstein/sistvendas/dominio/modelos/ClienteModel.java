package com.bcopstein.sistvendas.dominio.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
@Entity
public class ClienteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Nome não pode ser nulo
    private String nomeCompleto;
    
    @Column(unique = true) // CPF pode ser único, se desejado
    private String cpf;
    private String email;

    // Construtor padrão para JPA
    protected ClienteModel() {}

    // Construtor para criar um novo cliente
    public ClienteModel(String nomeCompleto, String cpf, String email) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf; // Validar formato do CPF seria uma boa prática
        this.email = email; // Validar formato do email
    }

    // Getters
    public Long getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }

    // Setters (use com cuidado, especialmente para ID)
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEmail(String email) { this.email = email; }

}