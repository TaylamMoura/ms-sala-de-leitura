const GATEWAY_URL = 'http://localhost:8080'

//FUNÇÃO AUXILIAR PARA PEGAR O TOKEN
function getAuthHeader(){
    const token = localStorage.getItem('token');
    return{
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}


//FUNÇÃO PARA VERIFICAR AUTENTICAÇÃO
async function verificarAutenticacao() {
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = 'inicio.html';
        return;
    }

    try {
        const resposta = await fetch(`${GATEWAY_URL}/usuarios/validate-token`, {
            method: 'GET', 
            headers: getAuthHeader()
        });

        if (!resposta.ok) {
            console.warn("Token inválido ou expirado");
            localStorage.clear(); 
            window.location.href = 'inicio.html';
        }
    } catch (error) {
        console.error('Erro ao validar login:', error);
    }
}


// UNIFIQUE O ONLOAD AQUI
window.onload = async function() {
    await verificarAutenticacao();

    if (document.getElementById('resultadoModal')) {
        document.getElementById('resultadoModal').style.display = 'none';
    }

    exibirLivrosNaPag();
};


// BUSCA LIVRO NA API
async function buscarLivroPorTitulo(titulo) {

  try {
    const response = await fetch(`${GATEWAY_URL}/livros/pesquisarLivro?title=${titulo}`, {
        method: 'GET',
        headers: getAuthHeader()
    });

    if (!response.ok) throw new Error('Livro não encontrado');

    const livro = await response.json();
    return livro ? [livro] : []; 
  } catch (error) {
    console.error('Erro na busca:', error);
    return [];
  }
}
const defaultImageUrl = 'https://via.placeholder.com/150x150';




//EVENTO ONSUBMIT DO FORMULARIO DE PESQUISA DO LIVRO
document.getElementById('buscarLivroForm').onsubmit = async function(event) {
  event.preventDefault();
  const titulo = document.getElementById('tituloLivro').value;

  // 1. Chama a sua nova função que bate no Gateway/Java
  const livros = await buscarLivroPorTitulo(titulo); 
  
  const resultadoDiv = document.getElementById('resultadoBusca');
  resultadoDiv.innerHTML = '';

  if (livros && livros.length > 0) {
    livros.forEach(livro => {
  const tituloLivro = livro.title; 
  const capaLivro = livro.coverUrl || defaultImageUrl;

  const livroDiv = document.createElement('div');
  livroDiv.className = 'livro-item';
  livroDiv.innerHTML = `<img src="${capaLivro}" alt="${tituloLivro}"><p>${tituloLivro}</p>`;

  livroDiv.addEventListener('click', () => {
    adicionarLivroAoBanco(livro); 
  });

  resultadoDiv.appendChild(livroDiv);
});

    document.getElementById('resultadoModal').style.display = 'block';

  } else {
    resultadoDiv.innerHTML = '<p>Livro não encontrado.</p>';
  }
};



//FUNÇÃO PARA ADD LIVRO NA PÁGINA INICIAL
function adicionarLivroNaPagina(livro) {
    const minhasLeiturasDiv = document.getElementById('minhasLeituras');
    const tituloLivro = livro.title;
    const capaLivro = livro.coverUrl ? livro.coverUrl : defaultImageUrl;

    const livroDiv = document.createElement('div');
    livroDiv.classList.add('livro-item');

    const linkElement = document.createElement('a');
    linkElement.href = `meu-livro.html?bookId=${livro.bookId}`;

    const imgElement = document.createElement('img');
    imgElement.src = capaLivro;
    imgElement.alt = tituloLivro;

    const tituloElement = document.createElement('p');
    tituloElement.textContent = tituloLivro;

    linkElement.appendChild(imgElement);
    linkElement.appendChild(tituloElement);

    livroDiv.appendChild(linkElement);

    minhasLeiturasDiv.appendChild(livroDiv);
}

// Adiciona um evento de clique para fechar o modal
document.querySelector('.close').onclick = function() {
    document.getElementById('resultadoModal').style.display = 'none';
};

// Fecha o modal quando o usuário clica fora da janela de conteúdo
window.onclick = function(event) {
    if (event.target == document.getElementById('resultadoModal')) {
        document.getElementById('resultadoModal').style.display = 'none';
    }
};


//FUNÇÃO PARA ADD LIVRO AO BANCO DE DADOS
async function adicionarLivroAoBanco(livro) {
  const userId = localStorage.getItem('userId');

  const livroData = {
    title: livro.title,
    author: livro.author || 'Autor desconhecido',
    pages: livro.pages || 0,
    coverUrl: livro.coverUrl || 'https://via.placeholder.com/150',
    publicationYear: livro.publicationYear || new Date().getFullYear(),
    finished: false, 
    country: livro.country || 'N/A',
    userId: parseInt(userId) 
  };

  try {
    const response = await fetch(`${GATEWAY_URL}/livros/salvarLivro`, {
      method: 'POST',
      headers: getAuthHeader(),
      body: JSON.stringify(livroData)
    });

    if (!response.ok) throw new Error('Erro ao salvar no banco');

    const novoLivro = await response.json();
    adicionarLivroNaPagina(novoLivro);
    document.getElementById('resultadoModal').style.display = 'none';
    window.location.reload();
  } catch (error) {
    console.error('Erro ao adicionar livro:', error);
  }
}


//FUNÇÃO PARA EXIBIR OS LIVROS SALVOS NA PÁGINA
async function ExibirLivrosNaPag() {
  const userId = localStorage.getItem('userId');

  try {
     const response = await fetch(`${GATEWAY_URL}/livros/livrosSalvos/${userId}`, {
             method: 'GET',
             headers: getAuthHeader()
         });

     if (!response.ok) {
           if (response.status === 401 || response.status === 403) {
                  alert('Sessão expirada. Faça login novamente.');
                  window.location.href = 'inicio.html';
           } else {
                  throw new Error('Erro ao buscar livros do banco de dados');
           }
           return;
     }

    const livros = await response.json();
    const minhasLeiturasDiv = document.getElementById('minhasLeituras');
    minhasLeiturasDiv.innerHTML = '';
    livros.forEach(livro => {
    adicionarLivroNaPagina(livro);
      });
  } catch (error) {
    console.error('Erro ao carregar minhas leituras:', error);
  }
}


//FUNÇÃO PARA LOGOUT
async function fazerLogout() {
    localStorage.removeItem('token');
    window.location.href = 'inicio.html';
}


//FUNÇÃO PARA CARREGAR PAGE ESTATISTICA-GERAL
function mostrarEstatisticaGeral() {
  window.location.href = 'estatistica-geral.html';
}


// Chama a função ExibirLivrosNaPag ao carregar a página
window.onload = ExibirLivrosNaPag;
