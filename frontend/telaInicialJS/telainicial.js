// CONFIGS & ENDPOINTS

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

// AUTH / FETCH

async function apiFetch(url, options = {}) {
    const token = localStorage.getItem("token");

    return fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { "Authorization": "Bearer " + token } : {}),
            ...(options.headers || {})
        }
    });
}

async function handleResponse(response) {
    if (response.status === 401 || response.status === 403) {
        alert("Sua sessão expirou. Faça login novamente.");

        setTimeout(() => {
            window.location.href = "login.html";
        }, 1000);

        throw new Error("Sessão expirada. Faça login novamente!");
    }

    if (!response.ok) {
        let mensagemErro;

        try {
            const erroJson = await response.json();
            mensagemErro = erroJson.mensagem;
        } catch {
            mensagemErro = await response.text();
        }

        throw new Error(mensagemErro || "Erro desconhecido!");
    }

    return response.json().catch(() => null);
}

// STATE

let saldoAtual = 0;

// UI / SECTIONS

function mostrarSecao(id) {
    document.querySelectorAll(".secao")
        .forEach(secao => secao.style.display = "none");

    document.getElementById(id).style.display = "block";
}

// ACCOUNT

async function minhaConta() {
    const msg = document.getElementById("msgConta");

    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.minha_conta)
        );

        document.getElementById("msgConta").textContent =
            `Id: ${dados.id} | Status: ${dados.StatusConta} | Saldo: R$ ${Number(dados.Saldo).toFixed(2)} | Email: ${dados.emailCliente} | Nome: ${dados.nomeCliente}`;

    } catch (erro) {
        msg.textContent = "Erro ao carregar conta: " + erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }
}

// BALANCE

async function carregarSaldo() {
    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.saldo)
        );

        saldoAtual = Number(dados.saldo ?? dados);

        document.getElementById("valorSaldo").textContent =
            "R$ " + saldoAtual.toFixed(2);

    } catch (erro) {
        alert("Erro ao carregar saldo: " + erro.message);
    }
}

// TRANSACTIONS

async function sacar() {
    const msg = document.getElementById("msgSaque");
    const valor = parseFloat(document.getElementById("valorSaque").value);

    if (!valor || valor <= 0) {
        msg.textContent = "O valor da operação tem que ser maior que zero!";
        msg.style.color = "red";
        return;

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }

    if (valor > saldoAtual) {
        msg.textContent = "Saldo insuficiente!";
        msg.style.color = "red";
        return;

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.saque, {
                method: "POST",
                body: JSON.stringify({ valor })
            })
        );

        msg.textContent = "Valor sacado com sucesso!";
        msg.style.color="green"
        document.getElementById("valorSaque").value = "";

        await carregarSaldo();

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";
    }
}

async function depositar() {
    const msg = document.getElementById("msgDeposito");
    const valor = parseFloat(document.getElementById("valorDeposito").value)

    if (!valor || valor <= 0) {
        msg.textContent = "O valor da operação tem que ser maior que zero!";
        msg.style.color = "red";
        return;

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.deposito, {
                method: "POST",
                body: JSON.stringify({ valor })
            })
        );

        msg.textContent = "Valor depositado com sucesso!";
        msg.style.color = "green";

        document.getElementById("valorDeposito").value = "";

        await carregarSaldo();

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";
    }
}

async function transferencia() {
    const msg = document.getElementById("msgTransferencia");

    const destinoId = Number(document.getElementById("idDestino").value);
    const valor = parseFloat(document.getElementById("valorTransferencia").value);
    const senha = document.getElementById("senhaTransferir").value;

    if (!destinoId || !valor || valor <= 0 || !senha) {
        msg.textContent = "Preencha os campos corretamente";
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);

        return;
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.transferencia, {
                method: "POST",
                body: JSON.stringify({
                    destinoId: Number(destinoId),
                    valor: Number(valor),
                    senha: senha
                })
            })
        );

        msg.textContent = "Transferência realizada com sucesso!";
        msg.style.color = "green";

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);

        await carregarSaldo();

    } catch (erro) {
        msg.textContent =erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }
}

// ACCOUNT MANAGEMENT

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

async function carregarStatus() {
    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.minha_conta)
        );

        const msg = document.getElementById("msg");

        msg.innerHTML = `
            <h3>Status da Conta</h3>
            <p><strong>Status:</strong> ${dados.StatusConta === "ATIVADA" ? "Ativada" : "Desativada"}</p>
            <button id="btnAtivar">Ativar</button>
            <button id="btnDesativar">Desativar</button>
            <p id="msgAtivar"></p>
        `;

        document.getElementById("btnAtivar")
            .addEventListener("click", () => alterarStatus(true));

        document.getElementById("btnDesativar")
            .addEventListener("click", () => alterarStatus(false));

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }
}

async function alterarStatus(ativar) {
    const msg = document.getElementById("msgAtivar");
    const url = ativar ? endpoints.ativar : endpoints.desativar;

    try {
        const resposta = await handleResponse(
            await apiFetch(url, { method: "PUT" })
        );

        const textoPadrao = ativar
            ? "Conta ativada com sucesso!"
            : "Conta desativada com sucesso!";

        msg.textContent = resposta?.mensagem || textoPadrao;
        msg.style.color = ativar ? "green" : "red"

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);

        setTimeout(() =>{
            carregarStatus();
        }, 6000)
    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";
    }
}

// UPDATE DATA

function mostrarFormularioAtualizacao() {
    const div = document.getElementById("msg");

    div.innerHTML = `
        <h3>Atualizar Dados</h3>
        <input type="text" id="novoNome" placeholder="Novo nome">
        <input type="email" id="novoEmail" placeholder="Novo email">
        <input type="password" id="senhaUser" placeholder="Digite sua senha">
        <button id="btnSalvarAtualizacao">Salvar</button>
        <p id="msgAtualizar"></p>
        <p>
            ATENÇÃO: Caso atualize o email, você será redirecionado para a tela de login, entre com o email atualizado!<br>
            ⚠ Para alterar senha, use "Esqueci minha senha" na tela de login.
        </p>
    `;

    document.getElementById("btnSalvarAtualizacao")
        .addEventListener("click", alterarDados);
}

async function alterarDados() {
    const msg = document.getElementById("msgAtualizar");

    const nome = document.getElementById("novoNome").value.trim();
    const email = document.getElementById("novoEmail").value.trim();
    const senha = document.getElementById("senhaUser").value;

    if (!nome && !email)
        msg.textContent = "Preencha pelo menos um campo!";
    msg.style.color = "red";

    if (!senha)
        msg.textContent = "Preencha a senha!";
    msg.style.color = "red";

    try {
        const resposta = await handleResponse(
            await apiFetch(endpoints.atualizar, {
                method: "PATCH",
                body: JSON.stringify({ nome: nome, email: email, senha: senha })
            })
        );

        msg.textContent = "Informações atualizadas com sucesso!";
        msg.style.color = "green";

        if (email !== "") {
            window.location.href = "login.html";
        }

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 3000);
    }
}

// HISTORY

function formatarData(dataISO) {
    if (!dataISO) return "Ainda não realizado!";

    const data = new Date(dataISO);

    return data.toLocaleString("pt-BR", {
        dateStyle: "short",
        timeStyle: "medium",
        timeZone: "America/Sao_Paulo"
    });
}

async function historicoTransferencias() {
    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.historico)
        );

        if (!dados || dados.length === 0) {
            return document.getElementById("msg").innerHTML =
                "<p>Nenhuma movimentação encontrada.</p>";
        }

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
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 6000);
    }
}

// DELETE ACCOUNT

async function apagarConta() {
    const msg = document.getElementById("msg");

    msg.innerHTML = `
        <h3>Apagar a Conta</h3>
        <h4>⚠️ Só pode apagar se o status estiver desativado e saldo zerado!</h4>
        <input type="email" id="email" placeholder="Email"><br>
        <input type="password" id="senha" placeholder="Senha"><br>
        <p id="msgApagar"></p>
        <button id="confirmarDelete">Apagar</button>
    `;

    const msgApagar = document.getElementById("msgApagar");

    document.getElementById("confirmarDelete")
        .addEventListener("click", async () => {

            const email = document.getElementById("email").value;
            const senha = document.getElementById("senha").value;

            if (!email || !senha) {
                msgApagar.textContent = "Preencha todos os campos!";
                msgApagar.style.color = "red";

                setTimeout(() => {
                    msgApagar.textContent = "";
                }, 6000);
            }

            try {
                await handleResponse(
                    await apiFetch(endpoints.apagar, {
                        method: "DELETE",
                        body: JSON.stringify({ email, senha })
                    })
                );

                msgApagar.textContent = "Conta apagada com sucesso!";
                msg.style.color = "green";

                window.location.href = "login.html";

            } catch (erro) {
                msgApagar.textContent = erro.message;
                msgApagar.style.color = "red";

                setTimeout(() => {
                    msg.textContent = "";
                }, 6000);
            }
        });
}

// LOGOUT

document.getElementById("btnLogout")
    .addEventListener("click", async () => {
        await apiFetch(endpoints.logout, { method: "POST" });
        localStorage.removeItem("token");
        window.location.href = "login.html";
    });


// BUTTON EVENTS

document.getElementById("btnConta")
    .addEventListener("click", () => {
        mostrarSecao("conta");
        minhaConta();
    });

document.getElementById("btnsaldo")
    .addEventListener("click", () => {
        mostrarSecao("saldo");
        carregarSaldo();
    });

document.getElementById("btnsaque")
    .addEventListener("click", () => mostrarSecao("saque"));

document.getElementById("btntransferencia")
    .addEventListener("click", () => mostrarSecao("transferencia"));

document.getElementById("btndeposito")
    .addEventListener("click", () => mostrarSecao("deposito"));

document.getElementById("btngerenciarConta")
    .addEventListener("click", () => {
        mostrarSecao("gerenciarConta");
        gerenciarConta();
    });

document.getElementById("confirmarDeposito")
    .addEventListener("click", depositar);

document.getElementById("confirmarSaque")
    .addEventListener("click", sacar);

document.getElementById("confirmarTransferencia")
    .addEventListener("click", transferencia);