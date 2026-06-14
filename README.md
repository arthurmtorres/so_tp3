# 📦 BCC264 - TP3: Comunicação Interprocessos (IPC)


Este projeto implementa um sistema centralizado de controle de estoque que disponibiliza suas funcionalidades através de quatro diferentes paradigmas de Comunicação Interprocessos (IPC): **REST, GraphQL, gRPC e WebSockets**. 

O objetivo principal é demonstrar a interoperabilidade entre múltiplas interfaces acessando um único estado compartilhado em memória, garantindo a consistência dos dados em cenários de concorrência.

## 🛠️ Tecnologias Utilizadas
* **Java 17** + **Spring Boot 3**
* **REST** (Spring Web)
* **GraphQL** (Spring for GraphQL)
* **gRPC** (grpc-spring-boot-starter + Protobuf)
* **WebSockets** (Spring WebSocket)
* **Docker & Docker Compose**

## 🏛️ Arquitetura e Concorrência
O coração do sistema é a classe `EstoqueService`. Ela atua como a única fonte da verdade, armazenando o estado dos itens (cadeira, mesa, monitor) em um `ConcurrentHashMap`. 

Para evitar **Condições de Corrida (Race Conditions)** durante compras simultâneas, os métodos de mutação (`realizarCompra` e `cancelarCompra`) são protegidos pela palavra-chave `synchronized`. Isso garante a atomicidade das operações, permitindo que o sistema lide de forma segura com o escalonamento de threads do Sistema Operacional.

Sempre que uma alteração de estoque ocorre (seja via REST, GraphQL ou gRPC), o serviço notifica todos os clientes conectados via **WebSocket** em tempo real, informando o novo saldo e a origem da transação.

---

## 🚀 Como Executar (Ambiente Dockerizado)

A aplicação foi totalmente containerizada para garantir a reprodutibilidade. Não é necessário ter o Java ou o Maven instalados na máquina host.

**Pré-requisitos:**
* Docker e Docker Compose instalados.

**Passo a passo:**
1. Clone o repositório ou descompacte o projeto.
2. Navegue até a pasta raiz do projeto via terminal.
3. Suba o contêiner usando o Docker Compose:

```bash
docker compose up --build
O Docker fará o download da imagem base, compilará o código via Maven e iniciará o servidor Tomcat.

O servidor estará escutando nas seguintes portas:

    8080: Tráfego HTTP (REST, GraphQL e WebSockets)

    50051: Tráfego HTTP/2 Multiplexado (gRPC)

(Opcional) A imagem também está disponível publicamente no Docker Hub e pode ser executada com:
Bash

docker run -p 8080:8080 -p 50051:50051 <SEU_USUARIO_DOCKERHUB>/bcc264-tp3:latest

🧪 Como Testar as Interfaces

Na raiz do projeto, há um arquivo chamado testes.http. Se você estiver utilizando o VS Code com a extensão REST Client, basta clicar em Send Request nos blocos de código para testar as requisições REST e GraphQL.
1. Testando o WebSocket (Tempo Real)

Para visualizar o broadcast de mensagens funcionando, abra o console de desenvolvedor (F12) do seu navegador de preferência e execute o script abaixo antes de realizar uma compra:
JavaScript

let socket = new WebSocket("ws://localhost:8080/ws/estoque");
socket.onmessage = function(event) {
    console.log("🔔 EVENTO DE ESTOQUE:", JSON.parse(event.data));
};
console.log("Escutando alterações de estoque...");

2. Endpoints REST

    Health Check: GET http://localhost:8080/health

    Listar Todos: GET http://localhost:8080/items

    Comprar (Exemplo de Payload):
    JSON

    POST http://localhost:8080/purchase
    {
      "item": "monitor",
      "quantity": 1,
      "clientId": "cli-01"
    }

3. Endpoint GraphQL

    URL Mapeada: POST http://localhost:8080/graphql

    Comprar (Exemplo de Mutation):
    GraphQL

    mutation { 
      purchase(item: "cadeira", quantity: 2, clientId: "cli-gql") { 
        success 
        remaining 
      } 
    }
