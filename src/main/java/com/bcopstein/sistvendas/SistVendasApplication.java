package com.bcopstein.sistvendas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.bcopstein.sistvendas.dominio.modelos"}) // Escaneia entidades
@EnableJpaRepositories(basePackages = {"com.bcopstein.sistvendas.dominio.persistencia"}) // Habilita repositórios JPA
public class SistVendasApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistVendasApplication.class, args);
    }

}