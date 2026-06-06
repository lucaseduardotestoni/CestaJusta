# Prioridades — CestaJusta V2

Documento de roadmap / status do backend. Atualizado em 2026-04-24.

---

## V1 — núcleo funcional (ENTREGUE)

A V1 entrega o valor principal do sistema: **consultar, colaborar e comparar preços da cesta básica**.

### Módulos concluídos

**Módulo Usuário**
- ✅ Cadastro (rota pública `POST /usuarios/cadastro`, bloqueia autocadastro como ADMIN)
- ✅ Login com JWT HS256 (rota pública `POST /login`)
- ✅ Senha com BCrypt
- ✅ Autorização por role: `ROLE_ADMIN`, `ROLE_CONSUMIDOR`, `ROLE_COMERCIANTE` (carregadas do banco em cada request)
- ✅ Check de usuário inativo durante autenticação

**Módulo Produto**
- ✅ CRUD completo (cadastrar, listar, buscar por id, buscar por nome, inativar, ativar)
- ✅ Cadastro/inativação/ativação restritos a ADMIN via `@PreAuthorize("hasRole('ADMIN')")`
- ✅ Soft delete via flag `ativo`
- ✅ Validação de código de barras único (BusinessException)
- ✅ Vínculo obrigatório com categoria

**Módulo Mercado**
- ✅ CRUD completo (cadastrar, listar, buscar por id, buscar por nome, inativar, ativar)
- ✅ Cadastro/inativação/ativação restritos a ADMIN
- ✅ CNPJ único como chave natural

**Módulo Preço (coleta colaborativa)**
- ✅ `POST /precos/cadastro` — qualquer usuário autenticado
- ✅ Autor identificado via SecurityContext (email do token)
- ✅ Validações: valor > 0, `dataColeta` não futura, produto e mercado ativos
- ✅ Listagens por produto e por mercado (ordenadas por `dataColeta` desc)

**Módulo Comparação**
- ✅ `GET /comparacoes/produto/{id}` — agrega menor, maior, média, total de mercados
- ✅ Dedup por mercado: pega o preço mais recente de cada mercado
- ✅ Lista final ordenada do menor valor pro maior

**Ownership Comerciante ↔ Mercado**
- ✅ Entidade `MercadoComerciante` (N:N) com audit de quem vinculou
- ✅ ADMIN associa/desassocia via `POST/DELETE /mercados/{mercadoId}/comerciantes/{comercianteId}`
- ✅ Listagens: donos de um mercado e mercados de um comerciante
- ✅ **Regra automática:** quando um COMERCIANTE dono do mercado cadastra preço, o status inicial é `CONFIRMADO` (preço oficial). Preços colaborativos continuam entrando como `PENDENTE`.

**Infraestrutura**
- ✅ Tratamento global de exceções (`ControllerExceptionHandler`) com payload padronizado `StandardError` — mapeia BusinessException (400), ResourceNotFoundException (404), validação (400 com `errors[]`), AccessDenied (403), tipo inválido (400), fallback (500)
- ✅ Schema gerenciado por JPA (`ddl-auto=update`); seeds em `data.sql` com idempotência total (`ON CONFLICT` + `WHERE NOT EXISTS`)
- ✅ Collection Postman completa para testes

### Banco de dados da V1

Tabelas consolidadas:
- `usuarios`
- `categorias` (com seed de 6 categorias)
- `produtos`
- `mercados`
- `mercado_comerciantes` (N:N com audit)
- `precos`

---

## V2 — evolução colaborativa + localização (PLANEJADA)

Regras já definidas em reuniões de design; implementação ainda não iniciada.

### Módulo Denúncia (regras fechadas)

Moderação colaborativa com override administrativo:

- Qualquer usuário autenticado pode denunciar um `Preco`
- Rate limit: mesmo usuário não pode abrir nova denúncia sobre o mesmo preço em menos de **3 dias**
- `StatusDenuncia`: `PENDENTE`, `APROVADA`, `REJEITADA`, `CANCELADA`
- Usuário pode cancelar a própria denúncia enquanto `PENDENTE`

**Votação colaborativa:**
- Entidade nova `VotoDenuncia` com `TipoVoto` (`CONFIRMA` ou `REJEITA`)
- Unique constraint em `(denuncia_id, usuario_id)` — um voto por usuário por denúncia
- **3 confirmações** → denúncia vira `APROVADA` automaticamente → preço denunciado vira `StatusPreco.REJEITADO`
- **3 rejeições** → denúncia vira `REJEITADA` automaticamente
- Votante pode retirar voto enquanto `PENDENTE`
- **Quem não pode votar:** o próprio denunciante E qualquer dono do mercado do preço denunciado (conflito de interesse)

**Override e expiração:**
- ADMIN pode forçar aprovação/rejeição a qualquer momento
- Denúncia sem engajamento em 30 dias → `REJEITADA` automaticamente (job agendado)

**Campos extras na `Denuncia`:** `dataResolucao`, `resolvidoPor` (enum: `SISTEMA`, `ADMIN`, `PROPRIO_DENUNCIANTE`)

### Módulo GPS / Localização (regras fechadas)

Objetivo: permitir busca e comparação de preços por proximidade ao usuário.

**Arquitetura escolhida:**
- Mercado ganha campos `latitude`, `longitude`, `endereco`, `cep` (nullable — mercados existentes não quebram)
- Frontend (mobile/web) captura GPS do dispositivo e envia `lat/lon` em cada request como query param (stateless, sem persistir localização do usuário)
- Backend faz proxy do geocoding via **Nominatim (OpenStreetMap)** — motivos: gratuito, sem cartão, sem dependência proprietária
- `GeocodingService` com interface (impl `NominatimGeocodingService`) pra facilitar futura migração (Mapbox/Google)
- Cache local com Caffeine (TTL 24h por endereço) pra respeitar rate limit do Nominatim

**Fases de implementação:**
- **Fase A:** GeocodingService + endpoints `/geocoding/busca` e `/geocoding/reverso`
- **Fase B:** Adicionar lat/lon em Mercado + atualizar DTOs
- **Fase C:** `GET /mercados/proximos?lat=X&lon=Y&raioKm=10` — Haversine em SQL nativo
- **Fase D:** `GET /comparacoes/produto/{id}?lat=X&lon=Y&raioKm=10` — filtro espacial opcional

### Outros itens da V2

- Histórico de preços com agregação (média por semana/mês)
- Filtros avançados em listagens (categoria, marca, status, faixa de valor, data)
- Status automático `DESATUALIZADO` para preços com mais de 30 dias de coleta (job agendado)
- Escaneamento de código de barras (frontend — backend já aceita código de barras na busca)
- Painel administrativo (endpoints de analytics: ranking de mercados, preços mais volumosos, usuários mais ativos)
- Notificações push via FCM (integração separada do backend core)

---

## V3 — escala e inteligência (FUTURO)

- Cache Redis pra queries pesadas
- Migração de Haversine puro para **PostGIS** (quando a base de mercados crescer)
- Self-host do Nominatim ou migração para Mapbox/Google (qualidade/rate limit)
- Sistema de reputação/confiabilidade por usuário (valor do voto pondera pela reputação)
- Mapa interativo com clustering de mercados
- Análise de dados: tendências de preço, alertas de alta, comparação regional
- Internacionalização (multilíngua, multi-moeda)

---

## Backlog atual (user stories)

**V1 (entregues):**
- US01 Cadastro de usuário ✅
- US02 Login com JWT ✅
- US03 Buscar produto por nome ✅
- US04 Registrar preço colaborativamente ✅
- US05 Visualizar preços por mercado ✅
- US06 Visualizar menor, maior e média de preço ✅
- US07 Vincular comerciante a mercado (ADMIN) ✅
- US08 Preço oficial do dono entra CONFIRMADO ✅

**V2 (backlog ordenado por prioridade):**
- US09 Denunciar preço abusivo
- US10 Votar em denúncia (confirmar/rejeitar)
- US11 ADMIN fazer override de denúncia
- US12 Job de expiração de denúncia sem engajamento
- US13 Backend proxy de geocoding (Nominatim)
- US14 Cadastrar mercado com lat/lon
- US15 Buscar mercados próximos
- US16 Comparar preços com filtro de raio
- US17 Filtros avançados de produto/preço
- US18 Histórico de preço agregado

**V3:**
- US19+ Cache, PostGIS, reputação, mapa, analytics, notificações push

---

## Segurança e hardening (backlog técnico)

Itens de segurança levantados em revisão do código. Não são features de produto, mas dívidas técnicas com impacto em confidencialidade, integridade e compliance. Numerados `SEC` para distinguir das user stories funcionais.

### Prioridade alta (cortar superfície exploitable)

- **SEC01 — Eliminar user enumeration no cadastro de usuário**
  - Hoje: `POST /usuarios/cadastro` com email duplicado retorna `"Já existe um usuário cadastrado com este e-mail."`, confirmando a existência da conta
  - Ação: retornar mensagem genérica tipo `"Não foi possível concluir o cadastro"` (ou 202 aceito-sempre + fluxo de confirmação por email quando houver infra)
  - Esforço: baixo

- **SEC02 — Rate limit + bloqueio de conta em `/login`**
  - Hoje: nenhum limite de tentativas — brute-force é trivial contra senhas fracas
  - Ação mínima: campo `tentativasLoginFalhas` + `bloqueadoAte` em `Usuario`; após N tentativas, bloquear por X minutos
  - Ação completa: rate limit por IP também (bucket4j ou similar)
  - Esforço: médio

- **SEC03 — Política de senha forte**
  - Hoje: `@Size(min = 6)` permite `"senha123"`, `"123456"`, etc. (senhas que aparecem em toda wordlist)
  - Ação: `@Pattern` exigindo mínimo 8 caracteres com pelo menos 1 maiúscula e 1 número. Opcional: biblioteca `zxcvbn-java` para score de força.
  - Esforço: baixo

- **SEC06 — Remover email de DTOs de listagem de comerciantes**
  - Hoje: `GET /mercados/{id}/comerciantes` e `GET /comerciantes/{id}/mercados` retornam `comercianteEmail` — qualquer usuário autenticado enumera emails de comerciantes (PII, LGPD)
  - Ação: remover campo `comercianteEmail` de `MercadoComercianteResponseDTO`. Se email for realmente necessário em algum fluxo, criar endpoint separado restrito a ADMIN.
  - Esforço: baixo (quick win de LGPD)

### Prioridade média (fortalecer após features V2)

- **SEC05 — Refresh token + access token curto + revogação**
  - Hoje: JWT válido por 24h sem forma de invalidar (logout, mudança de senha, conta comprometida não revogam o token)
  - Ação: access token de 15min + refresh token de 7 dias com rotação. Ou campo `tokenVersion` em Usuario que incrementa em eventos de invalidação.
  - Esforço: alto (toca autenticação inteira)

- **SEC07 — Rate limit + detecção de anomalia em `/precos/cadastro`**
  - Hoje: usuário autenticado pode cadastrar N preços absurdos e envenenar a comparação enquanto denúncias não atingem 3 votos
  - Ação: máx 20 preços/dia/usuário. Preços com valor > 3x a mediana do produto entram com flag interna "suspeito" (já sobe como PENDENTE normal, mas fica marcado para priorização na denúncia).
  - Esforço: médio

- **SEC08 — Audit log de ações administrativas**
  - Hoje: sem rastro de quem cadastrou/inativou produto, quem vinculou comerciante, quem fez override de denúncia (exceto `MercadoComerciante.vinculadoPor` que já existe)
  - Ação: tabela `audit_log` com `usuarioId`, `acao`, `recursoTipo`, `recursoId`, `timestamp`, `ip`. Interceptor ou AOP em endpoints ADMIN popula automaticamente.
  - Esforço: médio

### Prioridade baixa (polish)

- **SEC13 — Validação de formato de CNPJ**
  - Hoje: `@NotBlank + @Size(max=18)` aceita strings como `"abc123"`
  - Ação: `@CNPJ` da biblioteca Caelum Stella (`br.com.caelum.stella:caelum-stella-bean-validation`) ou `@Pattern` com regex de CNPJ formatado/limpo
  - Esforço: baixo

### Notas

- **SEC01 + SEC02 + SEC03 devem ser feitos em conjunto** — individualmente cada um é mitigável, combinados transformam `/login` numa fortaleza razoável.
- Itens não listados aqui (JWT secret default, credenciais do DB em properties, logs DEBUG em prod, XSS) são questões de **configuração de ambiente** e não de código — serão tratados no `application-prod.properties` e no deploy, fora do escopo do backlog de features.

---

## Próximo passo imediato

**Implementar o módulo de Denúncia** (US09–US12), seguindo as regras já fechadas. Tudo que é pré-requisito já está pronto:
- Roles funcionando (necessário para override ADMIN)
- ExceptionHandler global (pra retornar 400/404/403 limpos)
- Ownership Comerciante↔Mercado (necessário pra regra de exclusão de voto dos donos)

Ordem sugerida dentro do módulo:
1. Entidade `VotoDenuncia` + enum `TipoVoto`
2. Ajuste na `Denuncia` (campos `dataResolucao`, `resolvidoPor`, enum `OrigemResolucao`)
3. Endpoint criar denúncia (com regra de 3 dias)
4. Endpoints de voto (criar, retirar)
5. Lógica de transição automática (3 votos → APROVADA/REJEITADA)
6. Endpoint ADMIN de override
7. Job agendado de expiração em 30 dias

Após denúncia fechada, seguir para o módulo de GPS (Fase A: geocoding) que começa a transformar o app em "comparador local".