//FUNÇÃO PARA EXIBIR MODAL DE PESQUISA
window.onload = function() {
  document.getElementById('resultadoModal').style.display = 'none';
  console.log('Modal escondido no onload');
};

//FUNÇÃO PARA EXIBIR NOME DE USUÁRIO NO FRONT
async function exibirNomeUsuario() {
    try {
        const response = await fetch('/usuario-logado', {
            method: 'GET',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            throw new Error('Erro ao buscar o nome do usuário.');
        }

        const data = await response.json();
        const nomeUsuario = data.usuario || 'Usuário';
        const tituloH2 = document.querySelector('.titulo__secundario');
        tituloH2.innerText = `Minhas Leituras | ${nomeUsuario}`;
    } catch (error) {
        console.error('Erro ao buscar o nome do usuário:', error);
    }
}
window.addEventListener('load', exibirNomeUsuario);

// CONFIG APIKEY
async function fetchApiKey() {
    try {
        const response = await fetch('/api-key');
        const data = await response.text();
        return data;
    } catch (error) {
        console.error('Erro ao buscar a chave da API:', error);
        return null;
    }
}


// BUSCA LIVRO NA API
async function buscarLivroPorTitulo(titulo) {
  const apiKey = await fetchApiKey();
  console.log('API Key:', apiKey);

  if (!apiKey) {
     console.error('Chave não encontrada');
     return;
  }
  const url = `https://www.googleapis.com/books/v1/volumes?q=intitle:${titulo}&key=${apiKey}`;
  console.log('URL:', url);

  try {
    const response = await fetch(url);
    console.log('Response:', response);

    if (!response.ok) {
      throw new Error('Erro ao buscar livro');
    }
    const data = await response.json();
    console.log('Data:', data);
    return data.items;
  } catch (error) {
    console.error('Erro ao buscar livro:', error);
    return null;
  }
}

const defaultImageUrl = 'https://via.placeholder.com/150x150';




//EVENTO ONSUBMIT DO FORMULARIO DE PESQUISA DO LIVRO
document.getElementById('buscarLivroForm').onsubmit = async function(event) {
  event.preventDefault();
  const titulo = document.getElementById('tituloLivro').value;
  console.log('Título:', titulo);

  const livros = await buscarLivroPorTitulo(titulo);
  console.log('Livros:', livros);
  const resultadoDiv = document.getElementById('resultadoBusca');
  resultadoDiv.innerHTML = '';

  if (livros && livros.length > 0) {
    livros.forEach(livro => {
      const tituloLivro = livro.volumeInfo.title;
      const capaLivro = (livro.volumeInfo.imageLinks && livro.volumeInfo.imageLinks.thumbnail) ? livro.volumeInfo.imageLinks.thumbnail : defaultImageUrl;
      console.log('URL da Capa:', capaLivro);

      const livroDiv = document.createElement('div');
      livroDiv.className = 'livro-item';
      livroDiv.innerHTML = `<img src="${capaLivro}" alt="${tituloLivro}"><p>${tituloLivro}</p>`;

      livroDiv.addEventListener('click', () => {
        const livroData = {
          titulo: livro.volumeInfo.title,
          autor: livro.volumeInfo.authors ? livro.volumeInfo.authors[0] : 'Autor desconhecido',
          paginas: livro.volumeInfo.pageCount || 0,
          urlCapa: (livro.volumeInfo.imageLinks &&
                    livro.volumeInfo.imageLinks.thumbnail &&
                    livro.volumeInfo.imageLinks.thumbnail.trim() !== "")
                    ? livro.volumeInfo.imageLinks.thumbnail
                    : 'https://via.placeholder.com/150',
          anoPublicacao: livro.volumeInfo.publishedDate ? livro.volumeInfo.publishedDate.split('-')[0] : new Date().getFullYear()
        };

        adicionarLivroAoBanco(livroData);
      });

      resultadoDiv.appendChild(livroDiv);
    });

    document.getElementById('resultadoModal').style.display = 'block';
    console.log('Exibindo modal com resultados');

  } else {
    resultadoDiv.innerHTML = '<p>Livro não encontrado.</p>';
  }
};



//FUNÇÃO PARA ADD LIVRO NA PÁGINA INICIAL
function adicionarLivroNaPagina(livro) {
    const minhasLeiturasDiv = document.getElementById('minhasLeituras');
    const tituloLivro = livro.titulo;
    const capaLivro = livro.urlCapa ? livro.urlCapa : defaultImageUrl;

    const livroDiv = document.createElement('div');
    livroDiv.classList.add('livro-item');

    const linkElement = document.createElement('a');
    linkElement.href = `meu-livro.html?id=${livro.id}`;

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
  const livroData = {
    titulo: livro.titulo,
    autor: livro.autor || 'Autor desconhecido',
    paginas: livro.paginas || 0,
    urlCapa: livro.urlCapa || 'https://via.placeholder.com/150',
    anoPublicacao: livro.anoPublicacao || new Date().getFullYear()
  };

  try {
    const response = await fetch('/salvarLivro', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(livroData)
    });

    if (!response.ok) {
      throw new Error('Erro ao adicionar livro ao banco de dados');
    }
    const novoLivro = await response.json();
    adicionarLivroNaPagina(novoLivro);

    // Fecha a janela modal
    document.getElementById('resultadoModal').style.display = 'none';
    window.location.reload();
  } catch (error) {
    console.error('Erro ao adicionar livro ao banco de dados:', error);
  }
}


//FUNÇÃO PARA EXIBIR OS LIVROS SALVOS NA PÁGINA
async function ExibirLivrosNaPag() {
  try {
     const response = await fetch('/livrosSalvos', {
             method: 'GET',
             credentials: 'include',
             headers: {
                  'Content-Type': 'application/json'
             }
         });

     if (!response.ok) {
           if (response.status === 403) {
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
    try {
            await fetch('/logout', { method: 'POST', credentials: 'include' });
        } catch (error) {
            console.error('Erro no logout:', error);
        }
    window.location.href = 'inicio.html';
}


//FUNÇÃO PARA CARREGAR PAGE ESTATISTICA-GERAL
function mostrarEstatisticaGeral() {
  window.location.href = 'estatistica-geral.html';
}


// Chama a função ExibirLivrosNaPag ao carregar a página
window.onload = ExibirLivrosNaPag;