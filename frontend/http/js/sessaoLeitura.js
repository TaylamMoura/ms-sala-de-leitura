
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
let intervalo = null;


//FUNÇÕES DO CRONÔMETRO
function iniciarSessao(){
  if (!cronometroAtivo) {
    cronometroAtivo = true;
    // evita múltiplos setInterval
    if (!intervalo) {
      intervalo = setInterval(() => {
        tempoDecorrido += 1000;
        atualizarCronometro();
      }, 1000);
    }
  }

  // mostra/oculta botões (garanta que existam os ids no HTML)
  const btnStart = document.getElementById("iniciarSessao");
  const btnPause = document.getElementById("pausarSessao");
  const btnStop  = document.getElementById("finalizarSessao");
  if (btnStart) btnStart.classList.add('hidden');
  if (btnPause) btnPause.classList.remove('hidden');
  if (btnStop)  btnStop.classList.remove('hidden');
}

function pausarSessao(){
  cronometroAtivo = false;
  if (intervalo) {
    clearInterval(intervalo);
    intervalo = null;
  }

  const btnStart = document.getElementById("iniciarSessao");
  const btnPause = document.getElementById("pausarSessao");
  const btnStop  = document.getElementById("finalizarSessao");
  if (btnStart) btnStart.classList.remove('hidden');
  if (btnPause) btnPause.classList.add('hidden');
  if (btnStop)  btnStop.classList.remove('hidden');
}

function atualizarCronometro(){
  const minutos = Math.floor(tempoDecorrido / 60000);
  const segundos = Math.floor((tempoDecorrido % 60000) / 1000);
  const minutosFormatados = minutos.toString().padStart(2, '0');
  const segundosFormatados = segundos.toString().padStart(2, '0');
  const el = document.getElementById('cronometro') || document.querySelector('h1');
  if (el) el.textContent = `${minutosFormatados}:${segundosFormatados}`;
}

function finalizarSessao(){
  if (tempoDecorrido < 1000) {
    alert("Você não pode finalizar uma sessão sem tempo de leitura!");
    return;
  }

  // garante que o intervalo pare
  cronometroAtivo = false;
  if (intervalo) {
    clearInterval(intervalo);
    intervalo = null;
  }

  // atualiza UI do cronômetro (opcional)
  atualizarCronometro();

  // abre modal (função única)
  abrirModal();

  console.log("tempo decorrido (ms):", tempoDecorrido);
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
    const inputPagina = document.getElementById("inputPaginas");
    const paginaInserida = parseInt(inputPagina.value, 10);

    if (!paginaInserida || paginaInserida <= 0) {
        alert("Insira um número de página válido.");
        return;
    }

    const ultimaPagina = await buscarUltimaPagina(livroId);

    if (paginaInserida < ultimaPagina) {
        alert(`A página inserida (${paginaInserida}) não pode ser menor que a última (${ultimaPagina}).`);
        return;
    }

    const tempoLeituraSegundos = Math.floor(tempoDecorrido / 1000);

    const corpoRequisicao = {
        bookId: parseInt(livroId),
        lastPage: paginaInserida,
        readingTime: tempoLeituraSegundos
    };

    try {
        const response = await fetch(`${GATEWAY_URL}/sessao-leitura/finalizar`, {
            method: 'POST',
            headers: getAuthHeader(),
            body: JSON.stringify(corpoRequisicao)
        });

        if (response.ok) {
            // Pegamos o texto formatado do cronômetro para exibir no resumo
            const tempoFormatado = document.getElementById('cronometro').textContent;

            // Redireciona para a página de fim de sessão com os dados na URL
            const params = new URLSearchParams({
                bookId: livroId,
                tempo: tempoFormatado,
                pagina: paginaInserida
            });

            window.location.href = `fim-sessao.html?${params.toString()}`;
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
function abrirModal() {
  const modal = document.getElementById("modal-paginas");
  if (modal) {
    modal.classList.remove("hidden");
    modal.setAttribute("aria-hidden", "false");
  }
}

function fecharModal() {
  const modal = document.getElementById("modal-paginas");
  if (modal) {
    modal.classList.add("hidden");
    modal.setAttribute("aria-hidden", "true");
  }
}



async function exibirCapaLivro() {
    // 1. Use 'livroId' que é o nome da variável que você declarou lá no topo
    if (!livroId || !userId) {
        console.error("ID do livro ou do usuário não encontrado no script.");
        return;
    }

    try {
        // 2. Corrigi a URL: tirei as chaves {} e usei as variáveis certas com a barra /
        const response = await fetch(`${GATEWAY_URL}/livros/detalhes/${livroId}`, {
            headers: getAuthHeader()
        });

        if (response.ok) {
            const livro = await response.json();

            // 3. Verifique se no seu Java o campo é 'coverUrl' ou 'urlCapa'
            // Pelos nossos chats anteriores, o DTO do seu Java usa 'coverUrl'
            const campoCapa = livro.coverUrl || livro.urlCapa;

            const elementoCapa = document.getElementById('capaLivro');
            if (elementoCapa && campoCapa) {
                elementoCapa.src = campoCapa;
            }
        } else {
            console.error("Erro na resposta do servidor:", response.status);
        }
    } catch (error) {
        console.error("Erro ao exibir a capa:", error);
    }
}

