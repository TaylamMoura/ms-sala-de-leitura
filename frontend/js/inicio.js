function abrirFormularioCadastro() {
    window.location.href = 'cadastro.html';
}

//FUNÇÃO PARA ABRIR MODAL DE LOGIN
function abrirFormularioLogin() {
    const modalLogin = document.getElementById("formulario-login");
    modalLogin.style.display = 'block';
    modalLogin.setAttribute("aria-hidden", "false");
}

//FUNÇÃO PARA FECHAR MODAL DE LOGIN
function fecharFormularioLogin(){
    const modalLogin = document.getElementById("formulario-login");
    modalLogin.style.display = 'none';
    modalLogin.setAttribute("aria-hidden", "true");
}

//FUNÇÃO PARA VERIFICAR AUTENTICAÇÃO
function verificarAutenticacao() {
    const token = localStorage.getItem('token');

    if (!token) {
        alert('Você precisa fazer login para acessar esta página.');
        window.location.href = 'inicio.html';
        return;
    }

    fetch('http://localhost:8080/validate-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    })
    .then((resposta) => {
        if (!resposta.ok) {
            alert('Sessão expirada. Faça login novamente.');
            window.location.href = 'inicio.html';
        }
    })
    .catch((error) => {
        console.error('Erro ao validar o token:', error);
        alert('Erro de autenticação.');
        window.location.href = 'inicio.html';
    });
}


//FUNÇÃO DE LOGIN
async function fazerLogin(){
    const userEmail = document.getElementById('inputLogin').value;
    const userSenha = document.getElementById('inputSenha').value;
    
    if(!userEmail || !userSenha){
        alert('Preencha todos os campos!');
        return;
    }
    //ENVIO DE DADOS AO SERVIDOR
    try{
        const response = await fetch('http://localhost:8080/usuarios/login', {
              method:'POST',
              credentials: 'include',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                email: userEmail,
                senha: userSenha
              })
            });

        if (response.ok) {
              const dados = await response.json();
              window.location.href = 'index.html';
            } else {
              const erro =  await response.json();
              alert(erro.mensagem);
            }
          } catch(error) {
            console.error('Erro na requisição: ', error);
            alert('Erro ao conectar com o servidor');
          }
}


async function acessarRecursoProtegido() {
    const token = localStorage.getItem("token");

    if (!token) {
        alert('Você precisa fazer login para acessar este recurso.');
        return;
    }

    try {
        const resposta = await fetch("http://localhost:8080/protected-endpoint", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (resposta.ok) {
            const dados = await resposta.json();
            console.log(dados);
        } else {
            console.error('Erro na requisição:', await resposta.text());
            alert('Você não tem permissão para acessar este recurso.');
        }
    } catch (error) {
        console.error('Erro ao conectar:', error);
    }
}

