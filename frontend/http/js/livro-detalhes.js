const GATEWAY_URL = 'http://localhost:8080'; 

const urlParams = new URLSearchParams(window.location.search);
const bookId = urlParams.get('bookId');

function getAuthHeader(){
  return{
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  };
}

async function atualizarBarraProgresso() {
    if (!bookId) return;

    try {
        // Busca a última página lida no ms-sessions via Gateway
        const responseSession = await fetch(`${GATEWAY_URL}/sessao-leitura/ultima-pagina/${bookId}`, {
            headers: getAuthHeader()
        });

        if (!responseSession.ok) return;
        const paginaFinal = await responseSession.json();

        // Busca o total de páginas no ms-catalog via Gateway
        const responseLivro = await fetch(`${GATEWAY_URL}/livros/detalhes/${bookId}` , {
            headers: getAuthHeader()
        });

        if (!responseLivro.ok) return;
        const livro = await responseLivro.json();
        const totalPaginas = livro.pages;

        if (document.getElementById("paginaAtual"))
            document.getElementById("paginaAtual").innerText = paginaFinal;

        if (document.getElementById("totalPaginas"))
            document.getElementById("totalPaginas").innerText = totalPaginas;

        if (document.getElementById("progresso")) {
            const porcentagem = (paginaFinal / totalPaginas) * 100;
            document.getElementById("progresso").style.width = porcentagem + "%";
        }

    } catch (error) {
        console.error("Erro ao atualizar progresso:", error);
    }
}


async function exibirInformacoesLivro() {
  try {
        const response = await fetch(`${GATEWAY_URL}/livros/detalhes/${bookId}`, {
            method: 'GET',
            headers: getAuthHeader()
        });

        if (response.ok) {
            const livro = await response.json();
            
            document.getElementById('capaLivro').src = livro.coverUrl;
            document.getElementById('tituloLivro').innerText = livro.title;
            document.getElementById('autorLivro').innerText = livro.author;
            document.getElementById('paginasLivro').innerText = livro.pages;
            document.getElementById('anoPublicacao').innerText = livro.publicationYear;
            document.getElementById('paisOrigem').innerText = livro.country;

            atualizarBarraProgresso(bookId, livro.pages);
        }
    } catch (error) {
        console.error("Erro ao carregar detalhes:", error);
    }
}


function mostrarFormularioEdicao() {
  document.getElementById('formulario-edicao').style.display = 'block';
}


function fecharFormularioEdicao() {
  document.getElementById('formulario-edicao').style.display = 'none';
}


async function enviarEdicaoLivro() {
  const dadosAtualizados = {
        title: document.getElementById('inputTitulo').value,
        author: document.getElementById('inputAutor').value,
        pages: parseInt(document.getElementById('inputPaginas').value),
        publicationYear: parseInt(document.getElementById('inputAnoPublicacao').value),
        country: document.getElementById('inputPaisOrigem').value
    };

    try {
        const response = await fetch(`${GATEWAY_URL}/livros/editar/${bookId}`, {
            method: 'PUT',
            headers: getAuthHeader(),
            body: JSON.stringify(dadosAtualizados)
        });

        if (response.ok) {
            exibirInformacoesLivro();
            fecharFormularioEdicao();
        }
    } catch (error) {
        console.error("Erro ao editar:", error);
    }
  }


function cancelarEdicao() {
  document.getElementById('formulario-edicao').style.display = 'none';
}


function mostrarConfirmacaoExclusao() {
  document.getElementById('confirmacaoExclusaoModal').style.display = 'block';
}


function fecharConfirmacaoExclusao() {
  document.getElementById('confirmacaoExclusaoModal').style.display = 'none';
}

// FUNÇÃO PARA EXCLUIR LIVRO APÓS CONFIRMAÇÃO
async function excluirLivroConfirmado() {

  try {
    const response = await fetch(`${GATEWAY_URL}/livros/excluir/${bookId}`, {
      method: 'DELETE',
      headers: getAuthHeader()
    });

    if (!response.ok) {
      throw new Error('Erro ao excluir o livro');
    }

    alert('Livro excluído com sucesso!');
    window.location.href = 'home.html';

  } catch (error) {
    console.error('Erro ao excluir o livro:', error);
    alert('Erro ao excluir o livro.');

  } finally {
    fecharConfirmacaoExclusao();
  }
}


function iniciarSessao() {
  if(bookId){
    window.location.href = `sessao-leitura.html?bookId=${bookId}`;
  } else{
    alert('Erro: livro nao encontrado.');
  }
    
}

function redirecionarEstatisticasLivro() {
  if(bookId){
      window.location.href = `estatistica-livro.html?bookId=${bookId}`;
  } else {
    console.error("ID não encontrado na URL");
  }

}

// Atualiza a barra de progresso a cada 30 segundos
setInterval(atualizarBarraProgresso, 30000);

window.onload = function(){
  exibirInformacoesLivro();
  atualizarBarraProgresso();
}
