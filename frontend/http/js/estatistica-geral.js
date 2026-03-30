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
async function mostrarEstatisticaGeral() {
    try{
        const response = await fetch(`${GATEWAY_URL}/estatisticas/geral`, {
            method: 'GET',
            headers: getAuthHeader(),
    });

    if(!response.ok){
        if(response.status === 401) window.location.href = 'index.html';
        throw new Error("Erro ao carregar estatísticas gerais: " + response.status);
    }

    const data = await response.json();

    document.getElementById("totalHorasLidas").textContent = formatarHorasMinutos(data.totalSecondsRead);
    document.getElementById("totalPaginasLidas").textContent = data.totalPagesRead || 0;
    document.getElementById("totalLivrosLidos").textContent = data.totalBooksRead || 0;

    const campoTotalPaises = document.getElementById("totalPaisesLidos");
        if (campoTotalPaises) campoTotalPaises.textContent = data.totalCountriesRead || 0;

        // 2. Ranking de Livros (Destaque e Secundários)
        const ranking = data.rankingBooks;
        if (ranking && ranking.length > 0) {
            document.getElementById("capaLivroPrincipal").src = ranking[0].coverUrl || 'img/capa-livro.png';
            document.getElementById("tituloLivroPrincipal").textContent = ranking[0].title;
            document.getElementById("autorLivroPrincipal").textContent = ranking[0].author;

            renderizarLivrosSecundarios(ranking.slice(1, 5));
        }

        // 3. Ranking de Países (Top 3)
        renderizarRankingPaises(data.topCountries);

    } catch (error) {
        console.error("Erro na requisição das estatísticas:", error);
    }
}

function renderizarLivrosSecundarios(lista) {
    const container = document.getElementById("livros-secundarios"); // Mudado de querySelector para getElementById
    if (!container) return;

    container.innerHTML = "";
    lista.forEach(livro => {
        const div = document.createElement("div");
        // Classes do Tailwind para manter o estilo que você criou
        div.className = "flex items-center gap-4 p-3 bg-white/40 rounded-2xl border border-white hover:bg-white/60 transition-all group";
        div.innerHTML = `
            <img src="${livro.coverUrl || 'img/capa-livro.png'}" class="w-14 h-20 rounded-md shadow-md object-cover">
            <div class="flex flex-col">
                <span class="font-principal text-sm font-bold text-gray-800 leading-snug">${livro.title}</span>
                <span class="font-mono text-[11px] text-gray-500 italic">${livro.author}</span>
            </div>
        `;
        container.appendChild(div);
    });
}

function renderizarRankingPaises(paises) {
    const container = document.getElementById("rankingPaisesContainer");
    if (!container) return;

    container.innerHTML = "";
    if (!paises || paises.length === 0) {
        container.innerHTML = "<p class='text-xs text-gray-400'>Nenhum país registrado</p>";
        return;
    }

    paises.forEach((item, index) => {
        const div = document.createElement("div");
        div.className = "flex justify-between items-center text-sm";
        // Ajustado para os nomes do DTO: country e count
        div.innerHTML = `
            <span class="flex items-center gap-3">
                <span class="w-5 h-5 flex items-center justify-center ${index === 0 ? 'bg-leitura-laranja' : 'bg-gray-200'} text-white text-[9px] font-bold rounded-full">${index + 1}º</span>
                <span class="font-principal">${item.country}</span>
            </span>
            <span class="font-mono text-xs text-leitura-verde font-bold">${item.count} livros</span>
        `;
        container.appendChild(div);
    });
}

document.addEventListener("DOMContentLoaded", mostrarEstatisticaGeral);
