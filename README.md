<h1>Projeto MS Sala de Leitura 📚</h1>


  >>🔧 Projeto concluído 

link do projeto monolitico 'Sala de Leitura': https://github.com/TaylamMoura/salaDeLeitura.

## *Visão Geral* 🔍
  -  Esta aplicação foi pensada para registrar o progresso de leitura do usuário e  estimular o foco na leitura. O usuário adiciona livros à "estante virtual", inicia sessões de leitura de um livro específico e utiliza um cronômetro para medir o tempo de leitura. Ao final da leitura, são exibidas estatísticas do tempo e progresso.
  - Este projeto é o resultado de uma migração estratégica de uma arquitetura monolítica para microsserviços, focando em escalabilidade, resiliência e alta performance.

<img width="1006" height="628" alt="capa" src="https://github.com/user-attachments/assets/bf8cbfef-b75d-4e81-8f89-fc8103b2d82f" />


## *Arquitetura do Sistema* 🏗️ 
O projeto foi decomposto em serviços independentes que se comunicam de forma eficiente:

**API Gateway:**  Centraliza as requisições e gerencia a segurança via Tokens JWT.

**Service Discovery (HashiCorp Consul):** Gerencia a localização de cada instância dos serviços, permitindo uma comunicação dinâmica.

**Microservices:**

 - **ms-auth:** Responsável pela autenticação e gestão de usuários.

 - **ms-catalog:** Integração com a Google Books API e gestão do acervo.

 - **ms-sessions:** Responsável pela gestão das sessões de leitura e estatisticas.

## Tecnologias utilizadas:  🛠️

- **Linguagem:** Java 17 com Spring Boot.
- **Banco de Dados:** PostgreSQL.
- **Sevice Discovery:** Consul.
- **Frontend:** Tailwind CSS.
- **Containerização:** Docker

## Algumas limitações e Dicas de Infra ⚠️

### **Consul: Erro de Rejoin (offline > 168h)**
Se o projeto ficar mais de 7 dias sem ser executado, o **Consul** pode recusar a reconexão ao cluster, apresentando o seguinte erro no log:
`startup error: refusing to rejoin cluster because server has been offline for more than the configured server_rejoin_age_max (168h0m0s) - consider wiping your data dir`

Isso acontece porque o servidor expira o tempo máximo de ausência no cluster. 

**Como resolver:**
Para solucionar, é necessário derrubar os arquivos antigos no Docker e recriar o container:

1. Derrube o ambiente:
```bash
docker compose down
```


2. Remova o container e o volume específico do Consul (substitua pelo nome do seu volume, se houver):
```bash
docker rm -f consul
```

3. Suba o serviço novamente:
```bash
docker compose up -d consul
```

**⚠️ Cuidado para não apagar o conteiner do banco de dados ⚠️**

