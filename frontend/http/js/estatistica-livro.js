const GATEWAY_URL = 'http://localhost:8080';

function getAuthHeader() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function obterLivroId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('bookId');
}


async function mostrarEstatisticasLivro() {
    const bookId = obterLivroId();
    const userId = localStorage.getItem('userId');

    if (bookId) {
            const linkVoltar = document.getElementById('linkVoltar');
            if (linkVoltar) {
                linkVoltar.href = `livro-detalhes.html?bookId=${bookId}`;
            }
        }

    if (!bookId || !userId) {
        console.error("ID de Livro ou Usuário não encontrado!");
        return;
    }

    try {
        const respLivro = await fetch(`${GATEWAY_URL}/livros/exibirDados/${bookId}/${userId}`, {
              headers: getAuthHeader()
        });

        if (respLivro.ok) {
            const livro = await respLivro.json();
            document.getElementById("tituloLivro").textContent = livro.title;
            document.getElementById("autorLivro").textContent = livro.author;
            document.getElementById("paginasLivro").textContent = livro.pages;
            document.getElementById("anoPublicacao").textContent = livro.publicationYear;
            if (livro.coverUrl) {
                document.getElementById("capaLivro").src = livro.coverUrl;
            }
            document.getElementById("country").textContent = livro.country || "N/A";
        }

        // Busca as estatísticas do ms-statistics
        const respStats = await fetch(`${GATEWAY_URL}/estatisticas/livro/${bookId}`, {
            headers: getAuthHeader()
        });

        if (respStats.ok) {
             const data = await respStats.json();

             document.getElementById("diasLidos").textContent = data.daysRead || 0;

             document.getElementById("mediaPaginasPorDia").textContent =
                 data.averagePagesPerDay ? data.averagePagesPerDay.toFixed(1) : "0";

             document.getElementById("mediaTempoSessao").textContent =
                 data.averageSessionTime ? data.averageSessionTime.toFixed(1) : "0";


        } else if (respStats.status === 401) {
            window.location.href = 'index.html';
        }

    } catch (error) {
        console.error("Erro ao carregar dados da página:", error);
    }
}

document.addEventListener("DOMContentLoaded", mostrarEstatisticasLivro);
