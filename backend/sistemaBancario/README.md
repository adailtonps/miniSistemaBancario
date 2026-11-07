# Backend — Mini Sistema Bancário

Backend da aplicação **Mini Sistema Bancário**, desenvolvido com Java e Spring Boot, responsável pelas regras de negócio, autenticação, gerenciamento de contas, transações e pagamentos.

## Tecnologias

- Java 17
- Spring Boot 3.3.2
- Spring Security
- JWT
- BCrypt
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- Docker
- REST API

## URL

**Produção:**

`https://minisistemabancario.onrender.com`

## Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação.

Após realizar o login, o token deve ser enviado nas requisições protegidas:

```http
Authorization: Bearer <token>
```

As senhas dos usuários são armazenadas utilizando **BCrypt**.

### Login

```http
POST /auth/login
```

Retorna um JWT para autenticar o usuário.

### Cadastro

```http
POST /auth/cadastro
```

Retorna os dados básicos do cliente criado.

---

# Endpoints

## Autenticação

### `POST /auth/cadastro`

Cria um novo cliente e sua conta bancária.

**Retorna:** dados do cliente criado.

```json
{
  "id": 3,
  "nome": "Usuario",
  "email": "usuario@gmail.com"
}
```

### `POST /auth/login`

Realiza o login do cliente.

**Retorna:** token JWT e mensagem de sucesso.

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "mensagem": "Login realizado com sucesso!"
}
```

### `POST /auth/esqueci-senha`

Inicia o processo de recuperação de senha.

**Retorna:** mensagem relacionada ao envio/início da recuperação de senha.

---

## Cliente

### `PATCH /clientes/me`

Atualiza os dados do cliente autenticado.

**Retorna:** dados atualizados do cliente.

```json
{
  "id": 3,
  "nome": "Novo Nome",
  "email": "novo@email.com"
}
```

---

## Conta

### `GET /conta/minha-conta`

Consulta os dados da conta do cliente autenticado.

**Retorna:** informações da conta, cliente, saldo e status.

```json
{
  "id": 3,
  "StatusConta": "ATIVADA",
  "Saldo": 0.00,
  "emailCliente": "usuario@gmail.com",
  "nomeCliente": "Usuario"
}
```

### `GET /conta/me/saldo`

Consulta o saldo da conta.

**Retorna:**

```json
{
  "saldo": 0.00
}
```

### `POST /conta/me/deposito`

Realiza um depósito na conta.

**Retorna:** dados da transação realizada.

```json
{
  "idTransacao": 1,
  "valor": 200,
  "transacaoTipo": "DEPOSITO",
  "dataHoraTransacao": "2026-04-30T00:06:40.373371432"
}
```

### `POST /conta/me/saque`

Realiza um saque.

**Retorna:** dados da transação realizada.

```json
{
  "idTransacao": 2,
  "valor": 100,
  "transacaoTipo": "SAQUE",
  "dataHoraTransacao": "2026-04-30T00:08:25.781652"
}
```

### `PUT /conta/me/desativar`

Desativa a conta do cliente.

**Retorna:**

```json
{
  "mensagem": "Conta Desativada com sucesso!"
}
```

### `PUT /conta/me/ativar`

Ativa novamente a conta.

**Retorna:**

```json
{
  "mensagem": "Conta Ativada com sucesso!"
}
```

### `DELETE /conta/me`

Exclui o cliente e sua conta.

**Retorna:**

```json
{
  "mensagem": "Cliente apagado com sucesso!"
}
```

---

## Histórico

### `GET /historico`

Consulta o histórico de transações do cliente autenticado.

**Retorna:** lista de transações, podendo utilizar filtros.

```json
[
  {
    "idTransacao": 2,
    "valor": 100.00,
    "transacaoTipo": "SAQUE",
    "dataHoraTransacao": "2026-04-30T00:08:25.781652"
  },
  {
    "idTransacao": 1,
    "valor": 200.00,
    "transacaoTipo": "DEPOSITO",
    "dataHoraTransacao": "2026-04-30T00:06:40.373371"
  }
]
```

---

## Transferência

### `POST /transferir`

Realiza uma transferência para outra conta.

**Retorna:**

```text
Transferência realizada com sucesso!
```

---

## Pagamentos

### `POST /pagamento/gerar`

Gera um pagamento para integração com o sistema de e-commerce.

**Autenticação:** API Key através do header `X-Service-Key`.

**Retorna:** dados do pagamento gerado, incluindo código, valor, pedido e status.

### `POST /pagamento/realizar`

Realiza o pagamento utilizando um pagamento previamente gerado.

**Autenticação:** JWT.

**Retorna:** `200 OK` após a realização do pagamento.

---

## Integração com o E-commerce

O backend também possui integração com o sistema de e-commerce para atualização do status dos pagamentos.

Após um pagamento ser concluído ou cancelado, o sistema pode realizar um **callback para o e-commerce**, atualizando o status do pedido.
