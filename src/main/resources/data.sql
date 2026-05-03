-- =====================================================================
-- pgcrypto: gera hashes BCrypt compatíveis com BCryptPasswordEncoder
-- =====================================================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =====================================================================
-- Categorias
-- =====================================================================
INSERT INTO categorias (nome, ativo, data_criacao) VALUES
    ('Alimentos',         true, NOW()),
    ('Bebidas',           true, NOW()),
    ('Higiene e Limpeza', true, NOW()),
    ('Frutas e Verduras', true, NOW()),
    ('Lácteos',           true, NOW()),
    ('Carnes e Peixes',   true, NOW())
ON CONFLICT (nome) DO NOTHING;

-- =====================================================================
-- Usuários (todas as senhas: senha123)
--   admin@cestajusta.com     → ADMIN
--   ana/bruno/carla          → CONSUMIDOR
--   joao/maria/pedro         → COMERCIANTE (donos dos mercados originais)
-- =====================================================================
INSERT INTO usuarios (nome, email, senha, tipo_usuario, ativo, data_criacao) VALUES
    ('Admin Sistema', 'admin@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'ADMIN',       true, NOW()),
    ('Ana Souza',     'ana@cestajusta.com',   crypt('senha123', gen_salt('bf', 10)), 'CONSUMIDOR',  true, NOW()),
    ('Bruno Lima',    'bruno@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'CONSUMIDOR',  true, NOW()),
    ('Carla Rocha',   'carla@cestajusta.com', crypt('senha123', gen_salt('bf', 10)), 'CONSUMIDOR',  true, NOW()),
    ('João Koch',     'joao@koch.com',        crypt('senha123', gen_salt('bf', 10)), 'COMERCIANTE', true, NOW()),
    ('Maria Angeloni','maria@angeloni.com',   crypt('senha123', gen_salt('bf', 10)), 'COMERCIANTE', true, NOW()),
    ('Pedro Giassi',  'pedro@giassi.com',     crypt('senha123', gen_salt('bf', 10)), 'COMERCIANTE', true, NOW())
ON CONFLICT (email) DO NOTHING;

-- =====================================================================
-- Mercados — região de Blumenau/SC
--   5 originais (Koch, Angeloni, Giassi, M.E. Gaspar, Popular Brusque)
--   6 novos de Blumenau (Top, Cooper, Super A, Fort Atacadista, Capri, Brasil)
-- =====================================================================
INSERT INTO mercados (nome_fantasia, cnpj, cidade, estado, ativo, data_criacao) VALUES
    ('Supermercado Koch',            '84.683.408/0001-03', 'Blumenau', 'SC', true, NOW()),
    ('Angeloni Blumenau',            '83.646.984/0001-12', 'Blumenau', 'SC', true, NOW()),
    ('Giassi Supermercados',         '79.084.053/0001-00', 'Blumenau', 'SC', true, NOW()),
    ('Mercado Econômico Gaspar',     '12.345.678/0001-01', 'Gaspar',   'SC', true, NOW()),
    ('Supermercado Popular Brusque', '98.765.432/0001-02', 'Brusque',  'SC', true, NOW()),
    ('Top Supermercados',            '11.111.111/0001-11', 'Blumenau', 'SC', true, NOW()),
    ('Cooper Supermercados',         '22.222.222/0001-22', 'Blumenau', 'SC', true, NOW()),
    ('Super A Supermercados',        '33.333.333/0001-33', 'Blumenau', 'SC', true, NOW()),
    ('Fort Atacadista',              '44.444.444/0001-44', 'Blumenau', 'SC', true, NOW()),
    ('Capri Supermercado e Açougue', '55.555.555/0001-55', 'Blumenau', 'SC', true, NOW()),
    ('Brasil Atacadista',            '66.666.666/0001-66', 'Blumenau', 'SC', true, NOW())
ON CONFLICT (cnpj) DO NOTHING;

-- =====================================================================
-- Produtos — 27 itens da cesta básica oficial CestaJusta
--   9 mantêm código de barras antigo (compatibilidade com históricos)
--   18 são novos
-- =====================================================================
INSERT INTO produtos (nome, codigo_barras, marca, unidade_medida, imagem_path, categoria_id, ativo) VALUES
    -- Mantidos (com rename/marca atualizada)
    ('Arroz TP1 5kg',                          '7891234567890', 'Namorado',     '5kg',    'https://images.unsplash.com/photo-1568347877321-f8935c7dc5a8?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Feijão Carioca TP1 1kg',                 '7891111111111', 'Namorado',     '1kg',    'https://images.unsplash.com/photo-1600353068440-9a7d3b71ce62?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Açúcar Refinado 1kg',                    '7892222222222', 'União',        '1kg',    'https://images.unsplash.com/photo-1581600140682-d4e68c8e3d9c?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Café 500g',                              '7893333333333', 'Pilão',        '500g',   'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Óleo de Soja 900ml',                     '7894444444444', 'Liza',         '900ml',  'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Macarrão Spaghetti C/Ovos 500g',         '7895555555555', 'Dona Benta',   '500g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Sabão em Pó 800g',                       '7894040404040', 'Omo',          '800g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Detergente Líquido 500ml',               '7895050505050', 'Minuano',      '500ml',  'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Papel Higiênico Branco Folha Dupla c/4', '7895151515151', 'Personal VIP', '4un',    'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    -- Novos
    ('Milho Verde 170g',                       '7891001000001', 'Stella D''Oro','170g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Polpa de Tomate 300g',                   '7891001000002', 'Predilecta',   '300g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Tempero 300g',                           '7891001000003', 'Bene',         '300g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Biscoito Recheado 140g',                 '7891001000004', 'Bauducco',     '140g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Leite em Pó Integral 200g',              '7891001000005', 'Italac',       '200g',   'https://images.unsplash.com/photo-1563636619-e9143da7973b?w=200', (SELECT id FROM categorias WHERE nome='Lácteos'), true),
    ('Achocolatado 350g',                      '7891001000006', 'Nescau',       '350g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Goiabada 300g',                          '7891001000007', 'Xavante',      '300g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Gelatina em Pó 25g',                     '7891001000008', 'Sol',          '25g',    'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Fubá 500g',                              '7891001000009', 'Agrobal',      '500g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Farinha de Mandioca 500g',               '7891001000010', 'Mana',         '500g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Mistura Para Bolo Chocolate 400g',       '7891001000011', 'Dr. Oetker',   '400g',   'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Sal Refinado 1kg',                       '7891001000012', 'Cisne',        '1kg',    'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Farinha de Trigo 1kg',                   '7891001000013', 'Dona Benta',   '1kg',    'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Água Sanitária 1L',                      '7891001000014', 'Conde',        '1L',     'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Desinfetante 500ml',                     '7891001000015', 'Ypê',          '500ml',  'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Esponja Lã de Aço',                      '7891001000016', 'Assolan',      '8un',    'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Sabão em Pedra 5x200g',                  '7891001000017', 'Zavaski-Amare','5x200g', 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Sabonete 85g',                           '7891001000018', 'Ypê',          '85g',    'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true)
ON CONFLICT (codigo_barras) DO NOTHING;

-- =====================================================================
-- Vínculos Comerciante ↔ Mercado (apenas mercados originais)
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
-- Preços — snapshot inicial (1 preço por produto/mercado, datas recentes)
-- Cobertura: cada um dos 27 produtos com 3-4 mercados nos últimos 5 dias.
-- O enriquecimento histórico (várias datas por mercado) virá em seed seguinte.
-- Seed só roda se a tabela precos estiver vazia (idempotência total).
-- =====================================================================
INSERT INTO precos (produto_id, mercado_id, usuario_id, valor, data_coleta, status, data_criacao)
SELECT p.id, m.id, u.id, seed.valor, seed.data_coleta::date, seed.status, NOW()
FROM (VALUES
    -- Arroz TP1 5kg
    (28.90, '2026-05-01', 'CONFIRMADO', '7891234567890', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (29.50, '2026-05-02', 'PENDENTE',   '7891234567890', '22.222.222/0001-22', 'bruno@cestajusta.com'),
    (27.90, '2026-05-03', 'PENDENTE',   '7891234567890', '44.444.444/0001-44', 'carla@cestajusta.com'),
    (30.50, '2026-05-01', 'CONFIRMADO', '7891234567890', '84.683.408/0001-03', 'joao@koch.com'),

    -- Feijão Carioca
    (8.49,  '2026-05-01', 'CONFIRMADO', '7891111111111', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (8.99,  '2026-05-02', 'PENDENTE',   '7891111111111', '33.333.333/0001-33', 'bruno@cestajusta.com'),
    (9.20,  '2026-05-03', 'CONFIRMADO', '7891111111111', '84.683.408/0001-03', 'joao@koch.com'),

    -- Açúcar Refinado
    (4.79,  '2026-05-01', 'CONFIRMADO', '7892222222222', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (4.99,  '2026-05-02', 'PENDENTE',   '7892222222222', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (5.29,  '2026-05-03', 'CONFIRMADO', '7892222222222', '83.646.984/0001-12', 'maria@angeloni.com'),
    (4.89,  '2026-05-02', 'PENDENTE',   '7892222222222', '66.666.666/0001-66', 'carla@cestajusta.com'),

    -- Café
    (16.90, '2026-05-01', 'CONFIRMADO', '7893333333333', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (17.50, '2026-05-02', 'CONFIRMADO', '7893333333333', '84.683.408/0001-03', 'joao@koch.com'),
    (18.20, '2026-05-03', 'PENDENTE',   '7893333333333', '55.555.555/0001-55', 'bruno@cestajusta.com'),

    -- Óleo de Soja
    (7.49,  '2026-05-01', 'CONFIRMADO', '7894444444444', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (7.99,  '2026-05-02', 'PENDENTE',   '7894444444444', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (8.29,  '2026-05-03', 'CONFIRMADO', '7894444444444', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Macarrão Spaghetti
    (3.99,  '2026-05-01', 'CONFIRMADO', '7895555555555', '33.333.333/0001-33', 'ana@cestajusta.com'),
    (4.29,  '2026-05-02', 'PENDENTE',   '7895555555555', '22.222.222/0001-22', 'bruno@cestajusta.com'),
    (3.79,  '2026-05-03', 'CONFIRMADO', '7895555555555', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Sabão em Pó 800g
    (18.90, '2026-05-01', 'CONFIRMADO', '7894040404040', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (19.50, '2026-05-02', 'PENDENTE',   '7894040404040', '44.444.444/0001-44', 'bruno@cestajusta.com'),
    (20.90, '2026-05-03', 'CONFIRMADO', '7894040404040', '84.683.408/0001-03', 'joao@koch.com'),

    -- Detergente Líquido
    (2.99,  '2026-05-01', 'CONFIRMADO', '7895050505050', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (3.29,  '2026-05-02', 'PENDENTE',   '7895050505050', '55.555.555/0001-55', 'bruno@cestajusta.com'),
    (3.49,  '2026-05-03', 'CONFIRMADO', '7895050505050', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Papel Higiênico c/4
    (12.90, '2026-05-01', 'CONFIRMADO', '7895151515151', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (13.90, '2026-05-02', 'PENDENTE',   '7895151515151', '33.333.333/0001-33', 'bruno@cestajusta.com'),
    (14.50, '2026-05-03', 'CONFIRMADO', '7895151515151', '84.683.408/0001-03', 'joao@koch.com'),

    -- Milho Verde
    (3.49,  '2026-05-01', 'CONFIRMADO', '7891001000001', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (3.79,  '2026-05-02', 'PENDENTE',   '7891001000001', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (3.99,  '2026-05-03', 'CONFIRMADO', '7891001000001', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Polpa de Tomate
    (4.29,  '2026-05-01', 'CONFIRMADO', '7891001000002', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (4.59,  '2026-05-02', 'PENDENTE',   '7891001000002', '55.555.555/0001-55', 'bruno@cestajusta.com'),
    (4.89,  '2026-05-03', 'CONFIRMADO', '7891001000002', '84.683.408/0001-03', 'joao@koch.com'),

    -- Tempero
    (5.49,  '2026-05-01', 'CONFIRMADO', '7891001000003', '33.333.333/0001-33', 'ana@cestajusta.com'),
    (5.99,  '2026-05-02', 'PENDENTE',   '7891001000003', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (6.29,  '2026-05-03', 'CONFIRMADO', '7891001000003', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Biscoito Recheado
    (2.79,  '2026-05-01', 'PENDENTE',   '7891001000004', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (2.99,  '2026-05-02', 'CONFIRMADO', '7891001000004', '22.222.222/0001-22', 'maria@angeloni.com'),
    (3.49,  '2026-05-03', 'PENDENTE',   '7891001000004', '55.555.555/0001-55', 'bruno@cestajusta.com'),

    -- Leite em Pó Integral
    (12.90, '2026-05-01', 'CONFIRMADO', '7891001000005', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (13.50, '2026-05-02', 'PENDENTE',   '7891001000005', '66.666.666/0001-66', 'bruno@cestajusta.com'),
    (14.20, '2026-05-03', 'CONFIRMADO', '7891001000005', '84.683.408/0001-03', 'joao@koch.com'),

    -- Achocolatado
    (8.99,  '2026-05-01', 'CONFIRMADO', '7891001000006', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (9.49,  '2026-05-02', 'PENDENTE',   '7891001000006', '33.333.333/0001-33', 'bruno@cestajusta.com'),
    (9.99,  '2026-05-03', 'CONFIRMADO', '7891001000006', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Goiabada
    (6.49,  '2026-05-01', 'CONFIRMADO', '7891001000007', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (6.99,  '2026-05-02', 'PENDENTE',   '7891001000007', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (7.29,  '2026-05-03', 'CONFIRMADO', '7891001000007', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Gelatina em Pó
    (1.49,  '2026-05-01', 'PENDENTE',   '7891001000008', '33.333.333/0001-33', 'ana@cestajusta.com'),
    (1.79,  '2026-05-02', 'CONFIRMADO', '7891001000008', '22.222.222/0001-22', 'bruno@cestajusta.com'),
    (1.99,  '2026-05-03', 'PENDENTE',   '7891001000008', '55.555.555/0001-55', 'carla@cestajusta.com'),

    -- Fubá
    (3.29,  '2026-05-01', 'CONFIRMADO', '7891001000009', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (3.49,  '2026-05-02', 'PENDENTE',   '7891001000009', '44.444.444/0001-44', 'bruno@cestajusta.com'),
    (3.79,  '2026-05-03', 'CONFIRMADO', '7891001000009', '84.683.408/0001-03', 'joao@koch.com'),

    -- Farinha de Mandioca
    (4.49,  '2026-05-01', 'CONFIRMADO', '7891001000010', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (4.79,  '2026-05-02', 'PENDENTE',   '7891001000010', '66.666.666/0001-66', 'bruno@cestajusta.com'),
    (4.99,  '2026-05-03', 'CONFIRMADO', '7891001000010', '83.646.984/0001-12', 'maria@angeloni.com'),

    -- Mistura Para Bolo
    (5.99,  '2026-05-01', 'PENDENTE',   '7891001000011', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (6.29,  '2026-05-02', 'CONFIRMADO', '7891001000011', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (6.49,  '2026-05-03', 'PENDENTE',   '7891001000011', '55.555.555/0001-55', 'carla@cestajusta.com'),

    -- Sal Refinado
    (2.49,  '2026-05-01', 'CONFIRMADO', '7891001000012', '33.333.333/0001-33', 'ana@cestajusta.com'),
    (2.69,  '2026-05-02', 'PENDENTE',   '7891001000012', '22.222.222/0001-22', 'bruno@cestajusta.com'),
    (2.89,  '2026-05-03', 'CONFIRMADO', '7891001000012', '84.683.408/0001-03', 'joao@koch.com'),

    -- Farinha de Trigo
    (4.29,  '2026-05-01', 'CONFIRMADO', '7891001000013', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (4.49,  '2026-05-02', 'PENDENTE',   '7891001000013', '44.444.444/0001-44', 'bruno@cestajusta.com'),
    (4.79,  '2026-05-03', 'CONFIRMADO', '7891001000013', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Água Sanitária
    (4.99,  '2026-05-01', 'PENDENTE',   '7891001000014', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (5.49,  '2026-05-02', 'CONFIRMADO', '7891001000014', '33.333.333/0001-33', 'bruno@cestajusta.com'),
    (5.79,  '2026-05-03', 'PENDENTE',   '7891001000014', '83.646.984/0001-12', 'carla@cestajusta.com'),

    -- Desinfetante
    (6.49,  '2026-05-01', 'CONFIRMADO', '7891001000015', '44.444.444/0001-44', 'ana@cestajusta.com'),
    (6.99,  '2026-05-02', 'PENDENTE',   '7891001000015', '11.111.111/0001-11', 'bruno@cestajusta.com'),
    (7.49,  '2026-05-03', 'CONFIRMADO', '7891001000015', '84.683.408/0001-03', 'joao@koch.com'),

    -- Esponja Lã de Aço
    (3.49,  '2026-05-01', 'CONFIRMADO', '7891001000016', '33.333.333/0001-33', 'ana@cestajusta.com'),
    (3.79,  '2026-05-02', 'PENDENTE',   '7891001000016', '22.222.222/0001-22', 'bruno@cestajusta.com'),
    (3.99,  '2026-05-03', 'CONFIRMADO', '7891001000016', '79.084.053/0001-00', 'pedro@giassi.com'),

    -- Sabão em Pedra 5x200g
    (8.99,  '2026-05-01', 'PENDENTE',   '7891001000017', '11.111.111/0001-11', 'ana@cestajusta.com'),
    (9.49,  '2026-05-02', 'CONFIRMADO', '7891001000017', '44.444.444/0001-44', 'bruno@cestajusta.com'),
    (9.99,  '2026-05-03', 'PENDENTE',   '7891001000017', '55.555.555/0001-55', 'carla@cestajusta.com'),

    -- Sabonete
    (1.79,  '2026-05-01', 'CONFIRMADO', '7891001000018', '22.222.222/0001-22', 'ana@cestajusta.com'),
    (1.99,  '2026-05-02', 'PENDENTE',   '7891001000018', '66.666.666/0001-66', 'bruno@cestajusta.com'),
    (2.29,  '2026-05-03', 'CONFIRMADO', '7891001000018', '83.646.984/0001-12', 'maria@angeloni.com')
) AS seed(valor, data_coleta, status, codigo_barras, cnpj, email)
INNER JOIN produtos p  ON p.codigo_barras = seed.codigo_barras
INNER JOIN mercados m  ON m.cnpj = seed.cnpj
INNER JOIN usuarios u  ON u.email = seed.email
WHERE NOT EXISTS (SELECT 1 FROM precos LIMIT 1);
