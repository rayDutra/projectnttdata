# Gestor de Transações Financeiras

## Resumo do Projeto
Aplicação para gerenciar transações financeiras, como depósitos, retiradas e transferências. O sistema inclui funcionalidades de análise de despesas, importação em massa de dados, autenticação e autorização robustas, além de integração com APIs externas. A solução é construída com Spring Boot e utiliza Docker para facilitar a configuração do ambiente de desenvolvimento.

---

## Índice
1. [Funcionalidades Principais](#funcionalidades-principais)  
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)  
3. [Pré-requisitos](#pré-requisitos)  
4. [Configuração do Ambiente](#configuração-do-ambiente)
5. [Documentação da API](#documentação-da-api)  

---

## Funcionalidades Principais
- **CRUD de Usuários e Transações**: Gerenciar perfis de usuários e transações financeiras, como depósitos, retiradas e transferências.
- **Cadastro em Massa por Upload**: Importação de dados de usuários a partir de planilhas Excel.
- **Análise de Despesas**: Visualização de gráficos e relatórios categorizados sobre despesas financeiras.
- **Autenticação e Autorização**: Integração com Spring Security para proteger rotas sensíveis.
- **Integração com APIs**: Consumo de APIs públicas para taxas de câmbio e mock para dados de saldo bancário.
- **Documentação OpenAPI**: Documentação automatizada dos endpoints da API.
- **Relatórios em PDF ou Excel**: Exportação de relatórios de transações financeiras.

---

## Tecnologias Utilizadas
- **Java** com Spring Boot
- **Spring Security**
- **Spring Data JPA** com PostgreSQL
- **Docker** e **Docker Compose**
- **Springdoc OpenAPI**
- **Apache POI** para manipulação de arquivos Excel
- **Junit 5** e **Mockito** para testes

---

## Pré-requisitos
- Docker e Docker Compose instalados
- Java 17 ou superior
- Maven

---

## Configuração do Ambiente
1. Clone este repositório:
   ```bash
   git clone https://github.com/rayDutra/projectnttdata.git
   cd nttbank

---

## Documentação da API
1. Swagger UI: Interface gráfica para explorar os endpoints da API.
URL: http://localhost:8080/swagger-ui.html

2. API Docs: Documentação JSON para integração com ferramentas externas.
URL: http://localhost:8080/v3/api-docs

