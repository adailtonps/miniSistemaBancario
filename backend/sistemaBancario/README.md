# sistemaBancario
## Endpoints disponíveis:
## URL = https://minisistemabancario.onrender.com
## Criar um conta:
POST /auth/cadastro
Cria uma nova conta
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



