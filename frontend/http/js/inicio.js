const GATEWAY_URL = 'http://localhost:8080';

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


//FUNÇÃO DE LOGIN
async function fazerLogin(e){
    e.preventDefault();
    
    const userEmail = document.getElementById('inputLogin').value;
    const userSenha = document.getElementById('inputSenha').value;
    
    if(!userEmail || !userSenha){
        alert('Preencha todos os campos!');
        return;
    }
    //ENVIO DE DADOS AO SERVIDOR
    try{
        const response = await fetch(`${GATEWAY_URL}/usuarios/login`, {
              method:'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                email: userEmail,
                password: userSenha
              })
            });

        if (response.status === 200) {
              const dados = await response.json();
              localStorage.setItem('token', dados.token);
              localStorage.setItem('userId', dados.userId);
              window.location.href = 'home.html';

            } else {
               const contentType = response.headers.get("content-type");
               if(contentType && contentType.includes("application/json")){
                    const erro =  await response.json();
                    alert(erro.mensagem);
               } else {
                    const erroText = await response.text();
                    alert(erroText)
               }
            }
    } catch(error){
        console.error('Erro na requisição: ', error);
        alert('Erro ao conectar com o servidor');
    }
}
