const GATEWAY_URL = 'http://localhost:8080';

const formCadastro = document.getElementById("formCadastro");
const inputSenha = document.getElementById("inputSenha");
const inputConfirmarSenha = document.getElementById("inputConfirmarSenha");
const mensagemSenha = document.getElementById("senhaMensagem");
const mensagemConfirmaSenha = document.getElementById("mensagemConfirmaSenha");

// VALIDAÇÃO DO CAMPO SENHA
inputSenha.addEventListener("input", () => {
    if (inputSenha.value.length < 8) {
        mensagemSenha.textContent = "Sua senha deve ter pelo menos 8 csaracteres.";
        mensagemSenha.style.color = "red";
    } else {
        mensagemSenha.textContent = "Senha válida!";
        mensagemSenha.style.color = "green";
    }
});

// VALIDAÇÃO DO CAMPO 'COMFIRME SUA SENHA'
inputConfirmarSenha.addEventListener("input", () => {
    if (inputConfirmarSenha.value === "") {
        mensagemConfirmaSenha.textContent = "";
        mensagemConfirmaSenha.style.visibility = "hidden";
        inputConfirmarSenha.classList.remove("erro", "ok");

    } else if (inputSenha.value === inputConfirmarSenha.value) {
        mensagemConfirmaSenha.textContent = "As senhas conferem!";
        mensagemConfirmaSenha.style.color = "green";
        mensagemConfirmaSenha.style.visibility = "visible";
        inputConfirmarSenha.classList.add("ok");
        inputConfirmarSenha.classList.remove("erro");

    } else {
        mensagemConfirmaSenha.textContent = "As senhas não conferem!";
        mensagemConfirmaSenha.style.color = "red";
        mensagemConfirmaSenha.style.visibility = "visible";
        inputConfirmarSenha.classList.add("erro");
        inputConfirmarSenha.classList.remove("ok");
    }
});


formCadastro.addEventListener("submit", async (e) => {
    e.preventDefault(); 

    const nomeHTML = document.getElementById("inputNome").value;
    const dataNascimentoHTML = document.getElementById("inputDataNasc").value; 
    console.log("data digitada: ", dataNascimentoHTML);
    const emailHTML = document.getElementById("inputEmail").value;
    const senhaHTML = inputSenha.value;
    const confirmarSenha = inputConfirmarSenha.value;

    //VERIFICAÇÃO SE AS SENHAS SÃO IGUAIS ANTES DE ENVIAR
    if (senhaHTML !== confirmarSenha) {
        alert("As senhas não coincidem! Por favor, tente novamente.");
        return;
    }

    const dadosUsuario = {
        name: nomeHTML,
        birthDate: dataNascimentoHTML,
        email: emailHTML,
        password: senhaHTML
    };

    //ENVIO DO FORMULÁRIO
    try {
        const response = await fetch(`${GATEWAY_URL}/usuarios`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(dadosUsuario),
        });

        if (response.status === 201) {
            alert("Cadastro realizado com sucesso!");
            window.location.href = "index.html";
        } else {
            // Primeiro verificamos se a resposta é realmente um JSON
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                  const errorData = await response.json();
                  alert("Erro ao cadastrar: " + (errorData.message || "Erro no servidor."));
            } else {
                        // Se não for JSON (como o erro 401 do Gateway), lemos como texto
                   const errorText = await response.text();
                   console.error("Erro do servidor (não JSON):", errorText);
                   alert(`Erro ${response.status}: O servidor recusou a requisição.`);
            }
        }
    } catch (error) {
        console.error("Erro na comunicação com o servidor:", error);
        alert("Ocorreu um erro ao tentar se conectar ao servidor.");
    }
});
