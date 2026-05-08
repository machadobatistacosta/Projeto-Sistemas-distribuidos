# Relatório do Trabalho Prático - Mensageria

**Instituição:** UNIVERSIDADE REGIONAL DE BLUMENAU - FURB  
**Centro:** CENTRO DE CIÊNCIAS EXATAS E NATURAIS - DEPARTAMENTO DE SISTEMAS E COMPUTAÇÃO  
**Disciplina:** Sistemas Distribuídos  
**Professor:** Gabriel Castellani  
**Data:** 08/05/2026  
**Aluno:** Caike Machado Batista Costa  

---

## 1. Descrição do Cenário

### Contextualização do ambiente

O sistema avaliado é o **MicroChefs**, uma plataforma de delivery e gestão de restaurantes baseada em microsserviços. A solução é composta por serviços independentes e conteinerizados com Docker, incluindo:

- **Frontend Angular:** interface usada pelo cliente para cadastro, login, consulta de cardápio, criação de pedidos e acompanhamento de status.
- **Usuario-Service (.NET):** responsável por autenticação, cadastro de usuários e histórico de pedidos concluídos.
- **Produto-Service (Spring Boot):** responsável pelo catálogo de produtos e pela publicação de atualizações de status ao frontend via WebSocket.
- **Pedido-Service (Spring Boot):** responsável pela criação, persistência e atualização dos pedidos.
- **Cozinha-Service (Spring Boot):** consumidor dos pedidos pagos e produtor das atualizações de preparo.
- **DLQ-Support (Spring Boot):** consumidor da fila de mensagens com falha, registrando e apoiando o tratamento de erros.
- **RabbitMQ:** broker de mensagens usado para desacoplar os serviços.

O ambiente de execução é local, com Docker Compose, bancos PostgreSQL/MySQL e rede Docker privada (`private_network`). O RabbitMQ roda localmente no container `rabbitmq_broker`, com conexão interna em `rabbitmq:5672` e painel de administração em `localhost:15672`.

### Justificativa da comunicação assíncrona

Em um sistema de restaurante ou delivery, há picos de demanda em horários como almoço e jantar. Se o serviço de pedidos dependesse de uma chamada síncrona direta para a cozinha, qualquer lentidão ou indisponibilidade da cozinha poderia bloquear a criação e atualização de pedidos.

Com mensageria assíncrona, o **Pedido-Service** confirma a atualização do pedido no banco e publica eventos no RabbitMQ. A **Cozinha-Service** consome esses eventos no seu próprio ritmo e devolve novas mensagens com o andamento do preparo. Assim, o sistema continua responsivo mesmo quando o processamento da cozinha demora ou quando ocorrem falhas temporárias.

---

## 2. Arquitetura da Solução

### Produtores e consumidores

- **Pedido-Service**
  - Produtor: publica pedido pago em `pedido-key.pago` e publica atualizações gerais em `pedido-key.status`.
  - Consumidor: consome atualizações da cozinha em `pedido-key.update`.

- **Cozinha-Service**
  - Consumidor: recebe pedidos pagos pela `cozinha-queue`.
  - Produtor: publica `EM_PREPARO` e `PRONTO` para o Pedido-Service usando `pedido-key.update`.

- **Produto-Service**
  - Consumidor: recebe eventos de status pela `front-queue`.
  - Saída: envia atualizações em tempo real ao frontend via WebSocket no tópico `/topic/pedido-status`.

- **Usuario-Service**
  - Consumidor: recebe eventos de status pela `user-queue`.
  - Função: salva histórico do pedido quando o status final é `PRONTO`.

- **DLQ-Support**
  - Consumidor: recebe mensagens problemáticas pela `dead-letter-queue`.
  - Função: auditar, consultar e tratar mensagens rejeitadas ou com erro de processamento.

### Diagrama 1 - Arquitetura geral

![Diagrama 1 - Arquitetura geral](docs/diagramas/diagrama_1_arquitetura_geral.png)


### Fluxo de mensagens

1. O cliente cria um pedido no frontend.
2. O Pedido-Service grava o pedido no banco com status inicial `AGUARDANDO_PAGAMENTO`.
3. O cliente simula o pagamento pelo frontend.
4. O Pedido-Service altera o status para `PAGO`.
5. O Pedido-Service publica um evento no RabbitMQ com routing key `pedido-key.pago`.
6. A Cozinha-Service consome a mensagem pela `cozinha-queue`.
7. A Cozinha-Service publica atualizações de preparo (`EM_PREPARO` e `PRONTO`) com routing key `pedido-key.update`.
8. O Pedido-Service consome essas atualizações pela `pedido-queue`, sincroniza o status no banco e publica eventos gerais com routing key `pedido-key.status`.
9. O Produto-Service consome pela `front-queue` e repassa ao frontend via WebSocket.
10. O Usuario-Service consome pela `user-queue` e registra histórico quando o pedido fica `PRONTO`.

### Escalabilidade, confiabilidade e tolerância a falhas

- **Escalabilidade:** consumidores podem ser replicados horizontalmente. Por exemplo, múltiplas instâncias da Cozinha-Service podem consumir da `cozinha-queue`, distribuindo a carga de preparo.
- **Confiabilidade:** as filas são duráveis (`durable = true`), reduzindo risco de perda em reinicializações do broker.
- **Desacoplamento:** o Pedido-Service não precisa aguardar a cozinha terminar o preparo para responder ao usuário.
- **Tolerância a falhas:** falhas de processamento podem ser encaminhadas para a `dead-letter-queue`, onde o DLQ-Support registra e permite auditoria/tratamento.
- **Resiliência:** consumidores possuem tratamento de exceções e retentativas controladas quando há falha temporária de infraestrutura.

---

## 3. Configuração do RabbitMQ

### Exchanges

- `pedido-exchange`
  - Tipo: `TopicExchange`
  - Função: roteia eventos normais do fluxo de pedidos.

- `dead-letter-exchange`
  - Tipo: `TopicExchange`
  - Função: roteia mensagens com falha para a fila de tratamento.

### Filas e bindings

| Fila | Routing key | Consumidor | Finalidade |
| --- | --- | --- | --- |
| `cozinha-queue` | `pedido-key.pago` | Cozinha-Service | Receber pedidos pagos para preparo |
| `pedido-queue` | `pedido-key.update` | Pedido-Service | Receber atualizações da cozinha |
| `user-queue` | `pedido-key.status` | Usuario-Service | Registrar histórico de pedidos |
| `front-queue` | `pedido-key.status` | Produto-Service | Enviar status ao frontend via WebSocket |
| `dead-letter-queue` | `dead-message` | DLQ-Support | Auditar mensagens com falha |

### Diagrama 2 - Topologia do RabbitMQ

![Diagrama 2 - Topologia do RabbitMQ](docs/diagramas/diagrama_2_topologia_rabbitmq.png)


### Parâmetros locais

No `docker-compose.yaml`, os serviços usam o RabbitMQ local:

```yaml
SPRING_RABBITMQ_ADDRESSES=rabbitmq:5672
SPRING_RABBITMQ_USERNAME=admin
SPRING_RABBITMQ_PASSWORD=admin
RabbitMQ__Uri=amqp://admin:admin@rabbitmq:5672/
```

### Segurança

- A comunicação entre microsserviços e RabbitMQ acontece dentro da rede Docker privada `private_network`.
- O broker exige autenticação por usuário e senha (`admin/admin`) no ambiente local.
- Para produção, recomenda-se:
  - uso de TLS/AMQPS;
  - criação de virtual hosts por domínio ou ambiente;
  - usuários com permissões mínimas por serviço;
  - troca das credenciais padrão;
  - bloqueio do painel de administração para redes externas.

---

## 4. Exemplos de Uso

### Caso de uso: pedido pago e processado pela cozinha

**Entrada:** o cliente confirma/simula o pagamento no frontend. O frontend envia uma requisição REST ao Pedido-Service solicitando alteração do status para `PAGO`.

**Processamento esperado:**

1. O Pedido-Service valida a transição de status.
2. O Pedido-Service grava `PAGO` no banco.
3. O Pedido-Service publica o pedido no RabbitMQ usando `pedido-key.pago`.
4. A Cozinha-Service consome a mensagem e inicia o preparo.
5. A Cozinha-Service publica `EM_PREPARO` usando `pedido-key.update`.
6. O Pedido-Service consome a atualização, grava no banco e publica `pedido-key.status`.
7. O Produto-Service consome `pedido-key.status` e envia atualização ao frontend por WebSocket.
8. A Cozinha-Service publica `PRONTO` usando `pedido-key.update`.
9. O Pedido-Service sincroniza o status final e publica novamente `pedido-key.status`.
10. O Usuario-Service registra o histórico do pedido concluído.

### Diagrama 3 - Fluxo de pedido

![Diagrama 3 - Fluxo de pedido](docs/diagramas/diagrama_3_fluxo_pedido.png)


**Saídas esperadas:**

- O pedido aparece como `PAGO`, depois `EM_PREPARO` e finalmente `PRONTO`.
- O frontend recebe atualizações em tempo real.
- O banco de pedidos fica sincronizado com o andamento da cozinha.
- O Usuario-Service mantém o histórico do pedido concluído.
- Em caso de falha, a mensagem pode ser enviada para a DLQ para análise posterior.

---

## 5. Considerações Técnicas

### Tecnologias utilizadas

- **Mensageria:** RabbitMQ 3 Management em container Docker.
- **Backend Java:** Spring Boot, Spring AMQP, Spring Web, Spring Data JPA.
- **Backend .NET:** ASP.NET Core para Usuario-Service.
- **Frontend:** Angular.
- **Bancos de dados:** PostgreSQL para pedidos, produtos e mensagens de DLQ; MySQL para usuários.
- **Infraestrutura:** Docker Compose, rede Docker privada e Eureka Server.

### Padrão de mensagem

As mensagens são trafegadas em **JSON**, o que permite interoperabilidade entre serviços Java e .NET. Exemplos simplificados:

```json
{
  "id": 8,
  "usuarioId": 7,
  "statusPedido": "PRONTO"
}
```

```json
{
  "id": 8,
  "usuarioId": 7,
  "dataDoPedido": "2026-05-08",
  "itens": [
    {
      "idProduto": 1,
      "quantidadeProduto": 1
    }
  ]
}
```

### Boas práticas adotadas

- Declaração automática de exchanges, filas e bindings na inicialização dos serviços.
- Uso de filas duráveis.
- Separação de responsabilidades por routing key.
- Uso de TopicExchange para permitir expansão futura com padrões como `pedido-key.*`.
- Persistência do estado final no banco, evitando depender apenas de mensagens transitórias.
- DLQ para auditoria e tratamento de falhas.
- Comunicação local isolada via rede Docker.

### Validação prática

O fluxo foi validado localmente com Docker Compose:

- Cadastro de usuário retornando `201 Created`.
- Login retornando token JWT.
- Criação de pedido retornando `201 Created`.
- Pagamento simulado retornando `200 OK`.
- Pedido processado pela Cozinha-Service até `PRONTO`.
- Produto-Service publicando os status no WebSocket.
- Usuario-Service registrando histórico quando o pedido chega em `PRONTO`.

---

## Conclusão

A solução implementada demonstra o uso de RabbitMQ como mecanismo de integração assíncrona entre microsserviços. O uso de exchanges, filas, bindings e DLQ reduz o acoplamento entre os serviços, melhora a resiliência em horários de pico e permite que cada componente processe mensagens no seu próprio ritmo.

No contexto do MicroChefs, a mensageria é adequada porque o fluxo de pedido envolve etapas independentes, como pagamento, preparo, atualização de status, comunicação com frontend e registro de histórico. O RabbitMQ atua como o elemento central de coordenação assíncrona, garantindo que a falha ou lentidão de um serviço não bloqueie todo o sistema.
