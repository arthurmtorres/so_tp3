# 📦 TP3: Comunicação Interprocessos (IPC)

**Disciplina:** BCC264 - Sistemas Operacionais  
**Professor:** Prof. Dr. Carlos Frederico M. C. Cavalcanti  
**Autores:** 
- Thiago Linhares Lage - 24.2.4032
- Arthur Mendes Torres - 24.2.4042

---

## Sobre o projeto

Este projeto implementa um sistema centralizado de controle de estoque que disponibiliza suas funcionalidades através de quatro diferentes paradigmas de Comunicação Interprocessos (IPC): **REST, GraphQL, gRPC e WebSockets**. 

O objetivo principal é demonstrar a interoperabilidade entre múltiplas interfaces acessando um único estado compartilhado em memória, garantindo a consistência dos dados em cenários de concorrência.

---

## 🛠️ Tecnologias Utilizadas
* **Java 17** + **Spring Boot 3**
* **REST** (Spring Web)
* **GraphQL** (Spring for GraphQL)
* **gRPC** (grpc-spring-boot-starter + Protobuf)
* **WebSockets** (Spring WebSocket)
* **Docker & Docker Compose**

---

## Informações da Imagem no Docker Hub

Conforme exigido nas especificações de entrega, o projeto foi containerizado e a imagem está publicada no Docker Hub:

* **Usuário / Organização:** `arthurmendestorres`
* **Nome da Imagem:** `bcc264-tp3`
* **Tag da Submissão:** `latest`
* **Link direto:** `https://hub.docker.com/r/arthurmendestorres/bcc264-tp3`

---

## 🚀 Como Executar (Ambiente Dockerizado)

A aplicação foi totalmente containerizada para garantir a reprodutibilidade. 

**Pré-requisitos:**
* Docker e Docker Compose instalados.

**Passo a passo:**
1. Descompacte o projeto.
2. Navegue até a pasta raiz do projeto via terminal.
3. Suba o contêiner usando o Docker Compose:

```bash
docker compose up --build
```

O Docker fará o download da imagem base, compilará o código via Maven e iniciará o servidor Tomcat.

O servidor estará escutando nas seguintes portas:

    8080: Tráfego HTTP (REST, GraphQL e WebSockets)

    50051: Tráfego HTTP/2 Multiplexado (gRPC)

(Opcional) A imagem também está disponível publicamente no Docker Hub e pode ser executada com:

```bash
docker run -p 8080:8080 -p 50051:50051 arthurmendestorres/bcc264-tp3:latest
```

## 🧪 Como Testar as Interfaces

Na raiz do projeto, há um arquivo chamado testes.http. Se você estiver utilizando o VS Code com a extensão REST Client, basta clicar em Send Request nos blocos de código para testar as requisições REST e GraphQL.

Caso não tenha essa extensão, basta digitar os seguintes comandos no terminal para usar as interfaces.


1. REST


- Verificar se o servidor está de pé
```bash
curl -s -X GET http://localhost:8080/health
```

- Listar todos os itens
```bash
curl -s -X GET http://localhost:8080/items
```

- Consultar item específico
```bash
curl -s -X GET http://localhost:8080/items/monitor
```

- Comprar item
```bash
curl -s -X POST http://localhost:8080/purchase -H "Content-Type: application/json" -d '{"item": "monitor", "quantity": 1, "clientId": "cli-01"}'
```

- Cancelar compra
```bash
curl -s -X POST http://localhost:8080/cancel -H "Content-Type: application/json" -d '{"item": "monitor", "quantity": 1, "clientId": "cli-01"}'
```


2. GraphQL


- Listar todos os itens
```bash
curl -s -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query": "query { items { name stock } }"}'
```

- Consultar item específico
```bash
curl -s -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query": "query { item(id: \"monitor\") }"}'
```

- Comprar item
```bash
curl -s -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query": "mutation { purchase(item: \"monitor\", quantity: 1, clientId: \"cli-01\") { success remaining } }"}'
```

- Cancelar compra
```bash
curl -s -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d '{"query": "mutation { cancel(item: \"monitor\", quantity: 1, clientId: \"cli-01\") }"}'
```


3. gRPC


- Listar todos os itens
```bash
grpcurl -plaintext localhost:9090 inventory.InventoryService/ListItems
```

- Consultar item específico
```bash
grpcurl -plaintext -d '{"id": "monitor"}' localhost:9090 inventory.InventoryService/GetItem
```

- Comprar item
```bash
grpcurl -plaintext -d '{"item": "monitor", "quantity": 1, "clientId": "cli-grpc"}' localhost:9090 inventory.InventoryService/Purchase
```

- Cancelar compra
```bash
grpcurl -plaintext -d '{"item": "monitor", "quantity": 1, "clientId": "cli-grpc"}' localhost:9090 inventory.InventoryService/Cancel
```


4. WebSocket


- Conectar para escutar eventos em tempo real
```bash
websocat ws://localhost:8080/ws/estoque
```

## 🧹 Limpeza do Ambiente
Após finalizar os testes, destrua os contêineres e a rede virtual executando:
```bash
docker compose down