# sistemaBancario
## Endpoints disponíveis:
## URL = https://minisistemabancario.onrender.com
## Criar um conta:
POST /auth/cadastro  
Responsável por criar uma nova conta
## Body:
{   
  "email": "usuario@gmail.com",  
  "nome": "Usuario",  
  "senha": "usuario123A@"  
}
## Resposta
{  
    "id": 3,  
    "nome": "Usuario",  
    "email": "usuario@gmail.com"  
}


## Login
POST /auth/login  
Responsável por fazer login
## Body
{   
  "email": "usuario@gmail.com",  
  "senha": "usuario123A@"  
}
## Resposta
{  
    "token": "eyJhbGciOiJIUzI1NiJ9.exemplo.token.jwt",  
    "mensagem": "Login realizado com sucesso!"  
}
## Como usar o token
Para acessar endpoints protegidos, copie o token gerado dento das aspas e cole no Authorization:  
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.exemplo.token.jwt  


## Minha Conta  
GET /conta/minha-conta  
Responsável por mostrar os dados da conta
## Não é necessário enviar infos no body nesse endpoint
## Resposta
{  
    "id": 3,  
    "StatusConta": "ATIVADA",  
    "Saldo": 0.00,  
    "emailCliente": "usuario@gmail.com",  
    "nomeCliente": "Usuario"  
}


# Saldo
GET /conta/me/saldo
Responsável por mostrar o saldo da conta
## Não é necessário enviar infos no body nesse endpoint
## Resposta
{  
    "saldo": 0.00  
}


## Depósito
POST /conta/me/deposito
Responsável por fazer depósitos na conta
## Body
{  
  "valor":200  
}  
## Resposta
{  
    "idTransacao": 1,  
    "valor": 200,  
    "transacaoTipo": "DEPOSITO",  
    "dataHoraTransacao": "2026-04-30T00:06:40.373371432"  
}


## Saque
POST /conta/me/saque
Responsável por fazer saques na conta
## Body
{  
  "valor":100  
}  
## Resposta
{  
    "idTransacao": 2,  
    "valor": 100,  
    "transacaoTipo": "SAQUE",  
    "dataHoraTransacao": "2026-04-30T00:08:25.781652"
}


## Histórico
GET /conta/me/historico
Responsável por mostrar todo o histórico de saques, depósitos e transferências da conta  
## Não é necessário enviar infos no body
## Resposta
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



## Atualizar dados (nome ou email)
PATCH /clientes/me  
Responsável por atualizar os dados do cliente  
## Body (somente o dado que deseja atualizar: email ou nome, se quiser pode atualizar os dois)  
## SE ATUALIZAR O EMAIL, É NECESSÁRIO FAZER LOGIN NOVAMENTE COM O NOVO EMAIL ->  POST /auth/login
{  
    "email":"user@gmail.com",  
    "nome":"user",  
    "senha":"usuario123A@"  
}
## Resposta
{  
    "id": 3,  
    "nome": "user",  
    "email": "user@gmail.com"  
}


## Transferências
POST /transacoes/transferencia
Responsável por realizar transferências para outra conta  
É necessário ter outra conta criada no sistema
## Body
{  
    "destinoId":2,  
    "valor":20,  
    "senha":"usuario123A@"  
}
## Resposta  
Transferência realizada com sucesso!


## Desativar a conta
PUT /conta/me/desativar
Responsável por desativar a conta
É necessário ter o saldo zerado para conseguir desativar
## Não é necessário infos no body para esse endpoint
## Resposta  
{
    "mensagem": "Conta Desativada com sucesso!"  
}


# Ativar a conta
PUT /conta/me/ativar
Responsável por ativar a conta
## Não é necessário infos no body para esse endpoint
## Resposta  
{
    "mensagem": "Conta Ativada com sucesso!"
}


## Apagar a conta
DELETE /conta/me
Responsável por apagar a conta
É necessário ter a conta desativada e saldo zerado para apagar a conta
## Body
{






