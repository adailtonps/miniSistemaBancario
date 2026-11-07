# Frontend — Mini Sistema Bancário

Frontend da aplicação **Mini Sistema Bancário**, responsável pela interface do usuário e pela comunicação com a API REST do backend.

## Tecnologias

- HTML5
- CSS3
- JavaScript
- Bootstrap
- Fetch API
- JWT

## URL

**Aplicação:**  
`https://mini-sistema-bancario.vercel.app`

## Estrutura

```text
frontend/
│
├── cadastro/
│   ├── cadastro.css
│   └── cadastro.js
│
├── esqueci-senha/
│   ├── esqueci-senha.html
│   ├── esqueci-senha.css
│   └── esqueci-senha.js
│
├── imagens/
│   └── imagens do projeto
│
├── login/
│   ├── login.html
│   ├── login.css
│   └── login.js
│
├── reset-senha/
│   ├── reset-senha.html
│   ├── reset-senha.css
│   └── reset-senha.js
│
├── telaInicial/
│   ├── telaInicial.html
│   ├── telaInicial.css
│   └── telaInicial.js
│
└── index.html
```

## Páginas

| Página | Responsabilidade |
|---|---|
| `index.html` | Página inicial e Cadastro de novos clientes |
| `login.html` | Autenticação do usuário |
| `esqueci-senha.html` | Solicitação de recuperação de senha |
| `reset-senha.html` | Redefinição da senha através do token |
| `telaInicial.html` | Dashboard e operações da conta |

---

## JavaScript

### `cadastro.js`

Responsável pelo cadastro de novos clientes e navegação para a tela de login.

- Envia os dados do formulário para `/auth/cadastro`.
- Exibe mensagens de sucesso ou erro.
- Redireciona o usuário para o login após o cadastro.

### `login.js`

Responsável pela autenticação.

- Envia email e senha para `/auth/login`.
- Processa a resposta da API.
- Armazena o JWT no `localStorage`.
- Redireciona o usuário para a tela principal após o login.
- Possui uma função `apiFetch()` para realizar requisições com `Content-Type` configurado.

### `esqueci-senha.js`

Responsável pelo início da recuperação de senha.

- Envia o email para `/auth/esqueci-senha`.
- Processa o link retornado pelo backend.
- Redireciona o usuário para a tela de redefinição.

### `reset-senha.js`

Responsável pela redefinição da senha.

- Obtém o token presente na URL.
- Envia o token e a nova senha para `/auth/reset-senha`.
- Exibe o resultado da operação.
- Redireciona para o login após a alteração.

### `telaInicial.js`

Concentra as principais funcionalidades da aplicação após o login.

**Comunicação com a API**
- Centraliza os endpoints utilizados pelo frontend.
- `apiFetch()` adiciona automaticamente o JWT às requisições.
- `handleResponse()` trata respostas da API e sessões expiradas.

**Conta e saldo**
- Consulta dados da conta.
- Consulta e atualiza o saldo.
- Ativa e desativa a conta.
- Atualiza dados do cliente.
- Permite apagar a conta.

**Operações bancárias**
- Realiza saques.
- Realiza depósitos.
- Realiza transferências.
- Realiza pagamentos através de código.

**Histórico**
- Consulta o histórico de transações.
- Permite filtrar por tipo, data e valor.
- Exibe movimentações recentes.
- Separa e apresenta saques, depósitos, transferências e pagamentos.

**Interface**
- Controla a exibição das diferentes seções do dashboard.
- Atualiza dinamicamente os dados apresentados.
- Formata valores monetários e datas.
- Exibe mensagens de sucesso e erro.

---

## Autenticação no Frontend

Após o login, o JWT recebido pelo backend é armazenado no `localStorage`.

Nas requisições autenticadas, o token é enviado automaticamente:

```http
Authorization: Bearer <token>
```

Caso a API retorne `401` ou `403`, o frontend informa que a sessão expirou e redireciona o usuário para a tela de login.

## Integração com o Backend

O frontend utiliza a `Fetch API` para consumir os endpoints do backend.

```text
Usuário
   ↓
HTML / CSS
   ↓
JavaScript
   ↓
Fetch API
   ↓
Spring Boot REST API
   ↓
PostgreSQL
```

A URL utilizada pelo frontend em produção é:

`https://minisistemabancario.onrender.com` 
