document.addEventListener("DOMContentLoaded", () => {
    exibirCapaLivro();
});


//CAPTURAR ID DO LIVRO
const urlParams = new URLSearchParams(window.location.search);
const livroId = urlParams.get('id');

if (livroId) {
    console.log("Livro ID capturado: ", livroId);
} else {
    console.error("Erro: livroId não encontrado na URL.");
}

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
    if(tempoDecorrido === 0) {
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
    try{
        const response = await fetch(`/sessao-leitura/ultima-pagina/${livroId}`);
        if(!response.ok){
            throw new Error("Erro ao buscar ultima página");
        }
        const ultimaPagina = await response.json();
        return ultimaPagina;
    } catch (error){
        console.error("Erro ao busca última página: ", error);
        return 0;
    }
}


//FUNÇÃO PARA ENVIAR DADOS AO BACK-END
async function enviarSessaoLeitura(){
    const urlParams = new URLSearchParams(window.location.search);
    const livroId = urlParams.get("id");
    const paginaInserida = parseInt(document.getElementById("inputPaginas").value, 10);

    if(!paginaInserida || paginaInserida <= 0) {
        alert("Atenção, insira um número de páginas válida.");
        return;
    } 

    //Buscar a última página lida(se houver)
    let ultimaPagina;
    try{
        ultimaPagina = await buscarUltimaPagina(livroId);
    } catch(error){
        console.error("Erro ao buscar a última página parada:", error);
        alert("Erro ao validar a página inserida.");
        return;
    }

    //Validar a entrada de páginas do usuário
    if (paginaInserida < ultimaPagina) {
        alert(`A página inserida (${paginaInserida}) não pode ser menor que a última registrada (${ultimaPagina}).`);
        return;
    }

    //formatar o formato para enviar ao back-end
    const horas = Math.floor(tempoDecorrido / 3600000);
    const minutos =Math.floor((tempoDecorrido % 3600000) / 60000);
    const segundos = Math.floor((tempoDecorrido % 60000) / 1000);
    const tempoLeituraSegundos = (horas * 3600) + (minutos * 60) + segundos;

    //Envio de dados ao backend
    try{
        const response = await fetch('/sessao-leitura/finalizar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                livroId: parseInt(livroId),
                tempoLeitura: tempoLeituraSegundos ,
                paginasLidas: paginaInserida,
            }),
        });

        if(response.ok){
            fecharModal();
            tempoDecorrido = 0;
            atualizarCronometro();
            window.location.href = `/meu-livro.html?id=${livroId}`;
        }else {
            alert('Erro ao finalizar sessão');
        }
    } catch (error) {
        console.error('Erro na requisição: ', error);
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
    try {
        const id = livroId;
        if (id) {
            const response = await fetch(`http://localhost:8080/exibirDados/${id}`);
            if (!response.ok) {
                throw new Error("Erro ao buscar os dados do livro");
            }

            const livro = await response.json();
            const imgCapa = document.getElementById('capaLivro');
            imgCapa.src = livro.urlCapa;
        } else {
            console.error("ID do livro não encontrado.");
        }
    } catch (error) {
        console.error("Erro ao exibir a capa do livro:", error);
    }
}
