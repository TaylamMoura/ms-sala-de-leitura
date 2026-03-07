

const GATEWAY_URL = 'http://localhost:8080';

function getAuthHeader(){
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

document.addEventListener("DOMContentLoaded", () => {
    exibirCapaLivro();
});


//CAPTURAR ID DO LIVRO
const urlParams = new URLSearchParams(window.location.search);
const livroId = urlParams.get('bookId');
const userId = localStorage.getItem('userId');


//CONTROLE DO CRONÔMETRO
let cronometroAtivo = false;
let tempoDecorrido = 0;
let intervalo;


//FUNÇÕES DO CRONÔMETRO
function iniciarSessao(){
    if(!cronometroAtivo){
        cronometroAtivo = true;
        intervalo = setInterval(() => {
            tempoDecorrido += 1000;
            atualizarCronometro();
        }, 1000);
    }

    document.getElementById("iniciarSessao").style.display = "none"; // Esconde o botão play
    document.getElementById("pausarSessao").style.display = "inline-block"; // Mostra o botão pausar
    document.getElementById("finalizarSessao").style.display = "inline-block"; // Mostra o botão parar
}

function pausarSessao(){
    cronometroAtivo = false;
    clearInterval(intervalo);

    document.getElementById("iniciarSessao").style.display = "inline-block"; // Mostra o botão play
    document.getElementById("pausarSessao").style.display = "none"; // Esconde o botão pausar
    document.getElementById("finalizarSessao").style.display = "inline-block"; // Esconde o botão parar
}

function atualizarCronometro(){
    const minutos = Math.floor(tempoDecorrido / 60000);
    const segundos = Math.floor((tempoDecorrido % 60000) / 1000);

    const minutosFormatados = minutos.toString().padStart(2, '0');
    const segundosFormatados = segundos.toString().padStart(2, '0');

    //Atualiza o cronômetro
    document.querySelector('h1').textContent = `${minutosFormatados}:${segundosFormatados}`;
}

function finalizarSessao(){
    if(tempoDecorrido < 1000) {
        alert("Você não pode finalizar uma sessão sem tempo de leitura!");
        return;
    }
    cronometroAtivo = false;
    clearInterval(intervalo);
    abrirModalPaginas();
    console.log("tempo decorrido: " + tempoDecorrido);
}

//BUSCAR A PÁGINA QUE USUÁRIO PAROU PARA FAZER A VERIFICAÇÃO
async function buscarUltimaPagina(livroId) {
    try {
        const response = await fetch(`${GATEWAY_URL}/sessao-leitura/ultima-pagina/${livroId}`, {
            method: 'GET',
            headers: getAuthHeader()
        });
        if (!response.ok) return 0;
        
        return await response.json();
    } catch (error) {
        console.error("Erro ao buscar última página:", error);
        return 0;
    }
}


//FUNÇÃO PARA ENVIAR DADOS AO BACK-END
async function enviarSessaoLeitura() {
    const paginaInserida = parseInt(document.getElementById("inputPaginas").value, 10);

    if (!paginaInserida || paginaInserida <= 0) {
        alert("Insira um número de página válido.");
        return;
    }

    const ultimaPagina = await buscarUltimaPagina(livroId);

    if (paginaInserida < ultimaPagina) {
        alert(`A página inserida (${paginaInserida}) não pode ser menor que a última (${ultimaPagina}).`);
        return;
    }

    // Cálculo do tempo em segundos
    const tempoLeituraSegundos = Math.floor(tempoDecorrido / 1000);

    // MONTAGEM DO DTO (De acordo com seu EndSessionDTO no Java)
    const corpoRequisicao = {
        userId: parseInt(userId),
        bookId: parseInt(livroId),
        readingTime: tempoLeituraSegundos,
        lastPage: paginaInserida // Certifique-se que no Java o campo é 'lastPage'
    };

    try {
        const response = await fetch(`${GATEWAY_URL}/sessao-leitura/finalizar`, {
            method: 'POST',
            headers: getAuthHeader(),
            body: JSON.stringify(corpoRequisicao)
        });

        if (response.ok) {
            alert("Sessão salva com sucesso!");
            window.location.href = `meu-livro.html?bookId=${livroId}`;
        } else {
            const erro = await response.json();
            alert("Erro ao finalizar sessão: " + (erro.message || "Verifique os dados."));
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
    }
}

function cancelarEnvio() {
    alert('Envio cancelado.');
    tempoDecorrido = 0;
    atualizarCronometro();
}

//MODAL
function abrirModalPaginas() {
    const modal = document.getElementById("modal-paginas");
    modal.style.display = 'block';
    modal.setAttribute("aria-hidden", "false");
}

function fecharModal(){
    const modal = document.getElementById("modal-paginas");
    modal.style.display = 'none';
    modal.setAttribute("aria-hidden", "true");
}


async function exibirCapaLivro() {
    if (!livroId) return;
    try {
        const response = await fetch(`${GATEWAY_URL}/livros/exibirDados/${livroId}`, {
            headers: getAuthHeader()
        });
        if (response.ok) {
            const livro = await response.json();
            document.getElementById('capaLivro').src = livro.urlCapa;
        }
    } catch (error) {
        console.error("Erro ao exibir a capa:", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    exibirCapaLivro();
});
