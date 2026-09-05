async function enviarEmail() {
    const email = document.getElementById("email").value;

    if(!email){
        alert("Digite o seu email!");
        return;
    }

    try{

        const response = await fetch ("https://minisistemabancario.onrender.com/auth/esqueci-senha", {
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body: JSON.stringify({email: email})
        })

        const mensagem = await response.text()

        if(!response.ok){
            throw new Error(mensagem || "Erro ao solicitar redefinição de senha.");
        }

        alert("Clique em 'ok' e você será transferido para a tela de redefinir a senha:")
        window.location.href=mensagem;
        
} catch (erro){
    alert(erro.message);
}
}