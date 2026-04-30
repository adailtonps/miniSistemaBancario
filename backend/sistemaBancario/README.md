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
    "id": 1,  
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
Para acessar endpoints protegidas, copie e cole o token gerado no Authorization:  
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.exemplo.token.jwt  


## Minha Conta  



