function formatarHorasMinutos(segundos) {
    let horas = Math.floor(segundos / 3600);
    let minutos = Math.floor((segundos % 3600) / 60);
    return `${horas} hora${horas !== 1 ? "s" : ""} e ${minutos} minutos`;
}

// Função para carregar estatísticas GERAIS
function mostrarEstatisticaGeral() {

    fetch("/estatistica-geral")
        .then(response => response.json())
        .then(data => {
            console.log("Dados recebidos da API:", data);

            if (!data || !data.rankingLivros || data.rankingLivros.length === 0) {
                console.error("Erro: Nenhum livro encontrado no ranking.");
                return;
            }

            const rankingLivros = data.rankingLivros;

            // Exibe o primeiro livro como destaque do ranking
            const capaLivro = document.getElementById("capaLivroPrincipal");
            const tituloLivro = document.getElementById("tituloLivroPrincipal");
            const autorLivro = document.getElementById("autorLivroPrincipal");

            if (capaLivro) capaLivro.src = rankingLivros[0].urlCapa;
            if (tituloLivro) tituloLivro.textContent = rankingLivros[0].titulo;
            if (autorLivro) autorLivro.textContent = rankingLivros[0].autor;

            // Exibe estatísticas abaixo dos livros
            document.getElementById("totalHorasLidas").textContent = formatarHorasMinutos(data.totalSegundosLidos);
            document.getElementById("totalPaginasLidas").textContent = data.totalPaginasLidas;
            document.getElementById("totalLivrosLidos").textContent = data.totalLivrosLidos;

            // Exibe os próximos 4 livros na lista secundária
            const rankingContainer = document.querySelector(".livros-secundarios");
            if (rankingContainer) {
                rankingContainer.innerHTML = ""; 

                rankingLivros.slice(1, Math.min(5, rankingLivros.length)).forEach(livro => { 
                    const livroDiv = document.createElement("div");
                    livroDiv.classList.add("livro-item");

                    livroDiv.innerHTML = `
                        <div class="livro-capa">
                            <img src="${livro.urlCapa}" alt="Capa do livro">
                        </div>
                        <div class="info-bloco">
                            <span>${livro.titulo}</span>
                            <span>${livro.autor}</span>
                        </div>
                    `;
                    rankingContainer.appendChild(livroDiv);
                });
            } else {
                console.error("Elemento 'livros-secundarios' não encontrado!");
            }
        })
        .catch(error => console.error("Erro ao carregar estatísticas gerais:", error));
}

document.addEventListener("DOMContentLoaded", mostrarEstatisticaGeral);

