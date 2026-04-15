async function enviarEmail() {
    const email = document.getElementById("email").value;

    const response = await fetch ("http://localhost:8080/auth/esqueci-senha", {
        method:"POST",
        headers:{"Content-Type":"application/json"},
        body: JSON.stringify({email: email})
    })

    const mensagem = await response.text()

    if(!response.ok){
        alert(mensagem)
        return;
    }
    const resposta = prompt("Clique em 'ok' e você será transferido para a tela de redefinir a senha:",mensagem)

    if(resposta !== null){
        window.location.href=mensagem;
    }
}