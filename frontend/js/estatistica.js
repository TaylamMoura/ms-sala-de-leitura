const GATEWAY_URL = 'http://localhost:8080';

//FUNÇÃO AUXILIAR PARA HEADERS
function getAuthHeader( ){
    const token = localStorage.getItem('token');
    return{
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

//FUNÇÃO PARA OBTER O ID DO LIVRO
function obterLivroId() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('bookId');
}


//Função para converter horário para uma forma légivel no front-end
function formatarHorasMinutos(horas) {
    let horasInteiras = Math.floor(horas);
    let minutos = Math.round((horas - horasInteiras) * 60);

    return `${horasInteiras} hora${horasInteiras !== 1 ? "s" : ""} e ${minutos} minutos`;
}

//FUNÇÃO PARA CARREGAR OS DADOS DO LIVRO PARA EXIBIR NO FRONT
function carregarDadosLivro() {
    document.getElementById("tituloLivro").textContent = localStorage.getItem("tituloLivro") || "Título não disponível";
    document.getElementById("autorLivro").textContent = localStorage.getItem("autorLivro") || "Autor não disponível";
    document.getElementById("paginasLivro").textContent = localStorage.getItem("paginasLivro") || "XX";
    document.getElementById("anoPublicacao").textContent = localStorage.getItem("anoPublicacao") || "XXXX";

    const capaLivro = localStorage.getItem("capaLivro");
    if (capaLivro) {
        document.getElementById("capaLivro").src = capaLivro;
    }
}

document.addEventListener("DOMContentLoaded", carregarDadosLivro);


// Função para carregar estatísticas do LIVRO
async function mostrarEstatisticasLivro() {
    const bookId = obterLivroId();
    if(!bookId) {
        console.error("ID não fornecido na URL");
        return;
    }

    try{
        const respLivro = await fetch(`${GATEWAY_URL}/livros/exibirDados/${bookId}`, {
            headers: getAuthHeader()
        });

        if(!respLivro.ok){
            const livro = await respLivro.json();
            document.getElementById("tituloLivro").textContent = livro.title ;
            document.getElementById("autorLivro").textContent = livro.author ;
            document.getElementById("paginasLivro").textContent = livro.pages;
            document.getElementById("anoPublicacao").textContent = livro.publicationYear;
            document.getElementById("capaLivro").src = livro.coverUrl;
            document.getElementById("country").textContent = livro.country || "Não Informado";
    
        }

        const respStats = await fetch(`${GATEWAY_URL}/estatisticas/livro/${bookId}`, {
            headers: getAuthHeader()
        });

        if (respStats.ok) {
            const data = await respStats.json();

            document.getElementById("diasLidos").textContent = data.daysRead || 0;
            document.getElementById("mediaPaginasPorDia").textContent = 
                data.averagePagesPerDay ? data.averagePagesPerDay.toFixed(1) : "0";
            
            // O Java envia a média de tempo em segundos, formatamos aqui
            document.getElementById("mediaTempoSessao").textContent = formatarTempo(data.averageSessionTime);
        } else if (respStats.status === 401) {
            window.location.href = 'inicio.html';
        }

    } catch (error) {
        console.error("Erro ao carregar dados da página:", error);
    }
}

document.addEventListener("DOMContentLoaded", mostrarEstatisticasLivro);
