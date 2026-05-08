# MicroChefs - Sistema de Restaurante com Microserviços

Este projeto modela um sistema de restaurante usando uma arquitetura de microserviços, com comunicação assíncrona via RabbitMQ.

📄 **Relatório Completo:** Consulte o arquivo [relatorio_mensageria.md](./relatorio_mensageria.md) para o detalhamento da arquitetura de mensageria da disciplina de Sistemas Distribuídos.

## 📌 Desenvolvedor

* **Caike Machado Batista Costa**
  * Responsável pela arquitetura e desenvolvimento de todos os microsserviços (Pedidos, Produtos, Cozinha, Usuários).
  * Desenvolvimento do Front-end em Angular.
  * Integração do sistema de mensageria assíncrona com RabbitMQ.
  * Orquestração da infraestrutura (Docker e Eureka Server).

---

## 🔄 Fluxo de Mensageria

1. Cliente faz um pedido pelo front.
2. O pedido vai para o serviço de pedidos (REST).
3. Ao criar o pedido, ele publica um evento no RabbitMQ (`pedido.criado`).
4. O serviço da cozinha consome esse evento e começa o preparo.
5. Quando finaliza, publica outro evento (`pedido.finalizado`).
6. O serviço de usuário consome esse evento e atualiza o histórico.

---

## 🎯 Objetivos do Trabalho

* Mostrar comunicação assíncrona real entre serviços.
* Evitar acoplamento direto entre eles.
* Aplicar na prática o uso do RabbitMQ.
* Atender os requisitos do trabalho (produtor, consumidor, fluxo, etc.).

---

## ⚙️ Tecnologias

* **Backend:** Java + Spring Boot
* **Mensageria:** RabbitMQ
* **Infra:** Docker & Eureka Server
* **Banco de Dados:** PostgreSQL
* **Frontend:** Angular

---

## 🚀 Como Rodar o Ambiente Completo

### 1. Clonando o projeto completo

```bash
git clone https://github.com/machadobatistacosta/Projeto-Sistemas-distribuidos.git
```

### 2. Subindo a Infraestrutura (Docker)

Toda a infraestrutura (Databases, RabbitMQ, Eureka) e os microserviços Java estão dockerizados. Na raiz do projeto, execute:

```bash
docker-compose up -d --build
```

### 3. Rodando o Serviço de Usuários (.NET)

O `Usuario-Service` é desenvolvido em .NET e deve ser executado manualmente para testes locais:

```bash
cd Usuario-Service
dotnet run
```

### 4. Rodando o Frontend (Angular)

```bash
cd Frontend
npm install
npm start
```

---

## 🔗 Endpoints Úteis

* **Frontend:** `http://localhost:4200`
* **Eureka Dashboard:** `http://localhost:8761`
* **RabbitMQ Management:** `http://localhost:15672` (guest/guest)
* **Produto Service:** `http://localhost:8081`
* **Pedido Service:** `http://localhost:8090`
* **Usuario Service:** `http://localhost:7201`

---

## 🛠️ Novas Funcionalidades (Recentes)

* **Página de Registro:** Agora é possível criar novos usuários diretamente pelo front.
* **Acompanhamento de Pedido:** Tela de tracking real-time para ver o status do pedido (Pago -> Preparando -> Pronto).
* **CORS Habilitado:** Todos os serviços agora aceitam requisições do frontend local.
* **Auto-Queue Creation:** O RabbitMQ cria as filas necessárias (`cozinha-queue`, `pedido-queue`, etc.) automaticamente na subida dos serviços.

