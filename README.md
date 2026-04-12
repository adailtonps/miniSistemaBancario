## Mini Sistema Bancário

Sistema em Java com Spring Boot que simula um mini sistema bancário, permitindo gerenciar contas, realizar operações financeiras, consultar histórico de transações e atualizar dados do cliente, com foco em regras de negócio, persistência de dados e tratamento de exceções.

## Tecnologias:
- Java
- Spring Boot + JWT
- MySQL
- HTML, CSS e JavaScript (frontend)
  
## Funcionalidades:
- Cadastro e autenticação de usuários
- Login com JWT
- Depósitos, saques e transferências
- Validações de segurança
- Atualização de dados

## Link do projeto:
O link do backend pode ser usado para testar as requisições via ferramentasa, como Postman ou Insomnia.
Acesse o Readme do backend para saber os endpoints disponíveis.
- Frontend: Em breve
- Backend (API): Em breve

## Como executar o projeto localmente (opcional):
- Você pode apenas acessar o frontend e utilizar o sistema
- Caso deseje executar o projeto na sua máquina:
1 - Abra o arquivo "application.properties"
2 - Substitua as variáveis pelos seus dados MySQL:
  spring.datasource.username=SEU_USUARIO
  spring.datasource.password=SUA_SENHA
  jwt.secret=QUALQUER_CHAVE
3 - Crie um banco MySQL chamado:
  sistema_bancario
4 - Clone o repositório:
  (terminal)
  git clone URL_DO_REPOSITORIO
5 - Acesse a pasta do backend:
  cd backend/miniSistemaBancario/backend/sistemaBancario/sistemaBancario
6 - Execute o projeto:
  ./mvnw spring-boot:run - Linux/Mac
  mvnw.cmd spring-boot:run - Windows
