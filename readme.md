# README - Sistema de Vendas (sist-vendas)

Este arquivo README fornece instruções sobre como configurar o ambiente e executar o projeto Spring Boot "sist-vendas".

## 1. Pré-requisitos

Certifique-se de ter os seguintes softwares instalados em seu sistema:

* **Java Development Kit (JDK)**:
    * **Versão Utilizada no Projeto**: Java 21
    * Você pode baixar o JDK de sites como o [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.java.net/).
    * Após a instalação, verifique se a variável de ambiente `JAVA_HOME` está configurada corretamente e se o `java` está no PATH do seu sistema.
        ```bash
        java -version
        ```
* **Apache Maven**:
    * **Versão do Wrapper Utilizada no Projeto**: Maven 3.9.5 (conforme `maven-wrapper.properties`)
    * Embora o projeto inclua um Maven Wrapper (`mvnw`), ter o Maven instalado globalmente pode ser útil. Você pode baixá-lo em [maven.apache.org](https://maven.apache.org/download.cgi).
    * Após a instalação, verifique se a variável de ambiente `MAVEN_HOME` (ou `M2_HOME`) está configurada e se o `mvn` está no PATH.
        ```bash
        mvn -version
        ```
* **Git** (Opcional, mas recomendado para clonar o repositório):
    * Para clonar o projeto de um repositório Git. Você pode baixá-lo em [git-scm.com](https://git-scm.com/downloads).

## 2. Configuração do Projeto

1.  **Clone o Repositório** (se aplicável):
    Se você estiver obtendo o projeto de um repositório Git, clone-o para o seu sistema local:
    ```bash
    git clone <URL_DO_REPOSITORIO_GIT>
    cd sist-vendas
    ```
    Se você já possui os arquivos do projeto (como os fornecidos no contexto), pule esta etapa e navegue até o diretório raiz do projeto `sist-vendas` que contém o arquivo `pom.xml`.

2.  **Verifique a Estrutura do Projeto**:
    Certifique-se de que você está no diretório raiz do projeto `sist-vendas`, onde o arquivo `pom.xml` está localizado.

## 3. Executando o Projeto com Maven Spring Boot Plugin

O projeto está configurado para usar o Spring Boot Maven Plugin, que facilita a execução de aplicações Spring Boot.

1.  **Abra um Terminal ou Prompt de Comando**:
    Navegue até o diretório raiz do projeto `sist-vendas`.

2.  **Use o Maven Wrapper (Recomendado)**:
    O Maven Wrapper (`mvnw` para Linux/macOS ou `mvnw.cmd` para Windows) garante que você use a versão do Maven especificada e configurada para o projeto.

    * No Linux ou macOS:
        ```bash
        ./mvnw spring-boot:run
        ```
        (Se `./mvnw` não for executável, execute `chmod +x ./mvnw` primeiro)

    * No Windows:
        ```bash
        .\mvnw.cmd spring-boot:run
        ```

3.  **Use o Maven Global** (Alternativa, se o wrapper não estiver disponível ou se preferir):
    Se você tem o Maven instalado globalmente e configurado no PATH:
    ```bash
    mvn spring-boot:run
    ```

4.  **Aguarde a Inicialização**:
    O Maven irá baixar as dependências (se for a primeira vez ou se houverem novas dependências) e compilar o projeto. Após isso, o Spring Boot iniciará a aplicação.
    Você verá logs no console, e a aplicação geralmente estará disponível em `http://localhost:8080` (a menos que uma porta diferente seja especificada no arquivo `application.properties`).

    O arquivo `application.properties` define o nome da aplicação como `sist-vendas`.
    O arquivo `pom.xml` especifica a versão do Java como 21 e a versão do Spring Boot Starter Parent como 3.2.3.

## 4. Acessando a Aplicação

Após a inicialização bem-sucedida, você pode acessar os endpoints da aplicação:

* **Página de Boas-vindas**:
    * O controller principal (`Controller.java`) redireciona a rota raiz `/` para `/welcome.html`.
    * Acesse: `http://localhost:8080/` ou `http://localhost:8080/welcome.html`
* **Interface Principal do Sistema de Vendas**:
    * A página `index.html` parece ser a interface principal para interagir com as funcionalidades de produtos e orçamentos.
    * Acesse: `http://localhost:8080/index.html`

## 5. Parando a Aplicação

Para parar a aplicação Spring Boot que está rodando no terminal, geralmente você pode pressionar `Ctrl+C` no mesmo terminal onde o comando `mvn spring-boot:run` foi executado.

---

Este README deve fornecer os passos essenciais para executar o projeto `sist-vendas`. Consulte os arquivos `pom.xml` e `application.properties` para mais detalhes sobre as configurações e dependências do projeto.