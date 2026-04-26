-- =====================================================================
-- pgcrypto: gera hashes BCrypt compatíveis com BCryptPasswordEncoder
-- =====================================================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =====================================================================
-- Categorias
-- =====================================================================
INSERT INTO categorias (nome, ativo, data_criacao) VALUES
    ('Alimentos', true, NOW()),
    ('Bebidas', true, NOW()),
    ('Higiene e Limpeza', true, NOW()),
    ('Frutas e Verduras', true, NOW()),
    ('Lácteos', true, NOW()),
    ('Carnes e Peixes', true, NOW())
ON CONFLICT (nome) DO NOTHING;

-- =====================================================================
-- Usuários (todas as senhas: senha123)
--   admin@cestajusta.com     → ADMIN
--   ana/bruno/carla          → CONSUMIDOR
--   joao/maria/pedro         → COMERCIANTE (donos dos mercados abaixo)
-- =====================================================================
INSERT INTO usuarios (nome, email, senha, tipo_usuario, ativo, data_criacao) VALUES
    ('Admin Sistema', 'admin@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'ADMIN', true, NOW()),
    ('Ana Souza', 'ana@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'CONSUMIDOR', true, NOW()),
    ('Bruno Lima', 'bruno@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'CONSUMIDOR', true, NOW()),
    ('Carla Rocha', 'carla@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'CONSUMIDOR', true, NOW()),
    ('João Koch', 'joao@koch.com', crypt('senha123', gen_salt('bf', 10)), 'COMERCIANTE', true, NOW()),
    ('Maria Angeloni', 'maria@angeloni.com', crypt('senha123', gen_salt('bf', 10)), 'COMERCIANTE', true, NOW()),
    ('Pedro Giassi', 'pedro@giassi.com', crypt('senha123', gen_salt('bf', 10)), 'COMERCIANTE', true, NOW())
ON CONFLICT (email) DO NOTHING;

-- =====================================================================
-- Mercados
-- =====================================================================
INSERT INTO mercados (nome_fantasia, cnpj, cidade, estado, ativo, data_criacao) VALUES
    ('Supermercado Koch',           '84.683.408/0001-03', 'Blumenau', 'SC', true, NOW()),
    ('Angeloni Blumenau',           '83.646.984/0001-12', 'Blumenau', 'SC', true, NOW()),
    ('Giassi Supermercados',        '79.084.053/0001-00', 'Blumenau', 'SC', true, NOW()),
    ('Mercado Econômico Gaspar',    '12.345.678/0001-01', 'Gaspar',   'SC', true, NOW()),
    ('Supermercado Popular Brusque','98.765.432/0001-02', 'Brusque',  'SC', true, NOW())
ON CONFLICT (cnpj) DO NOTHING;

-- =====================================================================
-- Produtos — 18 itens espalhados em 5 categorias
-- =====================================================================
INSERT INTO produtos (nome, codigo_barras, marca, unidade_medida, categoria_id, ativo) VALUES
    ('Arroz Branco 5kg',          '7891234567890', 'Tio João',    '5kg',   (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Feijão Preto 1kg',          '7891111111111', 'Camil',       '1kg',   (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Açúcar Cristal 1kg',        '7892222222222', 'União',       '1kg',   (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Café Torrado 500g',         '7893333333333', 'Pilão',       '500g',  (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Óleo de Soja 900ml',        '7894444444444', 'Liza',        '900ml', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Macarrão Espaguete 500g',   '7895555555555', 'Renata',      '500g',  (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Leite Integral 1L',         '7896666666666', 'Piracanjuba', '1L',    (SELECT id FROM categorias WHERE nome='Lácteos'), true),
    ('Manteiga sem Sal 200g',     '7897777777777', 'Aviação',     '200g',  (SELECT id FROM categorias WHERE nome='Lácteos'), true),
    ('Queijo Mussarela 500g',     '7898888888888', 'Tirolez',     '500g',  (SELECT id FROM categorias WHERE nome='Lácteos'), true),
    ('Refrigerante Cola 2L',      '7899999999999', 'Coca-Cola',   '2L',    (SELECT id FROM categorias WHERE nome='Bebidas'), true),
    ('Água Mineral 1,5L',         '7891010101010', 'Crystal',     '1,5L',  (SELECT id FROM categorias WHERE nome='Bebidas'), true),
    ('Suco de Laranja 1L',        '7891212121212', 'Del Valle',   '1L',    (SELECT id FROM categorias WHERE nome='Bebidas'), true),
    ('Banana Prata 1kg',          '7892020202020', NULL,          '1kg',   (SELECT id FROM categorias WHERE nome='Frutas e Verduras'), true),
    ('Tomate 1kg',                '7893030303030', NULL,          '1kg',   (SELECT id FROM categorias WHERE nome='Frutas e Verduras'), true),
    ('Batata Inglesa 1kg',        '7893131313131', NULL,          '1kg',   (SELECT id FROM categorias WHERE nome='Frutas e Verduras'), true),
    ('Sabão em Pó 1kg',           '7894040404040', 'Omo',         '1kg',   (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Detergente Líquido 500ml',  '7895050505050', 'Ypê',         '500ml', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Papel Higiênico 12 rolos',  '7895151515151', 'Neve',        '12un',  (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true)
ON CONFLICT (codigo_barras) DO NOTHING;

-- =====================================================================
-- Vínculos Comerciante ↔ Mercado
--   João  → Koch
--   Maria → Angeloni
--   Pedro → Giassi
-- =====================================================================
INSERT INTO mercado_comerciantes (mercado_id, comerciante_id, vinculado_por, data_vinculacao)
SELECT m.id, u.id, a.id, NOW()
FROM (VALUES
    ('84.683.408/0001-03', 'joao@koch.com'),
    ('83.646.984/0001-12', 'maria@angeloni.com'),
    ('79.084.053/0001-00', 'pedro@giassi.com')
) AS seed(cnpj, email)
INNER JOIN mercados m ON m.cnpj = seed.cnpj
INNER JOIN usuarios u ON u.email = seed.email
INNER JOIN usuarios a ON a.email = 'admin@cestajusta.com'
WHERE NOT EXISTS (
    SELECT 1 FROM mercado_comerciantes mc
    WHERE mc.mercado_id = m.id AND mc.comerciante_id = u.id
);

-- =====================================================================
-- Preços — 45 registros com variedade:
--   - Preços de donos entram como CONFIRMADO (simula regra de negócio já no seed)
--   - Preços colaborativos em PENDENTE
--   - Um DESATUALIZADO (café no Koch, coleta antiga)
--   - Um REJEITADO (óleo no Angeloni, simulando denúncia aprovada)
--   - Mesmo produto em múltiplos mercados para testar comparação
--   - Produtos com 1-2 mercados para testar casos de cobertura parcial
-- Seed só roda se a tabela precos estiver vazia (idempotência total).
-- =====================================================================
INSERT INTO precos (produto_id, mercado_id, usuario_id, valor, data_coleta, status, data_criacao)
SELECT p.id, m.id, u.id, seed.valor, seed.data_coleta::date, seed.status, NOW()
FROM (VALUES
    -- Arroz (5 mercados)
    (24.90, '2026-04-20', 'CONFIRMADO', '7891234567890', '84.683.408/0001-03', 'joao@koch.com'),
    (27.40, '2026-04-21', 'CONFIRMADO', '7891234567890', '83.646.984/0001-12', 'maria@angeloni.com'),
    (31.50, '2026-04-19', 'CONFIRMADO', '7891234567890', '79.084.053/0001-00', 'pedro@giassi.com'),
    (25.80, '2026-04-22', 'PENDENTE',   '7891234567890', '12.345.678/0001-01', 'ana@cestajusta.com'),
    (26.50, '2026-04-23', 'PENDENTE',   '7891234567890', '98.765.432/0001-02', 'bruno@cestajusta.com'),

    -- Feijão (3 mercados)
    (8.90,  '2026-04-20', 'CONFIRMADO', '7891111111111', '84.683.408/0001-03', 'joao@koch.com'),
    (9.20,  '2026-04-21', 'CONFIRMADO', '7891111111111', '83.646.984/0001-12', 'maria@angeloni.com'),
    (10.50, '2026-04-18', 'PENDENTE',   '7891111111111', '79.084.053/0001-00', 'carla@cestajusta.com'),

    -- Açúcar (3 mercados)
    (4.89,  '2026-04-20', 'CONFIRMADO', '7892222222222', '84.683.408/0001-03', 'joao@koch.com'),
    (5.20,  '2026-04-22', 'PENDENTE',   '7892222222222', '83.646.984/0001-12', 'ana@cestajusta.com'),
    (5.50,  '2026-04-19', 'CONFIRMADO', '7892222222222', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Café — histórico: 2 coletas no Koch (a mais antiga em DESATUALIZADO)
    (15.90, '2026-04-10', 'DESATUALIZADO', '7893333333333', '84.683.408/0001-03', 'ana@cestajusta.com'),
    (16.50, '2026-04-20', 'CONFIRMADO',    '7893333333333', '84.683.408/0001-03', 'joao@koch.com'),
    (17.90, '2026-04-21', 'CONFIRMADO',    '7893333333333', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Óleo — mostra um REJEITADO (simula denúncia aprovada contra R$ 25,00)
    (7.90,  '2026-04-20', 'CONFIRMADO', '7894444444444', '84.683.408/0001-03', 'joao@koch.com'),
    (25.00, '2026-04-22', 'REJEITADO',  '7894444444444', '83.646.984/0001-12', 'bruno@cestajusta.com'),
    (8.20,  '2026-04-23', 'CONFIRMADO', '7894444444444', '83.646.984/0001-12', 'maria@angeloni.com'),
    (8.80,  '2026-04-19', 'CONFIRMADO', '7894444444444', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Macarrão (3 mercados, inclui Popular de Brusque)
    (3.49,  '2026-04-20', 'PENDENTE',   '7895555555555', '84.683.408/0001-03', 'ana@cestajusta.com'),
    (3.79,  '2026-04-21', 'CONFIRMADO', '7895555555555', '83.646.984/0001-12', 'maria@angeloni.com'),
    (3.29,  '2026-04-18', 'PENDENTE',   '7895555555555', '98.765.432/0001-02', 'carla@cestajusta.com'),

    -- Leite (3 mercados, todos CONFIRMADOs pelos donos)
    (5.49,  '2026-04-20', 'CONFIRMADO', '7896666666666', '84.683.408/0001-03', 'joao@koch.com'),
    (5.99,  '2026-04-21', 'CONFIRMADO', '7896666666666', '83.646.984/0001-12', 'maria@angeloni.com'),
    (6.20,  '2026-04-19', 'CONFIRMADO', '7896666666666', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Manteiga (2 mercados — cobertura parcial)
    (12.90, '2026-04-20', 'CONFIRMADO', '7897777777777', '84.683.408/0001-03', 'joao@koch.com'),
    (13.50, '2026-04-22', 'PENDENTE',   '7897777777777', '83.646.984/0001-12', 'ana@cestajusta.com'),

    -- Queijo (3 mercados)
    (28.90, '2026-04-20', 'CONFIRMADO', '7898888888888', '84.683.408/0001-03', 'joao@koch.com'),
    (31.50, '2026-04-21', 'CONFIRMADO', '7898888888888', '83.646.984/0001-12', 'maria@angeloni.com'),
    (30.00, '2026-04-23', 'PENDENTE',   '7898888888888', '79.084.053/0001-00', 'bruno@cestajusta.com'),

    -- Refrigerante Cola (3 mercados)
    (9.99,  '2026-04-22', 'PENDENTE',   '7899999999999', '84.683.408/0001-03', 'ana@cestajusta.com'),
    (10.49, '2026-04-21', 'CONFIRMADO', '7899999999999', '83.646.984/0001-12', 'maria@angeloni.com'),
    (10.99, '2026-04-19', 'CONFIRMADO', '7899999999999', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Água (2 mercados)
    (2.50,  '2026-04-20', 'CONFIRMADO', '7891010101010', '84.683.408/0001-03', 'joao@koch.com'),
    (1.99,  '2026-04-22', 'PENDENTE',   '7891010101010', '12.345.678/0001-01', 'bruno@cestajusta.com'),

    -- Suco (2 mercados)
    (8.90,  '2026-04-21', 'CONFIRMADO', '7891212121212', '83.646.984/0001-12', 'maria@angeloni.com'),
    (9.50,  '2026-04-23', 'PENDENTE',   '7891212121212', '79.084.053/0001-00', 'carla@cestajusta.com'),

    -- Banana (3 mercados — mais barato no Popular Brusque)
    (4.99,  '2026-04-22', 'PENDENTE',   '7892020202020', '84.683.408/0001-03', 'ana@cestajusta.com'),
    (5.49,  '2026-04-21', 'CONFIRMADO', '7892020202020', '83.646.984/0001-12', 'maria@angeloni.com'),
    (3.99,  '2026-04-23', 'PENDENTE',   '7892020202020', '98.765.432/0001-02', 'carla@cestajusta.com'),

    -- Tomate (3 mercados)
    (7.90,  '2026-04-22', 'PENDENTE',   '7893030303030', '84.683.408/0001-03', 'bruno@cestajusta.com'),
    (8.50,  '2026-04-21', 'PENDENTE',   '7893030303030', '83.646.984/0001-12', 'ana@cestajusta.com'),
    (7.20,  '2026-04-19', 'CONFIRMADO', '7893030303030', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Batata (2 mercados)
    (5.99,  '2026-04-20', 'CONFIRMADO', '7893131313131', '84.683.408/0001-03', 'joao@koch.com'),
    (6.50,  '2026-04-22', 'PENDENTE',   '7893131313131', '83.646.984/0001-12', 'ana@cestajusta.com'),

    -- Sabão em Pó (3 mercados, todos CONFIRMADOs)
    (22.90, '2026-04-20', 'CONFIRMADO', '7894040404040', '84.683.408/0001-03', 'joao@koch.com'),
    (24.50, '2026-04-21', 'CONFIRMADO', '7894040404040', '83.646.984/0001-12', 'maria@angeloni.com'),
    (26.00, '2026-04-19', 'CONFIRMADO', '7894040404040', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Detergente (2 mercados)
    (3.29,  '2026-04-22', 'PENDENTE',   '7895050505050', '84.683.408/0001-03', 'bruno@cestajusta.com'),
    (3.49,  '2026-04-21', 'CONFIRMADO', '7895050505050', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Papel Higiênico (3 mercados)
    (24.90, '2026-04-20', 'CONFIRMADO', '7895151515151', '84.683.408/0001-03', 'joao@koch.com'),
    (26.50, '2026-04-22', 'PENDENTE',   '7895151515151', '83.646.984/0001-12', 'ana@cestajusta.com'),
    (28.00, '2026-04-23', 'PENDENTE',   '7895151515151', '79.084.053/0001-00', 'carla@cestajusta.com')
) AS seed(valor, data_coleta, status, codigo_barras, cnpj, email)
INNER JOIN produtos p  ON p.codigo_barras = seed.codigo_barras
INNER JOIN mercados m  ON m.cnpj = seed.cnpj
INNER JOIN usuarios u  ON u.email = seed.email
WHERE NOT EXISTS (SELECT 1 FROM precos LIMIT 1);
