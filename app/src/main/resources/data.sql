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
    ('Arroz TP1 5kg',                          '7891234567890', 'Namorado',     '5kg',    '/produtos/arroz.png',           (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Feijão Carioca TP1 1kg',                 '7891111111111', 'Camil',        '1kg',    '/produtos/feijao.png',          (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Açúcar Refinado 1kg',                    '7892222222222', 'União',        '1kg',    '/produtos/acucar.png',          (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Café 500g',                              '7893333333333', 'Pilão',        '500g',   '/produtos/cafe.png',            (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Óleo de Soja 900ml',                     '7894444444444', 'Liza',         '900ml',  '/produtos/oleo.png',            (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Macarrão Spaghetti C/Ovos 500g',         '7895555555555', 'Dona Benta',   '500g',   '/produtos/macarrao.jpg',        (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Sabão em Pó 800g',                       '7894040404040', 'Omo',          '800g',   '/produtos/sabao-po.png',        (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Detergente Líquido 500ml',               '7895050505050', 'Minuano',      '500ml',  '/produtos/detergente.png',      (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Papel Higiênico Branco Folha Dupla c/4', '7895151515151', 'Personal VIP', '4un',    '/produtos/papel-higienico.png', (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    -- Novos
    ('Milho Verde 170g',                       '7891001000001', 'Stella D''Oro','170g',   '/produtos/milho-verde.png',     (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Polpa de Tomate 300g',                   '7891001000002', 'Predilecta',   '300g',   '/produtos/polpa-tomate.png',    (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Tempero 300g',                           '7891001000003', 'Bene',         '300g',   '/produtos/tempero.png',         (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Biscoito Recheado 140g',                 '7891001000004', 'Bauducco',     '140g',   '/produtos/biscoito.png',        (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Leite em Pó Integral 200g',              '7891001000005', 'Italac',       '200g',   '/produtos/leite-po.png',        (SELECT id FROM categorias WHERE nome='Lácteos'), true),
    ('Achocolatado 350g',                      '7891001000006', 'Nescau',       '350g',   '/produtos/achocolatado.png',    (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Goiabada 300g',                          '7891001000007', 'Xavante',      '300g',   '/produtos/goiabada.png',        (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Gelatina em Pó 25g',                     '7891001000008', 'Sol',          '25g',    '/produtos/gelatina.png',        (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Fubá 500g',                              '7891001000009', 'Agrobal',      '500g',   '/produtos/fuba.png',            (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Farinha de Mandioca 500g',               '7891001000010', 'Mana',         '500g',   '/produtos/farinha-mandioca.png',(SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Mistura Para Bolo Chocolate 400g',       '7891001000011', 'Dr. Oetker',   '400g',   '/produtos/mistura-bolo.png',    (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Sal Refinado 1kg',                       '7891001000012', 'Cisne',        '1kg',    '/produtos/sal.png',             (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Farinha de Trigo 1kg',                   '7891001000013', 'Dona Benta',   '1kg',    '/produtos/farinha-trigo.png',   (SELECT id FROM categorias WHERE nome='Alimentos'), true),
    ('Água Sanitária 1L',                      '7891001000014', 'Conde',        '1L',     '/produtos/agua-sanitaria.png',  (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Desinfetante 500ml',                     '7891001000015', 'Ypê',          '500ml',  '/produtos/desinfetante.png',    (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Esponja Lã de Aço',                      '7891001000016', 'Assolan',      '8un',    '/produtos/esponja.png',         (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Sabão em Pedra 5x200g',                  '7891001000017', 'Zavaski-Amare','5x200g', '/produtos/sabao-pedra.png',     (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true),
    ('Sabonete 85g',                           '7891001000018', 'Ypê',          '85g',    '/produtos/sabonete.png',        (SELECT id FROM categorias WHERE nome='Higiene e Limpeza'), true)
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
-- Preços — histórico denso (10 datas por par produto/mercado em 30 dias)
-- Cada par (produto, mercado) tem (valor_base, tendencia):
--   valor_base = preço de HOJE
--   tendencia  = % de variação ao longo de 30 dias
-- generate_series(0, 27, 3) gera 10 offsets de dias; interpolação linear:
--   valor(day_offset) = valor_base * (1 - tendencia/100 * day_offset/27)
-- Status alterna CONFIRMADO/PENDENTE pra simular fluxo colaborativo.
-- 27 produtos × 4 mercados × 10 datas = ~1080 preços.
-- Seed só roda se a tabela precos estiver vazia (idempotência total).
-- =====================================================================
INSERT INTO precos (produto_id, mercado_id, usuario_id, valor, data_coleta, status, data_criacao)
SELECT
    p.id,
    m.id,
    u.id,
    ROUND((base.valor_base * (1 - (base.tendencia / 100.0) * (day_offset / 27.0)))::numeric, 2) AS valor,
    (CURRENT_DATE - day_offset)::date AS data_coleta,
    CASE WHEN day_offset % 6 = 0 THEN 'CONFIRMADO' ELSE 'PENDENTE' END AS status,
    NOW() AS data_criacao
FROM (VALUES
    -- (codigo_barras,  cnpj,                  email,                   valor_base, tendencia_30d)
    -- Arroz TP1 5kg — flat (mercados divergem)
    ('7891234567890', '11.111.111/0001-11', 'ana@cestajusta.com',     28.50,  2.0),
    ('7891234567890', '22.222.222/0001-22', 'bruno@cestajusta.com',   29.90, -1.5),
    ('7891234567890', '44.444.444/0001-44', 'carla@cestajusta.com',   27.50, -3.5),
    ('7891234567890', '84.683.408/0001-03', 'joao@koch.com',          30.50,  4.5),
    -- Feijão Carioca 1kg — queda leve
    ('7891111111111', '66.666.666/0001-66', 'carla@cestajusta.com',    8.49, -2.0),
    ('7891111111111', '11.111.111/0001-11', 'ana@cestajusta.com',      8.99, -3.0),
    ('7891111111111', '33.333.333/0001-33', 'bruno@cestajusta.com',    9.50, -1.0),
    ('7891111111111', '83.646.984/0001-12', 'maria@angeloni.com',      9.20,  1.0),
    -- Açúcar Refinado 1kg — alta consistente
    ('7892222222222', '44.444.444/0001-44', 'ana@cestajusta.com',      4.79,  5.0),
    ('7892222222222', '22.222.222/0001-22', 'bruno@cestajusta.com',    5.20,  4.5),
    ('7892222222222', '11.111.111/0001-11', 'carla@cestajusta.com',    4.89,  5.5),
    ('7892222222222', '55.555.555/0001-55', 'bruno@cestajusta.com',    5.49,  3.0),
    -- Café 500g — alta forte
    ('7893333333333', '66.666.666/0001-66', 'carla@cestajusta.com',   16.90,  6.0),
    ('7893333333333', '22.222.222/0001-22', 'ana@cestajusta.com',     17.50,  5.5),
    ('7893333333333', '55.555.555/0001-55', 'bruno@cestajusta.com',   18.50,  4.5),
    ('7893333333333', '84.683.408/0001-03', 'joao@koch.com',          17.90,  5.0),
    -- Óleo de Soja 900ml — queda
    ('7894444444444', '44.444.444/0001-44', 'ana@cestajusta.com',      7.49, -2.0),
    ('7894444444444', '11.111.111/0001-11', 'bruno@cestajusta.com',    7.99, -1.5),
    ('7894444444444', '79.084.053/0001-00', 'pedro@giassi.com',        8.29, -1.0),
    ('7894444444444', '66.666.666/0001-66', 'carla@cestajusta.com',    7.69, -3.0),
    -- Macarrão Spaghetti C/Ovos
    ('7895555555555', '33.333.333/0001-33', 'ana@cestajusta.com',      3.99,  1.0),
    ('7895555555555', '22.222.222/0001-22', 'bruno@cestajusta.com',    4.29,  0.5),
    ('7895555555555', '83.646.984/0001-12', 'maria@angeloni.com',      3.79,  2.0),
    ('7895555555555', '55.555.555/0001-55', 'carla@cestajusta.com',    4.49, -0.5),
    -- Sabão em Pó 800g — alta
    ('7894040404040', '11.111.111/0001-11', 'ana@cestajusta.com',     18.90,  2.5),
    ('7894040404040', '44.444.444/0001-44', 'bruno@cestajusta.com',   17.90,  1.5),
    ('7894040404040', '84.683.408/0001-03', 'joao@koch.com',          20.50,  3.0),
    ('7894040404040', '66.666.666/0001-66', 'carla@cestajusta.com',   18.50,  2.0),
    -- Detergente Líquido 500ml
    ('7895050505050', '22.222.222/0001-22', 'ana@cestajusta.com',      2.99, -1.0),
    ('7895050505050', '55.555.555/0001-55', 'bruno@cestajusta.com',    3.49,  0.5),
    ('7895050505050', '83.646.984/0001-12', 'maria@angeloni.com',      3.29,  0.0),
    ('7895050505050', '33.333.333/0001-33', 'carla@cestajusta.com',    3.19, -0.5),
    -- Papel Higiênico c/4 — alta
    ('7895151515151', '11.111.111/0001-11', 'ana@cestajusta.com',     12.90,  1.5),
    ('7895151515151', '33.333.333/0001-33', 'bruno@cestajusta.com',   13.90,  2.5),
    ('7895151515151', '84.683.408/0001-03', 'joao@koch.com',          14.50,  3.0),
    ('7895151515151', '44.444.444/0001-44', 'carla@cestajusta.com',   12.50,  1.0),
    -- Milho Verde 170g
    ('7891001000001', '44.444.444/0001-44', 'ana@cestajusta.com',      3.49,  0.5),
    ('7891001000001', '11.111.111/0001-11', 'bruno@cestajusta.com',    3.79,  0.0),
    ('7891001000001', '83.646.984/0001-12', 'maria@angeloni.com',      3.99,  1.0),
    ('7891001000001', '66.666.666/0001-66', 'carla@cestajusta.com',    3.59,  0.5),
    -- Polpa de Tomate 300g
    ('7891001000002', '22.222.222/0001-22', 'ana@cestajusta.com',      4.29, -1.5),
    ('7891001000002', '55.555.555/0001-55', 'bruno@cestajusta.com',    4.79,  0.0),
    ('7891001000002', '84.683.408/0001-03', 'joao@koch.com',           4.89,  1.0),
    ('7891001000002', '33.333.333/0001-33', 'carla@cestajusta.com',    4.59, -0.5),
    -- Tempero 300g — alta
    ('7891001000003', '33.333.333/0001-33', 'ana@cestajusta.com',      5.49,  2.0),
    ('7891001000003', '11.111.111/0001-11', 'bruno@cestajusta.com',    5.99,  1.5),
    ('7891001000003', '79.084.053/0001-00', 'pedro@giassi.com',        6.29,  2.5),
    ('7891001000003', '55.555.555/0001-55', 'carla@cestajusta.com',    6.19,  1.0),
    -- Biscoito Recheado 140g
    ('7891001000004', '44.444.444/0001-44', 'ana@cestajusta.com',      2.79,  1.0),
    ('7891001000004', '22.222.222/0001-22', 'bruno@cestajusta.com',    2.99,  1.5),
    ('7891001000004', '55.555.555/0001-55', 'carla@cestajusta.com',    3.49,  0.0),
    ('7891001000004', '66.666.666/0001-66', 'ana@cestajusta.com',      2.69,  1.5),
    -- Leite em Pó Integral 200g — alta forte
    ('7891001000005', '11.111.111/0001-11', 'ana@cestajusta.com',     12.90,  3.5),
    ('7891001000005', '66.666.666/0001-66', 'bruno@cestajusta.com',   13.50,  3.0),
    ('7891001000005', '84.683.408/0001-03', 'joao@koch.com',          14.20,  4.0),
    ('7891001000005', '44.444.444/0001-44', 'carla@cestajusta.com',   12.50,  3.0),
    -- Achocolatado 350g
    ('7891001000006', '22.222.222/0001-22', 'ana@cestajusta.com',      8.99, -1.0),
    ('7891001000006', '33.333.333/0001-33', 'bruno@cestajusta.com',    9.49,  0.0),
    ('7891001000006', '83.646.984/0001-12', 'maria@angeloni.com',      9.99,  0.5),
    ('7891001000006', '55.555.555/0001-55', 'carla@cestajusta.com',   10.50, -0.5),
    -- Goiabada 300g
    ('7891001000007', '44.444.444/0001-44', 'ana@cestajusta.com',      6.49,  0.5),
    ('7891001000007', '11.111.111/0001-11', 'bruno@cestajusta.com',    6.99,  1.0),
    ('7891001000007', '79.084.053/0001-00', 'pedro@giassi.com',        7.29,  1.5),
    ('7891001000007', '66.666.666/0001-66', 'carla@cestajusta.com',    6.39,  0.0),
    -- Gelatina em Pó 25g — queda
    ('7891001000008', '33.333.333/0001-33', 'ana@cestajusta.com',      1.49, -2.0),
    ('7891001000008', '22.222.222/0001-22', 'bruno@cestajusta.com',    1.79, -1.5),
    ('7891001000008', '55.555.555/0001-55', 'carla@cestajusta.com',    1.99,  0.0),
    ('7891001000008', '11.111.111/0001-11', 'ana@cestajusta.com',      1.69, -2.5),
    -- Fubá 500g
    ('7891001000009', '11.111.111/0001-11', 'ana@cestajusta.com',      3.29,  0.5),
    ('7891001000009', '44.444.444/0001-44', 'bruno@cestajusta.com',    3.49,  0.0),
    ('7891001000009', '84.683.408/0001-03', 'joao@koch.com',           3.79,  1.0),
    ('7891001000009', '66.666.666/0001-66', 'carla@cestajusta.com',    3.19,  0.5),
    -- Farinha de Mandioca 500g
    ('7891001000010', '22.222.222/0001-22', 'ana@cestajusta.com',      4.49, -0.5),
    ('7891001000010', '66.666.666/0001-66', 'bruno@cestajusta.com',    4.79,  0.0),
    ('7891001000010', '83.646.984/0001-12', 'maria@angeloni.com',      4.99,  1.0),
    ('7891001000010', '33.333.333/0001-33', 'carla@cestajusta.com',    4.59,  0.5),
    -- Mistura Para Bolo 400g
    ('7891001000011', '44.444.444/0001-44', 'ana@cestajusta.com',      5.99,  2.0),
    ('7891001000011', '11.111.111/0001-11', 'bruno@cestajusta.com',    6.29,  1.5),
    ('7891001000011', '55.555.555/0001-55', 'carla@cestajusta.com',    6.79,  0.5),
    ('7891001000011', '22.222.222/0001-22', 'ana@cestajusta.com',      6.49,  1.0),
    -- Sal Refinado 1kg — queda leve
    ('7891001000012', '33.333.333/0001-33', 'ana@cestajusta.com',      2.49, -1.0),
    ('7891001000012', '22.222.222/0001-22', 'bruno@cestajusta.com',    2.69, -0.5),
    ('7891001000012', '84.683.408/0001-03', 'joao@koch.com',           2.89,  0.0),
    ('7891001000012', '66.666.666/0001-66', 'carla@cestajusta.com',    2.39, -1.5),
    -- Farinha de Trigo 1kg — alta
    ('7891001000013', '11.111.111/0001-11', 'ana@cestajusta.com',      4.29,  1.5),
    ('7891001000013', '44.444.444/0001-44', 'bruno@cestajusta.com',    4.49,  1.0),
    ('7891001000013', '79.084.053/0001-00', 'pedro@giassi.com',        4.79,  2.0),
    ('7891001000013', '66.666.666/0001-66', 'carla@cestajusta.com',    4.19,  1.0),
    -- Água Sanitária 1L — queda
    ('7891001000014', '22.222.222/0001-22', 'ana@cestajusta.com',      4.99, -2.0),
    ('7891001000014', '33.333.333/0001-33', 'bruno@cestajusta.com',    5.49, -1.5),
    ('7891001000014', '83.646.984/0001-12', 'carla@cestajusta.com',    5.79, -1.0),
    ('7891001000014', '55.555.555/0001-55', 'ana@cestajusta.com',      5.99, -0.5),
    -- Desinfetante 500ml
    ('7891001000015', '44.444.444/0001-44', 'ana@cestajusta.com',      6.49, -0.5),
    ('7891001000015', '11.111.111/0001-11', 'bruno@cestajusta.com',    6.99,  0.0),
    ('7891001000015', '84.683.408/0001-03', 'joao@koch.com',           7.49,  1.0),
    ('7891001000015', '66.666.666/0001-66', 'carla@cestajusta.com',    6.39, -1.0),
    -- Esponja Lã de Aço
    ('7891001000016', '33.333.333/0001-33', 'ana@cestajusta.com',      3.49,  1.0),
    ('7891001000016', '22.222.222/0001-22', 'bruno@cestajusta.com',    3.79,  0.5),
    ('7891001000016', '79.084.053/0001-00', 'pedro@giassi.com',        3.99,  1.5),
    ('7891001000016', '66.666.666/0001-66', 'carla@cestajusta.com',    3.39,  1.0),
    -- Sabão em Pedra 5x200g — alta
    ('7891001000017', '11.111.111/0001-11', 'ana@cestajusta.com',      8.99,  2.0),
    ('7891001000017', '44.444.444/0001-44', 'bruno@cestajusta.com',    9.49,  1.5),
    ('7891001000017', '55.555.555/0001-55', 'carla@cestajusta.com',    9.99,  1.0),
    ('7891001000017', '22.222.222/0001-22', 'ana@cestajusta.com',      9.29,  2.5),
    -- Sabonete 85g
    ('7891001000018', '22.222.222/0001-22', 'ana@cestajusta.com',      1.79, -0.5),
    ('7891001000018', '66.666.666/0001-66', 'bruno@cestajusta.com',    1.99,  0.0),
    ('7891001000018', '83.646.984/0001-12', 'maria@angeloni.com',      2.29,  0.5),
    ('7891001000018', '33.333.333/0001-33', 'carla@cestajusta.com',    1.89, -1.0)
) AS base(codigo_barras, cnpj, email, valor_base, tendencia)
INNER JOIN produtos p ON p.codigo_barras = base.codigo_barras
INNER JOIN mercados m ON m.cnpj = base.cnpj
INNER JOIN usuarios u ON u.email = base.email
CROSS JOIN generate_series(0, 27, 3) AS gs(day_offset)
WHERE NOT EXISTS (SELECT 1 FROM precos LIMIT 1);

-- Índice parcial para o relay da outbox (idempotente)
CREATE INDEX IF NOT EXISTS idx_outbox_pendente_parcial ON outbox (criado_em) WHERE enviado = false;
