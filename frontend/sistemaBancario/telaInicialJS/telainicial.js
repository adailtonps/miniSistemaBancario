//configs and endpoints
const URL_API = "https://minisistemabancario.onrender.com";

const endpoints = {
    minha_conta: URL_API + "/conta/minha-conta",
    saldo: URL_API + "/conta/me/saldo",
    ativar: URL_API + "/conta/me/ativar",
    desativar: URL_API + "/conta/me/desativar",
    historico: URL_API + "/conta/me/historico",
    atualizar: URL_API + "/clientes/me",
    apagar: URL_API + "/conta/me",
    saque: URL_API + "/conta/me/saque",
    deposito: URL_API + "/conta/me/deposito",
    transferencia: URL_API + "/transacoes/transferencia",
    logout: URL_API + "/auth/logout"
};

//authenticaton
async function apiFetch(url, options = {}) {
    return fetch(url, {
        ...options,
        credentials:"include",
        headers:{
            "Content-Type":"application/json",
            ...(options.headers || {})
        }
    })
}


//variable
let saldoAtual = 0;


//section
function mostrarSecao(id) {
    document.querySelectorAll(".secao").forEach(secao => secao.style.display = "none");
    document.getElementById(id).style.display = "block";
}

async function handleResponse(response) {
    if (response.status === 401 || response.status === 403) {
        alert("Sua sessão expirou. Faça login novamente.");
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1000);
        throw new Error("Sessão expirada. Faça login novamente!")
    }

    if (!response.ok) {
        let mensagemErro;
        try{
        const erroJson = await response.json()
        mensagemErro = erroJson.mensagem;
    } catch{
        mensagemErro = await response.text()
    }
    throw new Error(mensagemErro || "Erro desconhecido!")
}
    return response.json().catch(() => null);
}


//account
async function minhaConta() {
    try {
        const dados = await handleResponse(await apiFetch(endpoints.minha_conta))
        document.getElementById("msgConta").textContent =
            `Id: ${dados.id} | Status: ${dados.StatusConta} | Saldo: R$ ${Number(dados.Saldo).toFixed(2)} | Email: ${dados.emailCliente} | Nome: ${dados.nomeCliente}`;
    } catch (erro) {
        alert("Erro ao carregar conta: " + erro.message);
    }
}


//balance
async function carregarSaldo() {
    try {
        const dados = await handleResponse(await apiFetch(endpoints.saldo))
        saldoAtual = Number(dados.saldo ?? dados)
        document.getElementById("valorSaldo").textContent = "R$ " + saldoAtual.toFixed(2);
    } catch (erro) {
        alert("Erro ao carregar saldo: " + erro.message);
    }
}


//withdraw
async function sacar() {
    const valor = parseFloat(document.getElementById("valorSaque").value);
    if (!valor || valor <= 0) return alert("Valor inválido!");
    if (valor > saldoAtual) return alert("Saldo insuficiente!");

    try {
        await handleResponse(await apiFetch(endpoints.saque, {
            method: "POST",
            body: JSON.stringify({ valor })
        }));
        alert("Valor sacado com sucesso!");
        document.getElementById("valorSaque").value = "";
        await carregarSaldo();
    } catch (erro) {
        alert(erro.message);
    }
}


//deposit
async function depositar() {
    const valor = parseFloat(document.getElementById("valorDeposito").value);
    if (!valor || valor <= 0) return alert("Valor inválido!");

    try {
        await handleResponse(await apiFetch(endpoints.deposito, {
            method: "POST",
            body: JSON.stringify({ valor })
        }));
        alert("Valor depositado com sucesso!");
        document.getElementById("valorDeposito").value = "";
        await carregarSaldo();
    } catch (erro) {
        alert(erro.message);
    }
}


//transfer
async function transferencia() {
    const destinoId = Number(document.getElementById("idDestino").value);
    const valor = parseFloat(document.getElementById("valorTransferencia").value);
    const senha = document.getElementById("senhaTransferir").value;

    if (!destinoId || !valor || valor <= 0 || !senha) 
        return alert("Preencha os campos corretamente");

    try {
        await handleResponse(await apiFetch(endpoints.transferencia, {
            method: "POST",
            body: JSON.stringify({ destinoId: Number(destinoId), valor:Number(valor), senha:senha})
        }));
        alert("Transferência realizada com sucesso!");
        await carregarSaldo();
    } catch (erro) {
        alert("Erro na transferência: " + erro.message);
    }
}


//manage account
async function gerenciarConta() {
    const div = document.getElementById("msg");
    div.innerHTML = `
        <button id="btnStatusConta">Status da Conta</button>
        <button id="btnAtualizar">Atualizar Dados</button>
        <button id="btnHistorico">Histórico de Transferências</button>
        <button id="btnDeletar">Apagar Conta</button>
    `;

    document.getElementById("btnStatusConta").addEventListener("click", carregarStatus);
    document.getElementById("btnAtualizar").addEventListener("click", mostrarFormularioAtualizacao);
    document.getElementById("btnHistorico").addEventListener("click", historicoTransferencias);
    document.getElementById("btnDeletar").addEventListener("click", apagarConta);
}


//account status
async function carregarStatus() {
    try {
        const dados = await handleResponse(await apiFetch(endpoints.minha_conta));
        const div = document.getElementById("msg");
        div.innerHTML = `
            <h3>Status da Conta</h3>
            <p><strong>Status:</strong> ${dados.StatusConta === "ATIVADA" ? "Ativada" : "Desativada"}</p>
            <button id="btnAtivar">Ativar</button>
            <button id="btnDesativar">Desativar</button>
        `;
        document.getElementById("btnAtivar").addEventListener("click", () => alterarStatus(true));
        document.getElementById("btnDesativar").addEventListener("click", () => alterarStatus(false));
    } catch (erro) {
        alert("Erro ao carregar status: " + erro.message);
    }
}


//change status
async function alterarStatus(ativar) {
    const url = ativar ? endpoints.ativar : endpoints.desativar;
    try {
        const mensagem = await handleResponse(await apiFetch(url, { method: "PUT"}));
        alert(mensagem?.mensagem || "Status alterado com sucesso!");
        carregarStatus();
    } catch (erro) {
        alert("Erro ao alterar status: " + erro.message);
    }
}


//update data
function mostrarFormularioAtualizacao() {
    const div = document.getElementById("msg");
    div.innerHTML = `
        <h3>Atualizar Dados</h3>
        <input type="text" id="novoNome" placeholder="Novo nome">
        <input type="email" id="novoEmail" placeholder="Novo email">
        <input type="password" id="senhaUser" placeholder="Digite sua senha">
        <button id="btnSalvarAtualizacao">Salvar</button>
        <p>ATENÇÃO: Caso atualize o email, você será redirecionado para a tela de login, entre com o email atualizado!<br>
        ⚠ Para alterar senha, use "Esqueci minha senha" na tela de login.</p>
    `;
    document.getElementById("btnSalvarAtualizacao").addEventListener("click", alterarDados);
}

//update data
async function alterarDados() {
    const nome = document.getElementById("novoNome").value.trim();
    const email = document.getElementById("novoEmail").value.trim();
    const senha = document.getElementById("senhaUser").value
    if (!nome && !email) 
        return alert("Preencha pelo menos um campo!");
    if(!senha)
        return alert("Preencha a senha!")
    try {
        const resposta = await handleResponse(await apiFetch(endpoints.atualizar, {
            method: "PATCH",
            body: JSON.stringify({ nome: nome, email:email, senha:senha})
        }));
        alert("Informações atualizadas com sucesso!")
        if(email !== ""){
        window.location.href="login.html";
        }
    }  catch (erro){
        alert("Erro ao atualizar dados: "+erro.message);
    }   
}

//date
function formatarData(dataISO){
    if(!dataISO) return "Ainda não realizado!"
    const data = new Date(dataISO)
     return data.toLocaleString("pt-BR",{
        dateStyle: "short",
        timeStyle:"medium"
     })
}


//history transfer
async function historicoTransferencias() {
    try {
        const dados = await handleResponse(await apiFetch(endpoints.historico));
        if (!dados || dados.length === 0) return document.getElementById("msg").innerHTML = "<p>Nenhuma movimentação encontrada.</p>";

        let html = "<h3>Histórico de Transações</h3>";
        dados.forEach(transferencia => {
            html += `
                <div class="item-historico">
                    <p><strong>ID Transação:</strong> ${transferencia.id}</p>
                    <p><strong>Tipo:</strong> ${transferencia.transacaoTipo}</p>
                    <p><strong>Data e Hora:</strong> ${formatarData(transferencia.dataHoraTransacao)}</p>
                    <p><strong>Valor:</strong> R$ ${Number(transferencia.valor).toFixed(2)}</p>
                </div>`;
        });
        document.getElementById("msg").innerHTML = html;
    } catch (erro) {
        alert("Erro ao carregar histórico: " + erro.message);
    }
}



//delete account
async function apagarConta() {
    const div = document.getElementById("msg");
    div.innerHTML = `
        <h3>Apagar a Conta</h3>
        <h4>⚠️ Só pode apagar se o status estiver desativado e saldo zerado!</h4>
        <input type="email" id="email" placeholder="Email"><br>
        <input type="password" id="senha" placeholder="Senha"><br>
        <button id="confirmarDelete">Apagar</button>
    `;
    document.getElementById("confirmarDelete").addEventListener("click", async () => {
        const email = document.getElementById("email").value;
        const senha = document.getElementById("senha").value;
        if (!email || !senha) return alert("Preencha todos os campos!");

        try {
            await handleResponse(await apiFetch(endpoints.apagar, {
                method: "DELETE",
                body: JSON.stringify({ email, senha })
            }));
            alert("Conta apagada com sucesso!");
            window.location.href = "login.html";
        } catch (erro) {
            alert("Erro ao apagar conta: " + erro.message);
        }
    });
}


//logout
document.getElementById("btnLogout").addEventListener("click", async () => {
    await apiFetch(endpoints.logout, {
        method:"POST"
    })
    window.location.href = "login.html";
});



//buttons
document.getElementById("btnConta").addEventListener("click", () => { mostrarSecao("conta"); minhaConta(); });
document.getElementById("btnsaldo").addEventListener("click", () => { mostrarSecao("saldo"); carregarSaldo(); });
document.getElementById("btnsaque").addEventListener("click", () => mostrarSecao("saque"));
document.getElementById("btntransferencia").addEventListener("click", () => mostrarSecao("transferencia"));
document.getElementById("btndeposito").addEventListener("click", () => mostrarSecao("deposito"));
document.getElementById("btngerenciarConta").addEventListener("click", () => { mostrarSecao("gerenciarConta"); gerenciarConta(); });
document.getElementById("confirmarDeposito").addEventListener("click", depositar);
document.getElementById("confirmarSaque").addEventListener("click", sacar);
document.getElementById("confirmarTransferencia").addEventListener("click", transferencia);