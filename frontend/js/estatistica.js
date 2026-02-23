//FUNÇÃO PARA OBTER O ID DO LIVRO
function obterLivroId() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('livroId');
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
function mostrarEstatisticasLivro() {
    const livroId = obterLivroId();

    if(!livroId) {
        console.error("ID não fornecido na URL");
        return;
    }

    fetch(`/estatistica-livro?livroId=${livroId}`)
        .then(response => {
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            document.getElementById("diasLidos").textContent = data.diasLidos;
            document.getElementById("mediaPaginasPorDia").textContent = data.mediaPaginasPorDia;
            document.getElementById("mediaTempoSessao").textContent = formatarHorasMinutos(data.mediaTempoSessao);
            document.getElementById("tituloLivro").textContent = localStorage.getItem("tituloLivro") || data.titulo;
            document.getElementById("autorLivro").textContent = localStorage.getItem("autorLivro") || data.autor;
            document.getElementById("paginasLivro").textContent = localStorage.getItem("paginasLivro") || data.paginas;
            document.getElementById("anoPublicacao").textContent = localStorage.getItem("anoPublicacao") || data.anoPublicacao;

            const capaLivro = localStorage.getItem("capaLivro") || data.urlCapa;
            document.getElementById("capaLivro").src = capaLivro;
            

        })
        .catch(error => console.error("Erro ao carregar estatísticas do livro:", error));
}
document.addEventListener("DOMContentLoaded", mostrarEstatisticasLivro);
