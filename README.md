# UOL Bank API

API REST de um banco digital simplificado: **gestão de contas**, **transferência de fundos** entre contas, **notificação** ao cliente e **consulta de movimentações** (extrato), com foco em **consistência e resiliência sob alta concorrência**.

---

## Stack

| Item | Tecnologia |
|------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 (Web MVC, Data JPA, Validation, Actuator) |
| Banco de dados | H2 (em memória) |
| Documentação | SpringDoc OpenAPI / Swagger UI |
| Build | Maven (com wrapper `mvnw`) |
| Testes | JUnit 5, Mockito, AssertJ |
| Utilitário | Lombok |

---

## Como rodar

**Pré-requisitos:** JDK 21 instalado. O Maven não precisa ser instalado — o projeto usa o wrapper (`mvnw`).

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Recursos disponíveis ao subir

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs |
| Console H2 | http://localhost:8080/h2-console |
| Health (Actuator) | http://localhost:8080/actuator/health |

**Console H2** — JDBC URL: `jdbc:h2:mem:uolbank` · usuário: `sa` · senha: *(vazia)*

### Base de clientes pré-carregada

Ao iniciar, três contas são criadas automaticamente:

| ID | Nome | Saldo inicial |
|----|------|---------------|
| 1 | Alice | 1000.00 |
| 2 | Bob | 500.00 |
| 3 | Carol | 0.00 |

---

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/accounts` | Cadastra uma conta (nome + saldo inicial) |
| `GET` | `/api/accounts` | Lista todas as contas |
| `GET` | `/api/accounts/{id}` | Consulta uma conta |
| `POST` | `/api/transfers` | Transfere fundos entre duas contas |
| `GET` | `/api/accounts/{accountId}/movements` | Extrato (movimentações) da conta |

### Exemplos

```bash
# Criar conta
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"name":"Dave","initialBalance":250.00}'

# Transferir R$ 150 da conta 1 (Alice) para a 2 (Bob)
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{"sourceAccountId":1,"targetAccountId":2,"amount":150.00}'

# Extrato da conta 1
curl http://localhost:8080/api/accounts/1/movements
```

Após uma transferência concluída, a notificação aos clientes (origem e destino) é
registrada no log da aplicação (adapter mock):

```
[NOTIFICACAO] Conta 1 (Alice): Voce transferiu R$ 150.00 para Bob.
[NOTIFICACAO] Conta 2 (Bob): Voce recebeu R$ 150.00 de Alice.
```

---

## Decisões de design e arquitetura

### Arquitetura hexagonal (Ports & Adapters) modularizada

O código é organizado por **bounded contexts** (módulos de negócio), cada um com suas
camadas hexagonais isoladas. Em vez de pacotes técnicos (`controllers`, `services`,
`repositories`), agrupamos por **domínio**:

```
com.rafaelfarias.uol_bank_api
├── account/        Gestão de contas
├── transfer/       Transferência de fundos
├── notification/   Notificações
├── movement/       Consulta de movimentações (extrato)
└── shared/         Configuração (OpenAPI), tratamento de erros, exceções de domínio
```

Cada módulo segue a estrutura:

```
<modulo>/
├── domain/                 Modelo de domínio puro (regras de negócio, sem framework)
├── application/
│   ├── port/in/            Casos de uso (entrada) — o que o módulo oferece
│   ├── port/out/           Portas de saída — o que o módulo precisa do mundo externo
│   └── service/            Implementação dos casos de uso (orquestração)
└── adapter/
    ├── in/web/  (ou event/)  Adapters de entrada: REST controllers, listeners
    └── out/persistence/      Adapters de saída: JPA, publicação de eventos
```

**Regras de dependência:**
- O **domínio** não depende de Spring/JPA — é Java puro e testável isoladamente.
- A **aplicação** depende apenas de **portas** (interfaces), nunca de adapters concretos.
- A comunicação **entre módulos acontece somente por portas**. Um módulo nunca acessa
  a entidade JPA de outro — ex.: `transfer` usa o domínio `Account` via a porta
  `UpdateAccountUseCase` exposta por `account`, sem conhecer `AccountJpaEntity`.
- Direção das dependências entre módulos (downstream → upstream):
  `transfer → account`, `notification → {transfer, account}`, `movement → {transfer, account}`.

> Optou-se por **módulo Maven único** com fronteiras por pacote (em vez de multi-módulo
> Maven), por ser mais simples de rodar e revisar mantendo o mesmo isolamento lógico.
> A separação por portas permite extrair módulos para artefatos próprios no futuro sem
> reescrever a lógica.

### Consistência sob concorrência (transferência)

Requisito central do desafio. A estratégia adotada:

- **Transação única** envolvendo débito da origem, crédito do destino e registro da
  transferência — tudo confirma junto (atômico) ou sofre rollback.
- **Lock pessimista de escrita** (`@Lock(PESSIMISTIC_WRITE)`) ao carregar as contas
  (`findByIdForUpdate`). Transferências concorrentes sobre a mesma conta são
  **serializadas**, evitando *lost updates* e saldo negativo.
- **Ordem de aquisição de locks por id crescente** — previne *deadlock* quando duas
  transferências invertidas (A→B e B→A) ocorrem em paralelo.
- Coberto por um teste de concorrência (`TransferConcurrencyTest`) que dispara
  **200 transferências em paralelo** (16 threads) e verifica que o saldo final é exato
  e o total do sistema é conservado.

> *Alternativa considerada:* lock otimista (`@Version` + retry). Optou-se pelo pessimista
> por ser mais simples e seguro no cenário de alta contenção sobre a mesma conta.

### Notificação após o commit

A notificação é disparada por **evento de domínio** consumido com
`@TransactionalEventListener(phase = AFTER_COMMIT)`. Assim, o cliente só é notificado
quando a transferência foi **efetivamente persistida** — uma transferência que falha
(ex.: saldo insuficiente) e sofre rollback **não** gera notificação. O `TransferService`
publica o evento por uma porta de saída (`TransferEventPublisher`), permanecendo livre
de dependência direta do mecanismo de eventos do Spring.

### Outras decisões

- **`BigDecimal`** para todos os valores monetários (precisão `19,2`) — nunca `double`.
- **Tratamento de erros centralizado** (`@RestControllerAdvice`) com payload padronizado
  (`ApiError`): validação → `400`, recurso inexistente → `404`, regra de negócio
  (saldo insuficiente, mesma conta) → `422`.
- **`open-in-view=false`** — desabilita o anti-padrão de manter a sessão JPA aberta na
  camada web.

---

## Testes

```bash
./mvnw test        # Linux/macOS
mvnw.cmd test      # Windows
```

São **36 testes** cobrindo as camadas de cada módulo:

- **Domínio** (puro, sem Spring) — regras de saldo, validações de transferência.
- **Serviços/casos de uso** — com Mockito (mock das portas).
- **Web** — `@WebMvcTest` (status HTTP, validação, mapeamento de erros).
- **Integração / fluxo** — `@SpringBootTest`: concorrência, notificação após commit
  (e ausência em rollback) e reflexo das transferências no extrato.

---

## Estrutura resumida

```
src/main/java/com/rafaelfarias/uol_bank_api/
├── UolBankApiApplication.java
├── account/      ( domain · application[port.in/out, service] · adapter[in.web, out.persistence] · config )
├── transfer/     ( domain · application[port.in/out, event, service] · adapter[in.web, out.persistence, out.event] )
├── notification/ ( domain · application[port.in/out, service] · adapter[in.event, out] )
├── movement/     ( domain · application[port.in, service] · adapter[in.web] )
└── shared/       ( config · exception · web )
```
