#  Mini Sistema Bancário

Sistema bancário desenvolvido em **Java e Spring Boot**, com frontend em **HTML, CSS e JavaScript**, criado para aplicar na prática conceitos de desenvolvimento de software, APIs REST, segurança, persistência de dados e regras de negócio.

O sistema permite o gerenciamento de clientes e contas, realização de operações financeiras, consulta de histórico e processamento de pagamentos. Além disso, possui integração com um sistema de e-commerce desenvolvido separadamente, permitindo utilizar o sistema bancário como parte do fluxo de pagamento das compras.

O projeto foi desenvolvido de forma incremental, evoluindo de uma aplicação bancária básica para uma aplicação com autenticação, segurança, processamento de pagamentos, integração entre sistemas, tarefas automatizadas e deploy em produção.

##  Objetivo

O objetivo principal do projeto é simular um sistema bancário e, ao mesmo tempo, praticar conceitos utilizados no desenvolvimento de aplicações reais, principalmente no backend.

Durante o desenvolvimento foram aplicados conceitos como:

Arquitetura em camadas, desenvolvimento de APIs REST, autenticação e segurança, persistência de dados, validação de regras de negócio, tratamento de exceções, integração entre sistemas e deploy de aplicações.

##  Principais funcionalidades

###  Gerenciamento de clientes e contas

Cadastro e autenticação de usuários, atualização de dados cadastrais, consulta de conta e saldo, ativação e desativação de contas e gerenciamento do ciclo de vida da conta de acordo com as regras de negócio.

###  Operações financeiras

O sistema permite realizar operações bancárias como:

Depósitos, saques e transferências entre contas.

Também é possível consultar o histórico das movimentações e utilizar filtros para facilitar a consulta das transações.

###  Sistema de pagamentos

O projeto possui um fluxo próprio de pagamentos que permite a integração com sistemas externos.

Os pagamentos possuem código próprio, controle de status, prazo de validade e processamento automático de pagamentos expirados.

Quando um pagamento é realizado ou cancelado, o sistema pode comunicar o resultado ao sistema de e-commerce integrado.

###  Integração com E-commerce

Um dos principais recursos do projeto é a integração com um segundo sistema desenvolvido em **Spring Boot**.

O e-commerce pode solicitar ao sistema bancário a geração de um pagamento. Após o processamento do pagamento, o sistema bancário comunica o resultado ao e-commerce, permitindo que o pedido tenha seu status atualizado.

Fluxo simplificado:

```text
E-commerce
    ↓
Solicitação de pagamento
    ↓
Sistema Bancário
    ↓
Geração do código
    ↓
Pagamento
    ↓
Atualização do status
    ↓
Retorno para o E-commerce
```

Essa integração permite aplicar conceitos de **comunicação entre APIs, integração entre sistemas e processamento de pagamentos**.

##  Arquitetura

O backend utiliza uma **arquitetura em camadas**, separando responsabilidades entre diferentes componentes da aplicação.

```text
Controller
     ↓
Service
     ↓
Repository
     ↓
Database
```

O projeto também possui separação entre backend e frontend:

```text
miniSistemaBancario
│
├── backend
│   └── API REST
│
└── frontend
    └── Interface Web
```

O backend é responsável pelas regras de negócio, segurança, persistência e disponibilização da API REST.

O frontend é responsável pela interface utilizada pelos usuários e pela comunicação com a API.

A documentação detalhada de cada parte está disponível nos respectivos diretórios.

##  Segurança

O sistema utiliza recursos de segurança para proteger os dados e operações dos usuários.

Entre os principais recursos utilizados estão:

Spring Security, autenticação com JWT, BCrypt para armazenamento seguro de senhas, controle de acesso autenticado, API Keys para comunicação entre serviços e configuração de CORS.

A implementação detalhada da segurança e o funcionamento dos filtros podem ser consultados na documentação do backend.

##  Persistência de dados

O projeto utiliza **PostgreSQL** como banco de dados, com **Spring Data JPA e Hibernate** para persistência e mapeamento das entidades.

O domínio possui relacionamentos entre clientes, contas, transações, pagamentos e tokens de recuperação de senha.

Valores monetários são tratados utilizando `BigDecimal`, buscando maior precisão nas operações financeiras.

##  Automação

O sistema possui tarefas agendadas para verificar pagamentos pendentes que ultrapassaram o prazo de validade.

Quando um pagamento expira, ele é automaticamente marcado como cancelado e o sistema comunica essa alteração ao e-commerce integrado.

##  Frontend

O frontend foi desenvolvido utilizando:

HTML5  
CSS3  
JavaScript

A aplicação consome a API REST disponibilizada pelo backend e fornece as interfaces necessárias para interação com as funcionalidades do sistema.

A estrutura do frontend, páginas, integração com a API e demais detalhes estão documentados no README localizado dentro da pasta `frontend`.

##  Principais tecnologias

### Backend

Java 17  
Spring Boot  
Spring Security  
JWT  
Spring Data JPA  
Hibernate  
PostgreSQL  
Maven

### Frontend

HTML5  
CSS3  
JavaScript

### Infraestrutura

Docker  
Render  
Vercel  
Git/GitHub  
Variáveis de ambiente  
Spring Profiles

##  Deploy

O projeto está dividido em duas aplicações independentes.

### Backend

API desenvolvida com Spring Boot e hospedada no **Render**.

https://minisistemabancario.onrender.com

### Frontend

Aplicação web desenvolvida em HTML, CSS e JavaScript e hospedada na **Vercel**.

https://mini-sistema-bancario.vercel.app

##  Documentação

O projeto possui documentação separada para facilitar a consulta de cada parte da aplicação.

### Backend

Contém informações sobre a estrutura da API, arquitetura, segurança, banco de dados, regras de negócio e endpoints.

 `backend/README.md`

### Frontend

Contém informações sobre a estrutura da aplicação web, páginas, componentes, integração com a API e funcionamento do frontend.

 `frontend/README.md`

##  Conhecimentos aplicados

O desenvolvimento do projeto permitiu aplicar conceitos de:

Desenvolvimento de APIs REST  
Programação Orientada a Objetos  
Arquitetura em camadas  
Injeção de dependências  
Spring Security  
Autenticação com JWT  
Hash de senhas com BCrypt  
JPA e Hibernate  
Relacionamentos entre entidades  
DTOs  
Validação de dados  
Tratamento de exceções  
Transações  
Filtros dinâmicos  
Integração entre APIs  
Comunicação entre sistemas  
Tarefas agendadas  
Docker  
Variáveis de ambiente  
Deploy em produção  
Desenvolvimento frontend integrado a uma API REST

##  Sobre o projeto

Este projeto faz parte da minha evolução prática em **desenvolvimento de software com Java e Spring Boot**.

O sistema começou como uma aplicação bancária simples e foi evoluindo conforme novos conceitos foram estudados e aplicados, incorporando recursos de segurança, operações financeiras, processamento de pagamentos, integração com outro sistema, frontend e deploy em produção.
