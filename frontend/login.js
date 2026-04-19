document.getElementById("cadastroTela").addEventListener("click", () =>{
    window.location.href="index.html"
})

const form = document.getElementById("formLogin");
const msg = document.getElementById("msg");

async function apiFetch(url, options = {}) {
    return fetch(url, {
        ...options,
        headers:{
            "Content-Type":"application/json",
            ...(options.headers || {})
        }
    })
}

form.addEventListener("submit", async(event) =>{
    event.preventDefault();
    
    const dados = {
        email: form.email.value,
        senha:form.senha.value
    };

    try{
        const response = await apiFetch("https://minisistemabancario.onrender.com/auth/login", {
            method: "POST",
            body: JSON.stringify(dados)
        });

        let resultado=null;
        let mensagem = "";


        if(response.headers.get("content-type")?.includes("application/json")){
            resultado = await response.json();
            mensagem = resultado.mensagem || mensagem;
        } else {
            mensagem=await response.text();
        }


        if(!response.ok){
            throw new Error(mensagem || "Erro ao realizar login!");
        }

        localStorage.setItem("token",data.token)

        msg.textContent = "Login realizado com sucesso!"
        msg.style.color="green";

        setTimeout(() => {
            window.location.href = "telainicial.html"
        }, 1000);
    } catch (error){
        msg.textContent = error.message;
        msg.style.color="red";
    }
});