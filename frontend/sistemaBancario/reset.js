async function resetarSenha() {
    const URL_API = "http://localhost:8080/";
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");

    if(!token){
        alert("Token inválido!")
        return
    }

    const novaSenha = document.getElementById("novaSenha").value;

    const response = await fetch(`${URL_API}auth/reset-senha`,{
        method:"POST",
        headers: {"Content-Type":"application/json"},
        body:JSON.stringify({
            token: token,
            novaSenha:novaSenha
        })
    })

    const mensagem = await response.text()

    if(!response.ok){
        alert(mensagem)
        return
    }
    alert("Senha alterada com sucesso!")
    window.location.href="login.html";
}