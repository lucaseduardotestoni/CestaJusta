INSERT INTO categorias (nome, ativo, data_criacao) VALUES
    ('Alimentos', true, NOW()),
    ('Bebidas', true, NOW()),
    ('Higiene e Limpeza', true, NOW()),
    ('Frutas e Verduras', true, NOW()),
    ('Lácteos', true, NOW()),
    ('Carnes e Peixes', true, NOW())
ON CONFLICT (nome) DO NOTHING;