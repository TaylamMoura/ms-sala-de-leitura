
const formCadastro = document.getElementById("formCadastro");

const inputSenha = document.getElementById("inputSenha");
const mensagemSenha = document.getElementById("senhaMensagem");
const inputConfirmarSenha = document.getElementById("inputConfirmarSenha");
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

    const nome = document.getElementById("inputNome").value;
    const dataNascimento = document.getElementById("inputDataNasc").value; 
    console.log("data digitada: ", dataNascimento);
    const email = document.getElementById("inputEmail").value;
    const usuario = document.getElementById("inputNomeUsuario").value;
    const senha = inputSenha.value;
    const confirmarSenha = inputConfirmarSenha.value;

    //VERIFICAÇÃO SE AS SENHAS SÃO IGUAIS ANTES DE ENVIAR
    if (senha !== confirmarSenha) {
        alert("As senhas não coincidem! Por favor, tente novamente.");
        return;
    }

    const dadosUsuario = {
        nome,
        dataNascimento,
        email,
        usuario,
        senha,
    };

    //ENVIO DO FORMULÁRIO
    try {
        const response = await fetch("http://localhost:8080/usuarios", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(dadosUsuario),
        });

        if (response.ok) {
            alert("Cadastro realizado com sucesso!");
            window.location.href = "inicio.html";
        } else {
            const errorData = await response.json();
            alert("Erro ao cadastrar: " + (errorData.message || "Erro desconhecido."));
        }
    } catch (error) {
        console.error("Erro na comunicação com o servidor:", error);
        alert("Ocorreu um erro ao tentar se conectar ao servidor.");
    }
});
