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
    pagar: URL_API + "/pagamento/realizar",
    logout: URL_API + "/auth/logout"
};

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
            window.location.href = "../login/login.html";
        }, 1000);

        throw new Error("Sessão expirada. Faça login novamente!");
    }

    const texto = await response.text();

    if (!response.ok) {
        let mensagemErro;

        try {
            const erroJson = JSON.parse(texto);
            mensagemErro = erroJson.mensagem;
        } catch {
            mensagemErro = texto;
        }

        throw new Error(mensagemErro || "Erro desconhecido!");
    }

    if (!texto) {
        return null;
    }

    try {
        return JSON.parse(texto);
    } catch {
        return texto;
    }
}

let saldoAtual = 0;

const sidebar = document.getElementById("sidebar");

function mostrarSecao(id) {
    document.querySelectorAll(".secao").forEach(secao => {
        secao.style.display = "none";
    });

    document.getElementById("dashboard").style.display = "none";

    const secao = document.getElementById(id);

    if (!secao) {
        return;
    }

    if (
        id === "saque" ||
        id === "deposito" ||
        id === "transferencia" ||
        id === "pagar"
    ) {
        secao.style.display = "grid";
    } else {
        secao.style.display = "flex";
    }

    if (
        id === "saque" ||
        id === "deposito" ||
        id === "transferencia" ||
        id === "pagar"
    ) {
        carregarUltimasOperacoes();
    }
}

function mostrarDashboard() {
    document.querySelectorAll(".secao").forEach(secao => {
        secao.style.display = "none";
    });

    document.getElementById("dashboard").style.display = "grid";

    minhaConta();
    carregarUltimasOperacoes();
}

async function minhaConta() {
    const msg = document.getElementById("msgConta");

    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.minha_conta)
        );

        saldoAtual = Number(dados.Saldo);

        document.getElementById("boasVindas").textContent =
            `Olá, ${dados.nomeCliente}! 👋`;

        document.getElementById("idConta").textContent =
            dados.id;

        document.getElementById("valorSaldoConta").textContent =
            `R$ ${saldoAtual.toFixed(2)}`;

        document.getElementById("nomeCliente").textContent =
            dados.nomeCliente;

        document.getElementById("emailCliente").textContent =
            dados.emailCliente;

        document.getElementById("statusConta").textContent =
            dados.StatusConta;

    } catch (erro) {
        if (!msg) {
            return;
        }

        msg.textContent =
            "Erro ao carregar conta: " + erro.message;

        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);
    }
}

async function carregarSaldo() {
    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.saldo)
        );

        saldoAtual = Number(dados.saldo ?? dados);

        return saldoAtual;

    } catch (erro) {
        console.error("Erro ao carregar saldo:", erro);
        throw erro;
    }
}

async function sacar() {
    const msg = document.getElementById("msgSaque");

    const valor = parseFloat(
        document.getElementById("valorSaque").value
    );

    if (!valor || valor <= 0) {
        msg.textContent =
            "O valor da operação tem que ser maior que zero!";

        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

        return;
    }

    try {
        await carregarSaldo();

        if (valor > saldoAtual) {
            msg.textContent =
                `Saldo insuficiente! Seu saldo atual é R$ ${saldoAtual.toFixed(2)}.`;

            msg.style.color = "red";

            setTimeout(() => {
                msg.textContent = "";
            }, 4500);

            return;
        }

        await handleResponse(
            await apiFetch(endpoints.saque, {
                method: "POST",
                body: JSON.stringify({ valor })
            })
        );

        msg.textContent =
            "Valor sacado com sucesso!";

        msg.style.color = "green";

        document.getElementById("valorSaque").value = "";

        await carregarSaldo();
        await minhaConta();
        await carregarUltimasOperacoes();

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);
    }
}

async function depositar() {
    const msg = document.getElementById("msgDeposito");

    const valor = parseFloat(
        document.getElementById("valorDeposito").value
    );

    if (!valor || valor <= 0) {
        msg.textContent =
            "O valor da operação tem que ser maior que zero!";

        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

        return;
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.deposito, {
                method: "POST",
                body: JSON.stringify({ valor })
            })
        );

        msg.textContent =
            "Valor depositado com sucesso!";

        msg.style.color = "green";

        document.getElementById("valorDeposito").value = "";

        await carregarSaldo();
        await minhaConta();
        await carregarUltimasOperacoes();

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);
    }
}

async function transferencia() {
    const msg =
        document.getElementById("msgTransferencia");

    const destinoId = Number(
        document.getElementById("idDestino").value
    );

    const valor = parseFloat(
        document.getElementById("valorTransferencia").value
    );

    const senha =
        document.getElementById("senhaTransferir").value;

    if (!destinoId || !valor || valor <= 0 || !senha) {
        msg.textContent =
            "Preencha os campos corretamente!";

        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

        return;
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.transferencia, {
                method: "POST",
                body: JSON.stringify({
                    destinoId: destinoId,
                    valor: valor,
                    senha: senha
                })
            })
        );

        msg.textContent =
            "Transferência realizada com sucesso!";

        msg.style.color = "green";

        document.getElementById("idDestino").value = "";
        document.getElementById("valorTransferencia").value = "";
        document.getElementById("senhaTransferir").value = "";

        await carregarSaldo();
        await minhaConta();
        await carregarUltimasOperacoes();

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);
    }
}

async function pagar() {
    const msg = document.getElementById("msgPagar");

    const codigoPagamento =
        document.getElementById("idCodigo");

    const codigo =
        codigoPagamento.value.trim();

    if (!codigo) {
        msg.textContent =
            "Preencha o código do pagamento!";

        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

        return;
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.pagar, {
                method: "POST",
                body: JSON.stringify({
                    codigoPagamento: codigo
                })
            })
        );

        msg.textContent =
            "Pagamento realizado com sucesso!";

        msg.style.color = "green";

        codigoPagamento.value = "";

        await carregarSaldo();
        await minhaConta();
        await carregarUltimasOperacoes();

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";

        setTimeout(() => {
            msg.textContent = "";
        }, 4500);
    }
}

async function carregarUltimasOperacoes() {
    try {
        const dados = await handleResponse(
            await apiFetch(endpoints.historico)
        );

        const historico =
            Array.isArray(dados) ? dados : [];

        const ordenado = [...historico]
            .filter(item => item && item.data)
            .sort((a, b) => {
                return new Date(b.data) - new Date(a.data);
            });

        const ultimas =
            ordenado.slice(0, 3);

        mostrarUltimasMovimentacoes(ultimas);

        const saques = ordenado
            .filter(item => {
                const tipo =
                    String(item.tipo || "").toUpperCase();

                return tipo.includes("SAQUE");
            })
            .slice(0, 4);

        const depositos = ordenado
            .filter(item => {
                const tipo =
                    String(item.tipo || "").toUpperCase();

                return tipo.includes("DEPOSITO");
            })
            .slice(0, 4);

        const transferencias = ordenado
            .filter(item => {
                const tipo =
                    String(item.tipo || "").toUpperCase();

                return (
                    tipo.includes("TRANSFERENCIA") ||
                    tipo.includes("TRANSFER")
                );
            })
            .slice(0, 4);

        const pagamentos = ordenado
            .filter(item => {
                const tipo =
                    String(item.tipo || "").toUpperCase();

                return tipo.includes("PAGAMENTO");
            })
            .slice(0, 4);

        mostrarOperacoes(
            "listarSaques",
            saques,
            "saque"
        );

        mostrarOperacoes(
            "listarDepositos",
            depositos,
            "depósito"
        );

        mostrarOperacoes(
            "listarTransferencias",
            transferencias,
            "transferência"
        );

        mostrarOperacoes(
            "listarPagamentos",
            pagamentos,
            "pagamento"
        );

    } catch (erro) {
        console.error(
            "Erro ao carregar operações recentes:",
            erro
        );
    }
}

function mostrarUltimasMovimentacoes(operacoes) {
    const elemento =
        document.getElementById("listarMovimentacoes");

    if (!elemento) {
        return;
    }

    if (!operacoes.length) {
        elemento.innerHTML =
            "<p>Nenhuma movimentação encontrada.</p>";

        return;
    }

    elemento.innerHTML =
        operacoes.map(operacao => {

            const tipo =
                String(
                    operacao.tipo || ""
                ).toUpperCase();

            let nomeTipo =
                operacao.tipo || "Movimentação";

            if (tipo.includes("SAQUE")) {
                nomeTipo = "Saque";
            } else if (tipo.includes("DEPOSITO")) {
                nomeTipo = "Depósito";
            } else if (
                tipo.includes("TRANSFERENCIA") ||
                tipo.includes("TRANSFER")
            ) {
                nomeTipo = "Transferência";
            } else if (tipo.includes("PAGAMENTO")) {
                nomeTipo = "Pagamento";
            }

            return `
                <div class="item-historico">
                    <p>
                        <strong>${nomeTipo}</strong>
                    </p>

                    <p>
                        <strong>Valor:</strong>
                        R$ ${Number(operacao.valor).toFixed(2)}
                    </p>

                    <p>
                        <strong>Data:</strong>
                        ${formatarData(operacao.data)}
                    </p>
                </div>
            `;
        }).join("");
}

function mostrarOperacoes(
    elementoId,
    operacoes,
    nomeOperacao
) {
    const elemento =
        document.getElementById(elementoId);

    if (!elemento) {
        return;
    }

    if (!operacoes.length) {
        elemento.innerHTML = `
            <p>Nenhum ${nomeOperacao} realizado ainda.</p>
        `;

        return;
    }

    elemento.innerHTML =
        operacoes.map(operacao => {

            const tipo =
                String(
                    operacao.tipo || ""
                ).toUpperCase();

            let nomeTipo =
                nomeOperacao;

            if (tipo.includes("TRANSFERENCIA")) {
                nomeTipo = "Transferência";
            } else if (tipo.includes("TRANSFER")) {
                nomeTipo = "Transferência";
            } else if (tipo.includes("SAQUE")) {
                nomeTipo = "Saque";
            } else if (tipo.includes("DEPOSITO")) {
                nomeTipo = "Depósito";
            } else if (tipo.includes("PAGAMENTO")) {
                nomeTipo = "Pagamento";
            }

            return `
                <div class="item-operacao">

                    <p>
                        <strong>${nomeTipo}</strong>
                    </p>

                    <p>
                        <strong>Valor:</strong>
                        R$ ${Number(operacao.valor).toFixed(2)}
                    </p>

                    <p>
                        <strong>Data:</strong>
                        ${formatarData(operacao.data)}
                    </p>

                    ${
                        operacao.codigoPagamento
                            ? `
                                <p>
                                    <strong>Código:</strong>
                                    ${operacao.codigoPagamento}
                                </p>
                            `
                            : ""
                    }

                </div>
            `;
        }).join("");
}

function formatarData(dataISO) {
    if (!dataISO) {
        return "Ainda não realizado!";
    }

    const valor = String(dataISO).trim();

    const data = new Date(
        /(?:Z|[+-]\d{2}:\d{2})$/.test(valor)
            ? valor
            : valor + "Z"
    );

    return data.toLocaleString("pt-BR", {
        dateStyle: "short",
        timeStyle: "medium",
        timeZone: "America/Sao_Paulo"
    });
}

function gerenciarConta() {
    const btnStatusConta =
        document.getElementById("btnStatusConta");

    const btnAtualizar =
        document.getElementById("btnAtualizar");

    const btnHistorico =
        document.getElementById("btnHistorico");

    const btnDeletar =
        document.getElementById("btnDeletar");

    if (btnStatusConta) {
        btnStatusConta.onclick = carregarStatus;
    }

    if (btnAtualizar) {
        btnAtualizar.onclick =
            mostrarFormularioAtualizacao;
    }

    if (btnHistorico) {
        btnHistorico.onclick =
            historicoTransferencias;
    }

    if (btnDeletar) {
        btnDeletar.onclick =
            apagarConta;
    }
}

async function carregarStatus() {
    const div =
        document.getElementById("conteudoGerenciar");

    if (!div) {
        return;
    }

    try {
        const dados =
            await handleResponse(
                await apiFetch(endpoints.minha_conta)
            );

        div.innerHTML = `
            <div class="subcard-gerenciar">

                <h3>Status da Conta</h3>

                <p>
                    <strong>Status:</strong>
                    ${
                        dados.StatusConta === "ATIVADA"
                            ? "Ativada"
                            : "Desativada"
                    }
                </p>

                <div class="botoes-status">

                    <button id="btnAtivar">
                        Ativar
                    </button>

                    <button id="btnDesativar">
                        Desativar
                    </button>

                </div>

                <p id="msgAtivar"></p>

            </div>
        `;

        document.getElementById("btnAtivar")
            .onclick = () => alterarStatus(true);

        document.getElementById("btnDesativar")
            .onclick = () => alterarStatus(false);

    } catch (erro) {

        div.innerHTML = `
            <p class="mensagem-erro">
                ${erro.message}
            </p>
        `;
    }
}

async function alterarStatus(ativar) {
    const msg =
        document.getElementById("msgAtivar");

    const url =
        ativar
            ? endpoints.ativar
            : endpoints.desativar;

    try {
        const resposta =
            await handleResponse(
                await apiFetch(url, {
                    method: "PUT"
                })
            );

        msg.textContent =
            resposta?.mensagem ||
            (
                ativar
                    ? "Conta ativada com sucesso!"
                    : "Conta desativada com sucesso!"
            );

        msg.style.color = "green";

        await minhaConta();

        setTimeout(() => {
            carregarStatus();
        }, 1500);

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";
    }
}

function mostrarFormularioAtualizacao() {
    const div =
        document.getElementById("conteudoGerenciar");

    if (!div) {
        return;
    }

    div.innerHTML = `
        <div class="subcard-gerenciar">

            <h3>Atualizar Dados</h3>

            <div class="form-gerenciar">

                <input
                    type="text"
                    id="novoNome"
                    placeholder="Novo nome"
                >

                <input
                    type="email"
                    id="novoEmail"
                    placeholder="Novo email"
                >

                <input
                    type="password"
                    id="senhaUser"
                    placeholder="Digite sua senha"
                >

                <button id="btnSalvarAtualizacao">
                    Salvar alterações
                </button>

            </div>

            <p id="msgAtualizar"></p>

            <p class="aviso-gerenciar">
                Caso atualize o email, você será
                redirecionado para a tela de login.
                Para alterar a senha, use
                "Esqueci minha senha" na tela de login.
            </p>

        </div>
    `;

    document.getElementById(
        "btnSalvarAtualizacao"
    ).onclick = alterarDados;
}

async function alterarDados() {
    const msg =
        document.getElementById("msgAtualizar");

    const nome =
        document.getElementById("novoNome")
            .value.trim();

    const email =
        document.getElementById("novoEmail")
            .value.trim();

    const senha =
        document.getElementById("senhaUser")
            .value;

    if (!nome && !email) {
        msg.textContent =
            "Preencha pelo menos um campo!";

        msg.style.color = "red";
        return;
    }

    if (!senha) {
        msg.textContent =
            "Preencha a senha!";

        msg.style.color = "red";
        return;
    }

    try {
        await handleResponse(
            await apiFetch(endpoints.atualizar, {
                method: "PATCH",
                body: JSON.stringify({
                    nome: nome,
                    email: email,
                    senha: senha
                })
            })
        );

        msg.textContent =
            "Informações atualizadas com sucesso!";

        msg.style.color = "green";

        if (email !== "") {
            setTimeout(() => {
                window.location.href =
                    "../login/login.html";
            }, 1000);
        } else {
            await minhaConta();
        }

    } catch (erro) {
        msg.textContent = erro.message;
        msg.style.color = "red";
    }
}

async function historicoTransferencias() {
    const div =
        document.getElementById("conteudoGerenciar");

    if (!div) {
        return;
    }

    try {
        const dados =
            await handleResponse(
                await apiFetch(endpoints.historico)
            );

        if (!dados || !dados.length) {
            div.innerHTML = `
                <div class="subcard-gerenciar">
                    <h3>Histórico</h3>
                    <p>Nenhuma movimentação encontrada.</p>
                </div>
            `;

            return;
        }

        const historico =
            [...dados].sort((a, b) => {
                return new Date(b.data) -
                    new Date(a.data);
            });

        let html = `
            <div class="subcard-gerenciar">

                <h3>Histórico de Transações</h3>
        `;

        historico.forEach(item => {

            html += `
                <div class="item-historico">

                    <p>
                        <strong>ID:</strong>
                        ${item.id}
                    </p>

                    <p>
                        <strong>Tipo:</strong>
                        ${item.tipo}
                    </p>

                    <p>
                        <strong>Data:</strong>
                        ${formatarData(item.data)}
                    </p>

                    <p>
                        <strong>Valor:</strong>
                        R$ ${Number(item.valor).toFixed(2)}
                    </p>

                    ${
                        String(item.tipo)
                            .toUpperCase()
                            .includes("PAGAMENTO")
                            ? `
                                <p>
                                    <strong>Código:</strong>
                                    ${item.codigoPagamento}
                                </p>
                            `
                            : ""
                    }

                </div>
            `;
        });

        html += `
            </div>
        `;

        div.innerHTML = html;

    } catch (erro) {

        div.innerHTML = `
            <p class="mensagem-erro">
                ${erro.message}
            </p>
        `;
    }
}

async function apagarConta() {
    const div =
        document.getElementById("conteudoGerenciar");

    if (!div) {
        return;
    }

    div.innerHTML = `
        <div class="subcard-gerenciar card-excluir">

            <h3>Apagar Conta</h3>

            <p class="aviso-exclusao">
                ⚠️ A conta só pode ser apagada se estiver
                desativada e com saldo zerado.
            </p>

            <input
                type="email"
                id="email"
                placeholder="Email"
            >

            <input
                type="password"
                id="senha"
                placeholder="Senha"
            >

            <button id="confirmarDelete">
                Apagar Conta
            </button>

            <p id="msgApagar"></p>

        </div>
    `;

    const msgApagar =
        document.getElementById("msgApagar");

    document.getElementById(
        "confirmarDelete"
    ).onclick = async () => {

        const email =
            document.getElementById("email").value;

        const senha =
            document.getElementById("senha").value;

        if (!email || !senha) {
            msgApagar.textContent =
                "Preencha todos os campos!";

            msgApagar.style.color = "red";
            return;
        }

        try {
            await handleResponse(
                await apiFetch(endpoints.apagar, {
                    method: "DELETE",
                    body: JSON.stringify({
                        email,
                        senha
                    })
                })
            );

            msgApagar.textContent =
                "Conta apagada com sucesso!";

            msgApagar.style.color = "green";

            setTimeout(() => {
                window.location.href =
                    "../login/login.html";
            }, 1000);

        } catch (erro) {
            msgApagar.textContent =
                erro.message;

            msgApagar.style.color = "red";
        }
    };
}

document.getElementById("btnLogout")
    .addEventListener("click", async () => {

        try {
            await apiFetch(
                endpoints.logout,
                {
                    method: "POST"
                }
            );
        } finally {
            localStorage.removeItem("token");

            window.location.href =
                "../login/login.html";
        }
    });

document.getElementById("btnMenu")
    .addEventListener("click", () => {

        sidebar.classList.toggle("escondida");
    });

document.getElementById("btnConta")
    .addEventListener("click", () => {

        mostrarDashboard();
    });

document.getElementById("btnsaque")
    .addEventListener("click", () => {

        mostrarSecao("saque");
    });

document.getElementById("btndeposito")
    .addEventListener("click", () => {

        mostrarSecao("deposito");
    });

document.getElementById("btntransferencia")
    .addEventListener("click", () => {

        mostrarSecao("transferencia");
    });

document.getElementById("btnPagar")
    .addEventListener("click", () => {

        mostrarSecao("pagar");
    });

document.getElementById("btngerenciarConta")
    .addEventListener("click", () => {

        mostrarSecao("gerenciarConta");
        gerenciarConta();
    });

document.getElementById("confirmarDeposito")
    .addEventListener("click", depositar);

document.getElementById("confirmarSaque")
    .addEventListener("click", sacar);

document.getElementById("confirmarPagamento")
    .addEventListener("click", pagar);

document.getElementById("confirmarTransferencia")
    .addEventListener(
        "click",
        transferencia
    );

document.querySelectorAll(".verTodas").forEach(botao => {
    botao.addEventListener("click",function(event){
    event.preventDefault();

    mostrarSecao("gerenciarConta");
    gerenciarConta()
    historicoTransferencias();
});
});


mostrarDashboard();