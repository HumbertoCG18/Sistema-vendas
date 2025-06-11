# Sistema de Vendas - Lojas ACME

![Java](https://img.shields.io/badge/java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)
![Maven](https://img.shields.io/badge/build-maven-red.svg)
![Arquitetura](https://img.shields.io/badge/arquitetura-Clean-informational.svg)

---

### 1. Introdução

Este projeto é o backend de um sistema de vendas completo para as "Lojas ACME". Desenvolvido com foco em boas práticas de engenharia de software, o sistema gerencia o catálogo de produtos, controle de estoque, criação de orçamentos com cálculos de impostos e descontos, efetivação de vendas e consultas gerenciais.

### 2. Arquitetura do Sistema

A aplicação foi estruturada seguindo os princípios da **Arquitetura Limpa (Clean Architecture)**. Essa abordagem garante um sistema desacoplado, testável e de fácil manutenção, separando as responsabilidades em quatro camadas principais:

-   **Domínio:** O núcleo do sistema. Contém as entidades de negócio (`ClienteModel`, `OrcamentoModel`, etc.) e os serviços de domínio (`ServicoDeVendas`, `ServicoDeEstoque`) que encapsulam as regras de negócio mais importantes.
-   **Aplicação:** Contém os Casos de Uso (Use Cases, ex: `CriaOrcamentoUC`), que orquestram a execução das funcionalidades do sistema, conectando as camadas externas à lógica de domínio.
-   **Adaptadores de Interface:** A "cola" do sistema. Inclui o `Controller` (que adapta as requisições HTTP para chamadas de Casos de Uso) e as implementações dos Repositórios (que adaptam a necessidade de persistência do domínio para a tecnologia de banco de dados).
-   **Frameworks e Drivers:** A camada mais externa, composta por tecnologias como Spring Boot, JPA/Hibernate e o banco de dados H2.

### 3. Principais Tecnologias

-   **Java 21:** Versão da linguagem Java utilizada no projeto.
-   **Spring Boot 3.2.3:** Framework principal para a criação da aplicação, facilitando a configuração e o desenvolvimento.
-   **Spring Data JPA & Hibernate:** Para a camada de persistência, abstraindo o acesso ao banco de dados e mapeando objetos Java para tabelas (ORM).
-   **Maven:** Ferramenta de gerenciamento de dependências e build do projeto.
-   **H2 Database:** Banco de dados em memória, ideal para ambientes de desenvolvimento e testes rápidos.

### 4. Pré-requisitos

-   **Java Development Kit (JDK):** Versão 21 ou superior.
-   **Apache Maven:** Versão 3.9 ou superior (ou utilize o Maven Wrapper incluído).

### 5. Como Executar a Aplicação

1.  **Clone o Repositório** (se aplicável):
    ```bash
    git clone <URL_DO_REPOSITORIO>
    cd sistema-vendas
    ```

2.  **Execute com o Maven Wrapper** (recomendado):
    O wrapper garante que a versão correta do Maven seja utilizada.

    -   No Linux ou macOS:
        ```bash
        ./mvnw spring-boot:run
        ```
        *(Se necessário, dê permissão de execução com `chmod +x ./mvnw`)*

    -   No Windows:
        ```bash
        .\mvnw.cmd spring-boot:run
        ```

3.  A aplicação será iniciada e estará disponível em `http://localhost:8080`.

### 6. Acessando a Aplicação

#### 6.1. Interface Web e Documentação

-   **Página de Boas-vindas:** [http://localhost:8080/](http://localhost:8080/) ou [http://localhost:8080/welcome.html](http://localhost:8080/welcome.html)
-   **Aplicação Principal:** [http://localhost:8080/index.html](http://localhost:8080/index.html)
-   **Documentação da API:** [http://localhost:8080/documentacao.html](http://localhost:8080/documentacao.html)
-   **Dependências do Projeto:** [http://localhost:8080/dependencias.html](http://localhost:8080/dependencias.html)

#### 6.2. Console do Banco de Dados H2

É possível acessar o console do banco de dados H2 diretamente pelo navegador para inspecionar as tabelas e os dados em tempo real. Esta funcionalidade está habilitada pela propriedade `spring.h2.console.enabled=true` em `application.properties`.

1.  Acesse a URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

2.  Na tela de login, utilize as seguintes configurações, que estão definidas no arquivo `application.properties`:
    -   **Driver Class:** `org.h2.Driver`
    -   **JDBC URL:** `jdbc:h2:mem:testdb`
    -   **User Name:** `sa`
    -   **Password:** (deixe em branco)

3.  Clique em **"Connect"**. Você poderá executar queries SQL diretamente no banco de dados em memória.

#### 6.3. Endpoints da API

A aplicação expõe uma API REST completa para todas as suas funcionalidades. A documentação detalhada de cada endpoint, incluindo parâmetros e exemplos de resposta, está disponível na **[Página de Documentação da API](http://localhost:8080/documentacao.html)**.

Exemplos de endpoints:
-   `POST /novoOrcamento`: Para criar um novo orçamento.
-   `GET /gerencial/volumeVendas?dataInicial=...&dataFinal=...`: Para consultar o volume de vendas.
-   `GET /todosProdutosStatus`: Para listar todos os produtos e seus estoques.