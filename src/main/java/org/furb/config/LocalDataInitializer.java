package org.furb.config;

import org.furb.enums.StatusPreco;
import org.furb.enums.TipoUsuario;
import org.furb.model.*;
import org.furb.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("local")
public class LocalDataInitializer implements CommandLineRunner {

    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;
    private final MercadoRepository mercados;
    private final ProdutoRepository produtos;
    private final PrecoRepository precos;
    private final MercadoComercianteRepository vinculos;

    public LocalDataInitializer(CategoriaRepository categorias, UsuarioRepository usuarios,
                                MercadoRepository mercados, ProdutoRepository produtos,
                                PrecoRepository precos, MercadoComercianteRepository vinculos) {
        this.categorias = categorias;
        this.usuarios = usuarios;
        this.mercados = mercados;
        this.produtos = produtos;
        this.precos = precos;
        this.vinculos = vinculos;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) return;

        String senha = new BCryptPasswordEncoder(10).encode("senha123");

        Categoria alimentos = categorias.save(cat("Alimentos"));
        Categoria bebidas   = categorias.save(cat("Bebidas"));
        Categoria higiene   = categorias.save(cat("Higiene e Limpeza"));
        Categoria frutas    = categorias.save(cat("Frutas e Verduras"));
        Categoria lacteos   = categorias.save(cat("Lácteos"));
        categorias.save(cat("Carnes e Peixes"));

        Usuario admin = usuarios.save(usuario("Admin Sistema",    "admin@cestajusta.com", senha, TipoUsuario.ADMIN));
        Usuario ana   = usuarios.save(usuario("Ana Souza",        "ana@cestajusta.com",   senha, TipoUsuario.CONSUMIDOR));
        Usuario bruno = usuarios.save(usuario("Bruno Lima",       "bruno@cestajusta.com", senha, TipoUsuario.CONSUMIDOR));
        Usuario carla = usuarios.save(usuario("Carla Rocha",      "carla@cestajusta.com", senha, TipoUsuario.CONSUMIDOR));
        Usuario joao  = usuarios.save(usuario("João Koch",        "joao@koch.com",        senha, TipoUsuario.COMERCIANTE));
        Usuario maria = usuarios.save(usuario("Maria Angeloni",   "maria@angeloni.com",   senha, TipoUsuario.COMERCIANTE));
        Usuario pedro = usuarios.save(usuario("Pedro Giassi",     "pedro@giassi.com",     senha, TipoUsuario.COMERCIANTE));

        Mercado koch     = mercados.save(mercado("Supermercado Koch",           "84.683.408/0001-03", "Blumenau", "SC"));
        Mercado angeloni = mercados.save(mercado("Angeloni Blumenau",           "83.646.984/0001-12", "Blumenau", "SC"));
        Mercado giassi   = mercados.save(mercado("Giassi Supermercados",        "79.084.053/0001-00", "Blumenau", "SC"));
        Mercado economico = mercados.save(mercado("Mercado Econômico Gaspar",   "12.345.678/0001-01", "Gaspar",   "SC"));
        Mercado popular  = mercados.save(mercado("Supermercado Popular Brusque","98.765.432/0001-02", "Brusque",  "SC"));

        vinculos.save(vinculo(koch,     joao,  admin));
        vinculos.save(vinculo(angeloni, maria, admin));
        vinculos.save(vinculo(giassi,   pedro, admin));

        Produto arroz      = produtos.save(produto("Arroz Branco 5kg",         "7891234567890", "Tio João",    "5kg",   alimentos));
        Produto feijao     = produtos.save(produto("Feijão Preto 1kg",         "7891111111111", "Camil",       "1kg",   alimentos));
        Produto acucar     = produtos.save(produto("Açúcar Cristal 1kg",       "7892222222222", "União",       "1kg",   alimentos));
        Produto cafe       = produtos.save(produto("Café Torrado 500g",        "7893333333333", "Pilão",       "500g",  alimentos));
        Produto oleo       = produtos.save(produto("Óleo de Soja 900ml",       "7894444444444", "Liza",        "900ml", alimentos));
        Produto macarrao   = produtos.save(produto("Macarrão Espaguete 500g",  "7895555555555", "Renata",      "500g",  alimentos));
        Produto leite      = produtos.save(produto("Leite Integral 1L",        "7896666666666", "Piracanjuba", "1L",    lacteos));
        Produto manteiga   = produtos.save(produto("Manteiga sem Sal 200g",    "7897777777777", "Aviação",     "200g",  lacteos));
        Produto queijo     = produtos.save(produto("Queijo Mussarela 500g",    "7898888888888", "Tirolez",     "500g",  lacteos));
        Produto refri      = produtos.save(produto("Refrigerante Cola 2L",     "7899999999999", "Coca-Cola",   "2L",    bebidas));
        Produto agua       = produtos.save(produto("Água Mineral 1,5L",        "7891010101010", "Crystal",     "1,5L",  bebidas));
        Produto suco       = produtos.save(produto("Suco de Laranja 1L",       "7891212121212", "Del Valle",   "1L",    bebidas));
        Produto banana     = produtos.save(produto("Banana Prata 1kg",         "7892020202020", null,          "1kg",   frutas));
        Produto tomate     = produtos.save(produto("Tomate 1kg",               "7893030303030", null,          "1kg",   frutas));
        Produto batata     = produtos.save(produto("Batata Inglesa 1kg",       "7893131313131", null,          "1kg",   frutas));
        Produto sabao      = produtos.save(produto("Sabão em Pó 1kg",          "7894040404040", "Omo",         "1kg",   higiene));
        Produto detergente = produtos.save(produto("Detergente Líquido 500ml", "7895050505050", "Ypê",         "500ml", higiene));
        Produto papel      = produtos.save(produto("Papel Higiênico 12 rolos", "7895151515151", "Neve",        "12un",  higiene));

        // Arroz
        precos.save(preco(arroz, koch,      joao,  24.90, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(arroz, angeloni,  maria, 27.40, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(arroz, giassi,    pedro, 31.50, "2026-04-19", StatusPreco.CONFIRMADO));
        precos.save(preco(arroz, economico, ana,   25.80, "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(arroz, popular,   bruno, 26.50, "2026-04-23", StatusPreco.PENDENTE));
        // Feijão
        precos.save(preco(feijao, koch,     joao,  8.90,  "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(feijao, angeloni, maria, 9.20,  "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(feijao, giassi,   carla, 10.50, "2026-04-18", StatusPreco.PENDENTE));
        // Açúcar
        precos.save(preco(acucar, koch,     joao,  4.89,  "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(acucar, angeloni, ana,   5.20,  "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(acucar, giassi,   pedro, 5.50,  "2026-04-19", StatusPreco.CONFIRMADO));
        // Café
        precos.save(preco(cafe, koch,     ana,   15.90, "2026-04-10", StatusPreco.DESATUALIZADO));
        precos.save(preco(cafe, koch,     joao,  16.50, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(cafe, angeloni, maria, 17.90, "2026-04-21", StatusPreco.CONFIRMADO));
        // Óleo
        precos.save(preco(oleo, koch,     joao,  7.90,  "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(oleo, angeloni, bruno, 25.00, "2026-04-22", StatusPreco.REJEITADO));
        precos.save(preco(oleo, angeloni, maria, 8.20,  "2026-04-23", StatusPreco.CONFIRMADO));
        precos.save(preco(oleo, giassi,   pedro, 8.80,  "2026-04-19", StatusPreco.CONFIRMADO));
        // Macarrão
        precos.save(preco(macarrao, koch,     ana,   3.49, "2026-04-20", StatusPreco.PENDENTE));
        precos.save(preco(macarrao, angeloni, maria, 3.79, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(macarrao, popular,  carla, 3.29, "2026-04-18", StatusPreco.PENDENTE));
        // Leite
        precos.save(preco(leite, koch,     joao,  5.49, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(leite, angeloni, maria, 5.99, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(leite, giassi,   pedro, 6.20, "2026-04-19", StatusPreco.CONFIRMADO));
        // Manteiga
        precos.save(preco(manteiga, koch,     joao, 12.90, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(manteiga, angeloni, ana,  13.50, "2026-04-22", StatusPreco.PENDENTE));
        // Queijo
        precos.save(preco(queijo, koch,     joao,  28.90, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(queijo, angeloni, maria, 31.50, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(queijo, giassi,   bruno, 30.00, "2026-04-23", StatusPreco.PENDENTE));
        // Refrigerante
        precos.save(preco(refri, koch,     ana,   9.99,  "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(refri, angeloni, maria, 10.49, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(refri, giassi,   pedro, 10.99, "2026-04-19", StatusPreco.CONFIRMADO));
        // Água
        precos.save(preco(agua, koch,      joao,  2.50, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(agua, economico, bruno, 1.99, "2026-04-22", StatusPreco.PENDENTE));
        // Suco
        precos.save(preco(suco, angeloni, maria, 8.90, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(suco, giassi,   carla, 9.50, "2026-04-23", StatusPreco.PENDENTE));
        // Banana
        precos.save(preco(banana, koch,     ana,   4.99, "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(banana, angeloni, maria, 5.49, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(banana, popular,  carla, 3.99, "2026-04-23", StatusPreco.PENDENTE));
        // Tomate
        precos.save(preco(tomate, koch,     bruno, 7.90, "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(tomate, angeloni, ana,   8.50, "2026-04-21", StatusPreco.PENDENTE));
        precos.save(preco(tomate, giassi,   pedro, 7.20, "2026-04-19", StatusPreco.CONFIRMADO));
        // Batata
        precos.save(preco(batata, koch,     joao, 5.99, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(batata, angeloni, ana,  6.50, "2026-04-22", StatusPreco.PENDENTE));
        // Sabão
        precos.save(preco(sabao, koch,     joao,  22.90, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(sabao, angeloni, maria, 24.50, "2026-04-21", StatusPreco.CONFIRMADO));
        precos.save(preco(sabao, giassi,   pedro, 26.00, "2026-04-19", StatusPreco.CONFIRMADO));
        // Detergente
        precos.save(preco(detergente, koch,     bruno, 3.29, "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(detergente, angeloni, maria, 3.49, "2026-04-21", StatusPreco.CONFIRMADO));
        // Papel Higiênico
        precos.save(preco(papel, koch,     joao,  24.90, "2026-04-20", StatusPreco.CONFIRMADO));
        precos.save(preco(papel, angeloni, ana,   26.50, "2026-04-22", StatusPreco.PENDENTE));
        precos.save(preco(papel, giassi,   carla, 28.00, "2026-04-23", StatusPreco.PENDENTE));
    }

    private Categoria cat(String nome) {
        Categoria c = new Categoria();
        c.setNome(nome);
        return c;
    }

    private Usuario usuario(String nome, String email, String senha, TipoUsuario tipo) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(senha);
        u.setTipoUsuario(tipo);
        return u;
    }

    private Mercado mercado(String nome, String cnpj, String cidade, String estado) {
        Mercado m = new Mercado();
        m.setNomeFantasia(nome);
        m.setCnpj(cnpj);
        m.setCidade(cidade);
        m.setEstado(estado);
        return m;
    }

    private MercadoComerciante vinculo(Mercado mercado, Usuario comerciante, Usuario vinculadoPor) {
        MercadoComerciante mc = new MercadoComerciante();
        mc.setMercado(mercado);
        mc.setComerciante(comerciante);
        mc.setVinculadoPor(vinculadoPor);
        return mc;
    }

    private Produto produto(String nome, String codigo, String marca, String unidade, Categoria categoria) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setCodigoBarras(codigo);
        p.setMarca(marca);
        p.setUnidadeMedida(unidade);
        p.setCategoria(categoria);
        return p;
    }

    private Preco preco(Produto produto, Mercado mercado, Usuario usuario,
                        double valor, String data, StatusPreco status) {
        Preco p = new Preco();
        p.setProduto(produto);
        p.setMercado(mercado);
        p.setUsuario(usuario);
        p.setValor(BigDecimal.valueOf(valor));
        p.setDataColeta(LocalDate.parse(data));
        p.setStatus(status);
        return p;
    }
}
