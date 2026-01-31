# Sistema de ACERVO MUSICAL - SEPLAG MT

Projeto prático desenvolvido para o processo seletivo do Edital 001/2026/SEPLAG-MT - Perfil Engenheiro da Computação (Nível Sênior).
O sistema é uma API RESTful desenvolvida em Java com Spring Boot, focada em alta performance, segurança e escalabilidade.

**Candidato:** Caio Augusto Freitas Diogo Tavares  
**Identificador:** 039795392  
**N de inscrição:** 16449

---

# 🎵 Acervo SEPLAG API

Backend robusto para gerenciamento de catálogo musical, integrando armazenamento de objetos (MinIO), sincronização com legado e notificações em tempo real via WebSockets.

---

## 🚀 Quick Start (Rodando em 5 minutos)

A maneira mais rápida de subir o ambiente completo (API + Banco + Object Storage) é via Docker.

### Pré-requisitos
- Docker & Docker Compose
- Portas 8080, 5432 e 9000 livres

### Instalação

**1. Clone o repositório:**
```bash
git clone https://github.com/caiotvrs/caioaugustofreitasdiogotavares039795.git
cd caioaugustofreitasdiogotavares039795
```

**2. Configure o ambiente (Opcional):**

O arquivo `docker-compose.yml` já vem com defaults funcionais.

> **Nota**: Se estiver rodando localmente para testar o upload de imagens, certifique-se que a variável `MINIO_ENDPOINT` aponte para o seu IP local (localhost ou 127.0.0.1 dará erro de loopback e usar o endereço da rede docker impedirá de visualizar a imagem).

**3. Suba os containers:**
```bash
docker compose up -d --build
```

**Pronto!** A documentação interativa estará disponível em:

👉 **http://localhost:8080/swagger-ui.html**

---

## 🛠 Stack Tecnológica

O projeto foi construído sobre uma arquitetura Layered utilizando o ecossistema Spring:

- **Core**: Java 17, Spring Boot 3, Spring Data JPA
- **Security**: Spring Security com JWT (Access + Refresh Tokens) e Rate Limiting customizado
- **Data & Storage**: PostgreSQL (com Flyway para migrações) e MinIO (compatível com S3) para capas de álbuns
- **Real-time**: Spring WebSocket (STOMP) para notificações de novos lançamentos
- **Tests**: JUnit 5, Mockito e Testcontainers

---

## 🏛 Arquitetura e Design

### Estrutura de Pacotes

A aplicação segue uma separação clara de responsabilidades (SoC), isolando regras de negócio da camada de apresentação e persistência.

```
com.acervo.api
├── controller/   # Camada HTTP (REST)
├── service/      # Regras de Negócio e Orquestração
├── repository/   # Acesso a dados (Spring Data JPA)
├── domain/       # Entidades Persistentes
├── dto/          # Contratos de API (Request/Response isolados)
├── client/       # Integrações (Feign/RestTemplate para Legado)
└── security/     # Configurações JWT e Filtros
```

### Decisões Técnicas Chave

#### PostgreSQL vs MySQL
Optamos pelo Postgres pela sua robustez com integridade relacional complexa e melhor suporte a tipos de dados avançados, prevendo futuras necessidades de queries analíticas sobre o acervo.

#### Estratégia de Autenticação (Híbrida)
- **GET Público**: Decidimos manter as rotas de leitura (GET /albuns) abertas para facilitar a indexação (SEO) e permitir que frontends exibam o catálogo sem barreira de login.
- **Escrita Protegida**: Operações de mutação exigem JWT. Implementamos Refresh Tokens para garantir que a sessão do usuário permaneça segura sem exigir logins frequentes.

#### WebSockets com STOMP
Para evitar polling desnecessário no frontend, utilizamos WebSockets. O protocolo STOMP foi escolhido por fornecer semântica de pub/sub (tópicos) "out-of-the-box", facilitando a inscrição de clientes em eventos como `/topic/albums`.

---

## 🔌 API Reference

A documentação completa dos endpoints (contratos, status codes e exemplos) é gerada automaticamente via OpenAPI/Swagger.

### Endpoints de Destaque

| Método | Endpoint | Descrição | Auth? |
|--------|----------|-----------|-------|
| POST | `/v1/auth/login` | Gera Access e Refresh Tokens | ❌ |
| GET | `/v1/albuns` | Lista álbuns (Paginação, Filtros e Sort Dinâmico) | ❌ |
| POST | `/v1/albuns` | Cria novo álbum e notifica via WebSocket | ✅ |
| POST | `/v1/albuns/{id}/capa` | Upload de imagem para o MinIO | ✅ |
| POST | `/v1/regionais/sync` | Força sincronização com API legada | ✅ |

### Exemplo de Payload WebSocket

Ao criar um álbum, o seguinte JSON é enviado no tópico `/topic/albums`:

```json
{
  "albumId": 105,
  "title": "Meteora",
  "artistNames": ["Linkin Park"],
  "message": "Novo álbum disponível no catálogo!",
  "timestamp": "2026-01-31T15:00:00"
}
```

---

## 🧪 Testes e Qualidade

O projeto possui uma suíte de testes cobrindo as camadas críticas (Service, Security e Utils).

Para executar os testes unitários e de integração:

```bash
# Via Maven Wrapper (Linux/Mac)
./mvnw clean test

# Via Docker (Ambiente isolado)
docker compose run --rm test-runner
```

**Cobertura atual**: Autenticação, Fluxo de CRUD de Álbuns/Artistas e Lógica de Sincronização.

---

## 🐳 Serviços Docker

| Serviço | Porta Interna | Porta Exposta | Credenciais Default |
|---------|---------------|---------------|---------------------|
| API | 8080 | 8080 | - |
| Postgres | 5432 | 5432 | User/Pass definidos docker-compose |
| MinIO API | 9000 | 9000 | minioadmin / minioadmin |
| MinIO Console | 9001 | 9001 | minioadmin / minioadmin |

---

## 🔧 Troubleshooting

### Erro de CORS
**Solução**: Verifique se a origem do seu frontend está na variavel `CORS_ALLOWED_ORIGINS` do `docker-compose.yml`.

### Problema: Rate limit muito restritivo
**Solução**: Ajuste `RATE_LIMIT_REQUESTS_PER_MINUTE` no `docker-compose.yml`.

### Problema: JWT expirou muito rápido
**Solução**: Aumente `JWT_EXPIRATION_MS` (em milissegundos) no `docker-compose.yml`.

### Falha no upload de imagens
**Solução**: Verifique se a variavel `MINIO_ENDPOINT` no `docker-compose.yml` está configurada corretamente com o IP do seu computador. 
---

**Desenvolvido com ☕ e 🎵 **
