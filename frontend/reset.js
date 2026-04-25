async function resetarSenha() {
    const URL_API = "https://minisistemabancario.onrender.com";
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");

    if (!token) {
        alert("Token inválido!");
        return;
    }

    const novaSenha = document.getElementById("novaSenha").value;

    try {
        const response = await fetch(`${URL_API}/auth/reset-senha`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                token: token,
                novaSenha: novaSenha
            })
        });

        const mensagem = await response.text();

        if (!response.ok) {
            alert(mensagem);
            return;
        }

        alert("Senha alterada com sucesso!");
        window.location.href = "login.html";

    } catch (erro) {
        alert("Erro ao conectar com o servidor.");
        console.error(erro);
    }
}