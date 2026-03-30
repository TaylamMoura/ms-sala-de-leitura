const GATEWAY_URL = 'http://localhost:8080';
const defaultImageUrl = 'https://via.placeholder.com/150x225?text=Sem+Capa';

// 1. FUNÇÃO AUXILIAR PARA PEGAR O TOKEN
function getAuthHeader() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// 2. FUNÇÃO PARA VERIFICAR AUTENTICAÇÃO
async function verificarAutenticacao() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'index.html';
        return;
    }
    try {
        const resposta = await fetch(`${GATEWAY_URL}/usuarios/validate-token`, {
            method: 'GET',
            headers: getAuthHeader()
        });
        if (!resposta.ok) {
            localStorage.clear();
            window.location.href = 'index.html';
        }
    } catch (error) {
        console.error('Erro ao validar login:', error);
    }
}

// 3. ONLOAD UNIFICADO (Executa ao abrir a página)
window.onload = async function() {
    await verificarAutenticacao();

    // Garante que a modal comece escondida
    const modal = document.getElementById('resultadoModal');
    if (modal) modal.classList.add('hidden');

    ExibirLivrosNaPag();
};

// 4. BUSCA LIVRO NA API (GOOGLE BOOKS VIA GATEWAY)
async function buscarLivroPorTitulo(titulo) {
  try {
    const response = await fetch(`${GATEWAY_URL}/livros/pesquisarLivro?title=${titulo}`, {
        method: 'GET',
        headers: getAuthHeader()
    });

    if (!response.ok) throw new Error('Erro na busca');

    const livros = await response.json();
    return livros;
  } catch (error) {
    console.error('Erro na busca:', error);
    return [];
  }
}

// 5. EVENTO DE PESQUISA (FORMULÁRIO)
document.getElementById('buscarLivroForm').onsubmit = async function(event) {
    event.preventDefault();
    const titulo = document.getElementById('tituloLivro').value;
    if (!titulo) return;

    const livros = await buscarLivroPorTitulo(titulo);
    const resultadoDiv = document.getElementById('resultadoBusca');
    const modal = document.getElementById('resultadoModal');

    resultadoDiv.innerHTML = '';

    if (livros && livros.length > 0) {
        livros.forEach(livro => {
            const tituloLivro = livro.title;
            const capaLivro = livro.coverUrl || defaultImageUrl;

            // Criar card do resultado com Tailwind
            const card = document.createElement('div');
            card.className = 'flex items-center gap-4 p-4 bg-white rounded-2xl shadow-sm border border-leitura-verde/10 hover:bg-leitura-creme cursor-pointer transition-all active:scale-95';

            card.innerHTML = `
                <img src="${capaLivro}" alt="${tituloLivro}" class="w-24 h-36 object-cover rounded-lg shadow-md">
                <div class="flex-1">
                    <p class="font-livro text-lg font-bold text-gray-800 leading-tight">${tituloLivro}</p>
                    <p class="text-[10px] text-leitura-verde font-mono uppercase mt-1 font-bold">Clique para adicionar à estante</p>
                </div>
            `;

            card.onclick = () => adicionarLivroAoBanco(livro);
            resultadoDiv.appendChild(card);
        });
        modal.classList.remove('hidden');
    } else {
        resultadoDiv.innerHTML = '<p class="text-center py-10 font-mono text-gray-500">Nenhum livro encontrado.</p>';
        modal.classList.remove('hidden');
    }
};

// 6. ADICIONAR LIVRO AO BANCO DE DADOS
async function adicionarLivroAoBanco(livro) {
    const userId = localStorage.getItem('userId');
    const livroData = {
        title: livro.title,
        author: livro.author || 'Autor desconhecido',
        pages: livro.pages || 0,
        coverUrl: livro.coverUrl || defaultImageUrl,
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

        if (response.ok) {
            document.getElementById('resultadoModal').classList.add('hidden');
            window.location.reload(); // Recarrega para mostrar na estante
        } else {
            alert("Erro ao salvar o livro.");
        }
    } catch (error) {
        console.error('Erro ao adicionar livro:', error);
    }
}

// 7. EXIBIR LIVROS NA ESTANTE (PÁGINA INICIAL)
// 7. EXIBIR LIVROS NA ESTANTE (PÁGINA INICIAL)
async function ExibirLivrosNaPag() {
    const userId = localStorage.getItem('userId');
    const minhasLeiturasDiv = document.getElementById('minhasLeituras');

    try {
        const response = await fetch(`${GATEWAY_URL}/livros/livrosSalvos/${userId}`, {
            method: 'GET',
            headers: getAuthHeader()
        });

        if (response.ok) {
            const livros = await response.json();
            minhasLeiturasDiv.innerHTML = '';

            // Usamos for...of para poder usar await dentro do loop
            for (const livro of livros) {
                const capa = livro.coverUrl || defaultImageUrl;
                let tagHtml = '';

                // LÓGICA DAS TAGS
                if (livro.finished) {
                    // Se o livro está marcado como finalizado no banco
                    tagHtml = `
                        <div class="absolute -top-3 -right-3 bg-leitura-laranja text-white text-[11px] font-bold px-3 py-1.5 rounded-full shadow-lg z-20 uppercase tracking-[0.1em] border-2 border-white flex items-center justify-center shadow-black/20"
                                style="font-family: 'Tenor Sans', sans-serif;">
                                Lido
                        </div>`;
                } else {
                    // Se não está finalizado, buscamos a última página no ms-sessions
                    try {
                        const respSessao = await fetch(`${GATEWAY_URL}/sessao-leitura/ultima-pagina/${userId}/${livro.bookId}`, {
                            headers: getAuthHeader()
                        });

                        if (respSessao.ok) {
                            const ultimaPagina = await respSessao.json();

                            if (ultimaPagina > 0 && livro.pages > 0) {
                                const porcentagem = Math.round((ultimaPagina / livro.pages) * 100);
                                if (porcentagem > 0) {
                                    tagHtml = `
                                        <div class="absolute -top-3 -right-2 bg-leitura-verde text-white text-[14px] font-medium px-2.5 py-1 rounded-lg shadow-lg z-20 border-2 border-white flex items-center justify-center min-w-[50px] shadow-black/20"
                                               style="font-family: 'Tenor Sans', sans-serif;">
                                              ${porcentagem}<span class="text-[10px] ml-0.5 opacity-80 font-sans">%</span>
                                        </div>`;
                                }
                            }
                        }
                    } catch (e) {
                        console.error("Erro ao buscar progresso do livro:", livro.bookId);
                    }
                }

                const card = document.createElement('div');
                card.className = 'group flex flex-col items-center w-full max-w-[150px] animate-in fade-in duration-700 relative';

                card.innerHTML = `
                    <a href="livro-detalhes.html?bookId=${livro.bookId}" class="w-full relative">
                        <div class="relative transition-all duration-300 group-hover:-translate-y-2">

                            ${tagHtml} <div class="overflow-hidden rounded-xl shadow-lg border border-gray-100">
                                <img src="${capa}" alt="${livro.title}" class="w-full aspect-[2/3] object-cover">
                            </div>

                            <div class="absolute inset-0 bg-black/10 opacity-0 group-hover:opacity-100 transition-opacity rounded-xl"></div>
                        </div>

                        <p class="mt-3 text-center font-livro text-sm font-bold text-gray-700 line-clamp-2 group-hover:text-leitura-verde transition-colors">
                            ${livro.title}
                        </p>
                    </a>
                `;
                minhasLeiturasDiv.appendChild(card);
            }
        }
    } catch (error) {
        console.error('Erro ao carregar estante:', error);
    }
}

// 8. CONTROLES DA MODAL (FECHAR)
function fecharModal() {
    document.getElementById('resultadoModal').classList.add('hidden');
}

document.querySelector('.close').onclick = fecharModal;

window.onclick = function(event) {
    const modal = document.getElementById('resultadoModal');
    if (event.target == modal) fecharModal();
};

// 9. FUNÇÕES DE NAVEGAÇÃO
function fazerLogout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

function mostrarEstatisticaGeral() {
    window.location.href = 'estatistica-geral.html';
}
