package org.furb.services;

import org.furb.dto.denuncia.DenunciaCadastroDTO;
import org.furb.enums.StatusDenuncia;
import org.furb.model.Denuncia;
import org.furb.model.Mercado;
import org.furb.model.Preco;
import org.furb.model.Usuario;
import org.furb.repositories.DenunciaRepository;
import org.furb.repositories.MercadoComercianteRepository;
import org.furb.repositories.PrecoRepository;
import org.furb.repositories.VotoDenunciaRepository;
import org.furb.security.UsuarioAutenticadoProvider;
import org.furb.services.exeptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.furb.dto.denuncia.DenunciaResponseDTO;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DenunciaServiceTest {

    @Mock private DenunciaRepository denunciaRepository;
    @Mock private VotoDenunciaRepository votoDenunciaRepository;
    @Mock private PrecoRepository precoRepository;
    @Mock private MercadoComercianteRepository mercadoComercianteRepository;
    @Mock private UsuarioAutenticadoProvider usuarioAutenticadoProvider;
    @Mock private org.furb.storage.FotoStorage fotoStorage;
    @Mock private org.furb.outbox.OutboxService outboxService;

    private DenunciaService service;

    private Preco preco;
    private Usuario denunciante;
    private Mercado mercado;

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-03T12:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        service = new DenunciaService(denunciaRepository, votoDenunciaRepository, precoRepository,
                mercadoComercianteRepository, usuarioAutenticadoProvider, clock, fotoStorage, outboxService);

        mercado = new Mercado();
        setId(mercado, 5L);

        preco = new Preco();
        preco.setMercado(mercado);
        setId(preco, 10L);

        denunciante = new Usuario();
        denunciante.setNome("Denunciante");
        setId(denunciante, 1L);
    }

    private static void setId(Object entity, Long id) {
        try {
            var f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DenunciaCadastroDTO dto() {
        DenunciaCadastroDTO dto = new DenunciaCadastroDTO();
        dto.setPrecoId(10L);
        dto.setMotivo("Preço muito acima do mercado");
        return dto;
    }

    @Test
    void criar_salvaComStatusPendente() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(denunciante);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(denunciaRepository.existsByUsuarioIdAndPrecoIdAndDataCriacaoAfter(eq(1L), eq(10L), any()))
                .thenReturn(false);
        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(i -> i.getArgument(0));

        Denuncia criada = service.criar(dto(), null);

        assertThat(criada.getStatus()).isEqualTo(StatusDenuncia.PENDENTE);
        assertThat(criada.getMotivo()).isEqualTo("Preço muito acima do mercado");
    }

    @Test
    void criar_dentroDoIntervaloAntiSpam_lancaBusinessException() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(denunciante);
        when(precoRepository.findById(10L)).thenReturn(Optional.of(preco));
        when(denunciaRepository.existsByUsuarioIdAndPrecoIdAndDataCriacaoAfter(eq(1L), eq(10L), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.criar(dto(), null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("3 dias");

        verify(denunciaRepository, never()).save(any());
    }

    private Denuncia denunciaPendente(Usuario autorDenuncia) {
        Denuncia d = new Denuncia();
        d.setPreco(preco);
        d.setUsuario(autorDenuncia);
        d.setStatus(StatusDenuncia.PENDENTE);
        setId(d, 100L);
        return d;
    }

    @Test
    void votar_denunciante_naoPodeVotar() {
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(denunciante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(denunciaPendente(denunciante)));

        assertThatThrownBy(() -> service.votar(100L, org.furb.enums.TipoVoto.CONFIRMA))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("denunciante");
    }

    @Test
    void votar_donoDoMercado_naoPodeVotar() {
        Usuario dono = new Usuario();
        setId(dono, 9L);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(dono);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(denunciaPendente(denunciante)));
        when(mercadoComercianteRepository.existsByMercadoIdAndComercianteId(5L, 9L)).thenReturn(true);

        assertThatThrownBy(() -> service.votar(100L, org.furb.enums.TipoVoto.CONFIRMA))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mercado");
    }

    @Test
    void votar_votoJaRegistrado_lancaBusinessException() {
        Usuario votante = new Usuario();
        setId(votante, 7L);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(votante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(denunciaPendente(denunciante)));
        when(mercadoComercianteRepository.existsByMercadoIdAndComercianteId(5L, 7L)).thenReturn(false);
        when(votoDenunciaRepository.existsByDenunciaIdAndUsuarioId(100L, 7L)).thenReturn(true);

        assertThatThrownBy(() -> service.votar(100L, org.furb.enums.TipoVoto.CONFIRMA))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já votou");

        verify(votoDenunciaRepository, never()).save(any());
    }

    @Test
    void votar_terceiroConfirma_aprovaEDenunciaRejeitaPreco() {
        Usuario votante = new Usuario();
        setId(votante, 7L);
        Denuncia d = denunciaPendente(denunciante);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(votante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(d));
        when(mercadoComercianteRepository.existsByMercadoIdAndComercianteId(5L, 7L)).thenReturn(false);
        when(votoDenunciaRepository.existsByDenunciaIdAndUsuarioId(100L, 7L)).thenReturn(false);
        when(votoDenunciaRepository.countByDenunciaIdAndTipo(100L, org.furb.enums.TipoVoto.CONFIRMA)).thenReturn(3L);

        service.votar(100L, org.furb.enums.TipoVoto.CONFIRMA);

        assertThat(d.getStatus()).isEqualTo(StatusDenuncia.APROVADA);
        assertThat(d.getResolvidoPor()).isEqualTo(org.furb.enums.OrigemResolucao.SISTEMA);
        assertThat(preco.getStatus()).isEqualTo(org.furb.enums.StatusPreco.REJEITADO);
        verify(precoRepository).save(preco);
    }

    @Test
    void votar_terceiroRejeita_rejeitaDenunciaSemAfetarPreco() {
        Usuario votante = new Usuario();
        setId(votante, 8L);
        Denuncia d = denunciaPendente(denunciante);
        preco.setStatus(org.furb.enums.StatusPreco.PENDENTE);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(votante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(d));
        when(mercadoComercianteRepository.existsByMercadoIdAndComercianteId(5L, 8L)).thenReturn(false);
        when(votoDenunciaRepository.existsByDenunciaIdAndUsuarioId(100L, 8L)).thenReturn(false);
        when(votoDenunciaRepository.countByDenunciaIdAndTipo(100L, org.furb.enums.TipoVoto.REJEITA)).thenReturn(3L);

        service.votar(100L, org.furb.enums.TipoVoto.REJEITA);

        assertThat(d.getStatus()).isEqualTo(StatusDenuncia.REJEITADA);
        assertThat(preco.getStatus()).isEqualTo(org.furb.enums.StatusPreco.PENDENTE);
    }

    @Test
    void cancelar_peloDenunciante_marcaCancelada() {
        Denuncia d = denunciaPendente(denunciante);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(denunciante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(d));
        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(i -> i.getArgument(0));

        service.cancelar(100L);

        assertThat(d.getStatus()).isEqualTo(StatusDenuncia.CANCELADA);
        assertThat(d.getResolvidoPor()).isEqualTo(org.furb.enums.OrigemResolucao.PROPRIO_DENUNCIANTE);
    }

    @Test
    void cancelar_porOutroUsuario_lancaBusinessException() {
        Usuario outro = new Usuario();
        setId(outro, 42L);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(outro);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(denunciaPendente(denunciante)));

        assertThatThrownBy(() -> service.cancelar(100L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void resolverComoAdmin_aprovada_rejeitaPreco() {
        Denuncia d = denunciaPendente(denunciante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(d));
        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(i -> i.getArgument(0));

        service.resolverComoAdmin(100L, StatusDenuncia.APROVADA);

        assertThat(d.getStatus()).isEqualTo(StatusDenuncia.APROVADA);
        assertThat(d.getResolvidoPor()).isEqualTo(org.furb.enums.OrigemResolucao.ADMIN);
        assertThat(preco.getStatus()).isEqualTo(org.furb.enums.StatusPreco.REJEITADO);
    }

    @Test
    void resolverComoAdmin_statusInvalido_lancaBusinessException() {
        assertThatThrownBy(() -> service.resolverComoAdmin(100L, StatusDenuncia.PENDENTE))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void retirarVoto_existente_remove() {
        Usuario votante = new Usuario();
        setId(votante, 7L);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(votante);
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(denunciaPendente(denunciante)));
        when(votoDenunciaRepository.existsByDenunciaIdAndUsuarioId(100L, 7L)).thenReturn(true);

        service.retirarVoto(100L);

        verify(votoDenunciaRepository).deleteByDenunciaIdAndUsuarioId(100L, 7L);
    }

    @Test
    void expirarPendentes_rejeitaDenunciasAntigas() {
        Denuncia antiga = denunciaPendente(denunciante);
        when(denunciaRepository.findByStatusAndDataCriacaoBefore(eq(StatusDenuncia.PENDENTE), any()))
                .thenReturn(java.util.List.of(antiga));
        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(i -> i.getArgument(0));

        int total = service.expirarPendentes();

        assertThat(total).isEqualTo(1);
        assertThat(antiga.getStatus()).isEqualTo(StatusDenuncia.REJEITADA);
        assertThat(antiga.getResolvidoPor()).isEqualTo(org.furb.enums.OrigemResolucao.SISTEMA);
    }

    @Test
    void buscarPorId_retornaDtoComContagens() {
        Denuncia d = denunciaPendente(denunciante);
        d.setMotivo("abusivo");
        when(denunciaRepository.findById(100L)).thenReturn(Optional.of(d));
        when(votoDenunciaRepository.countByDenunciaIdAndTipo(100L, org.furb.enums.TipoVoto.CONFIRMA)).thenReturn(2L);
        when(votoDenunciaRepository.countByDenunciaIdAndTipo(100L, org.furb.enums.TipoVoto.REJEITA)).thenReturn(1L);

        DenunciaResponseDTO dto = service.buscarPorId(100L);

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getPrecoId()).isEqualTo(10L);
        assertThat(dto.getDenuncianteId()).isEqualTo(1L);
        assertThat(dto.getMotivo()).isEqualTo("abusivo");
        assertThat(dto.getStatus()).isEqualTo(StatusDenuncia.PENDENTE);
        assertThat(dto.getVotosConfirma()).isEqualTo(2L);
        assertThat(dto.getVotosRejeita()).isEqualTo(1L);
    }

    @Test
    void listarPorPreco_retornaLista() {
        Denuncia d = denunciaPendente(denunciante);
        d.setMotivo("preco errado");
        when(denunciaRepository.findByPrecoId(10L)).thenReturn(List.of(d));
        when(votoDenunciaRepository.countByDenunciaIdAndTipo(100L, org.furb.enums.TipoVoto.CONFIRMA)).thenReturn(0L);
        when(votoDenunciaRepository.countByDenunciaIdAndTipo(100L, org.furb.enums.TipoVoto.REJEITA)).thenReturn(0L);

        List<DenunciaResponseDTO> lista = service.listarPorPreco(10L);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getStatus()).isEqualTo(StatusDenuncia.PENDENTE);
        assertThat(lista.get(0).getPrecoId()).isEqualTo(10L);
    }
}
