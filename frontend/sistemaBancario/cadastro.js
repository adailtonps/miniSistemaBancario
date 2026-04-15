const form = document.getElementById("formCadastro");
const msg = document.getElementById("msg");

document.getElementById("jaTemConta").addEventListener("click", () => {
    window.location.href = "login.html"
})

form.addEventListener("submit", async(event) => {
    event.preventDefault();

    const dados = {
        nome: form.nome.value,
        email: form.email.value,
        senha: form.senha.value
    };

    try{
        const response = await fetch("http://localhost:8080/auth/cadastro", {
            method: "POST",
            headers: {
                "Content-Type":"application/json"
            },
            body: JSON.stringify(dados)
        });

        if(!response.ok){
            const erro = await response.json();
            throw new Error(erro.mensagem);
        }

        msg.textContent = "Cadastro realizado com sucesso!";
        msg.style.color="green";

        setTimeout(() => {
            window.location.href ="login.html";
        }, 1500);

        form.reset();
    } catch (erro){
        msg.textContent=erro.message;
        msg.style.color="red";
    }

})