const GATEWAY_URL = "http://localhost:8080"; 

function getAuthHeader(){
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}


function formatarHorasMinutos(segundos) {
    let horas = Math.floor(segundos / 3600);
    let minutos = Math.floor((segundos % 3600) / 60);
    return `${horas} hora${horas !== 1 ? "s" : ""} e ${minutos} minutos`;
}

// Função para carregar estatísticas GERAIS
function mostrarEstatisticaGeral() {
    try{
        const response = fetch(`${GATEWAY_URL}/estatistica-geral`, {
            method: 'GET',
            headers: getAuthHeader(),
    });

    if(!response.ok){
        if(response.status === 401) window.location.href = 'inicio.html';
        throw new Error("Erro ao carregar estatísticas gerais: " + response.status);
    }

    const data = response.json();

    document.getElementById("totalHorasLidas").textContent = formatarHorasMinutos(data.totalSecondsRead);
    document.getElementById("totalPaginasLidas").textContent = data.totalPagesRead || 0;
    document.getElementById("totalLivrosLidos").textContent = data.totalBooksReads || 0;

    const campoTotalPaises = document.getElementById("totalPaisesLidos");
        if (campoTotalPaises) campoTotalPaises.textContent = data.totalCountriesRead || 0;

        // 2. Ranking de Livros (Destaque e Secundários)
        const ranking = data.bookRanking;
        if (ranking && ranking.length > 0) {
            // Primeiro livro (Destaque)
            const capaPrincipal = document.getElementById("capaLivroPrincipal");
            if (capaPrincipal) capaPrincipal.src = ranking[0].coverUrl;
            
            const tituloPrincipal = document.getElementById("tituloLivroPrincipal");
            if (tituloPrincipal) tituloPrincipal.textContent = ranking[0].title;
            
            const autorPrincipal = document.getElementById("autorLivroPrincipal");
            if (autorPrincipal) autorPrincipal.textContent = ranking[0].author;

            // Próximos 4 livros (Secundários)
            renderizarLivrosSecundarios(ranking.slice(1, 5));
        }

        // 3. Ranking de Países (Top 3)
        renderizarRankingPaises(data.topCountries);

    } catch (error) {
        console.error("Erro na requisição das estatísticas:", error);
    }
}

function renderizarLivrosSecundarios(lista) {
    const container = document.querySelector(".livros-secundarios");
    if (!container) return;

    container.innerHTML = ""; 
    lista.forEach(livro => {
        const div = document.createElement("div");
        div.classList.add("livro-item");
        div.innerHTML = `
            <div class="livro-capa">
                <img src="${livro.coverUrl}" alt="Capa de ${livro.title}">
            </div>
            <div class="info-bloco">
                <p class="ranking-titulo">${livro.title}</p>
                <p class="ranking-autor">${livro.author}</p>
            </div>
        `;
        container.appendChild(div);
    });
}

function renderizarRankingPaises(paises) {
    const container = document.getElementById("rankingPaisesContainer");
    if (!container || !paises || paises.length === 0) {
        container.innerHTML = "<span>Nenhum dado disponível</span>";
        return;
    }

    container.innerHTML = "";
    paises.forEach((item, index) => {
        const span = document.createElement("span");
        span.style.display = "block"; // Faz um ficar embaixo do outro
        span.style.fontSize = "0.9em"; // Um pouco menor para caber bem
        span.innerHTML = `<strong>${index + 1}º ${item.country}</strong> (${item.count} livros)`;
        container.appendChild(span);
    });
}

document.addEventListener("DOMContentLoaded", mostrarEstatisticaGeral);
