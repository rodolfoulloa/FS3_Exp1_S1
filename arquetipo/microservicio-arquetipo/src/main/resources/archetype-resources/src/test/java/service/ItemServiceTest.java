#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ${package}.event.EventoEntidad;
import ${package}.event.TipoEvento;
import ${package}.exception.RecursoNoEncontradoException;
import ${package}.model.Item;
import ${package}.repository.ItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Pruebas del servicio: el algoritmo del Template Method y la publicación de
 * eventos para los observadores (Observer).
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repositorio;

    @Mock
    private ApplicationEventPublisher publicador;

    private ItemService servicio;

    @BeforeEach
    void setUp() {
        servicio = new ItemService(repositorio, publicador);
    }

    private EventoEntidad eventoPublicado() {
        ArgumentCaptor<EventoEntidad> captor = ArgumentCaptor.forClass(EventoEntidad.class);
        verify(publicador).publishEvent(captor.capture());
        return captor.getValue();
    }

    @Test
    void crear_ignoraElIdRecibido_guardaYPublicaEventoCreado() {
        Item nuevo = Item.builder().id(77L).nombre("Nuevo").build();
        when(repositorio.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            assertThat(i.getId()).isNull(); // el ID lo genera la BD
            i.setId(1L);
            return i;
        });

        Item creado = servicio.crear(nuevo);

        assertThat(creado.getId()).isEqualTo(1L);
        EventoEntidad evento = eventoPublicado();
        assertThat(evento.tipo()).isEqualTo(TipoEvento.CREADO);
        assertThat(evento.recurso()).isEqualTo("item");
        assertThat(evento.id()).isEqualTo(1L);
    }

    @Test
    void actualizar_copiaLosDatosSobreElExistente_yPublicaEventoActualizado() {
        Item existente = Item.builder().id(1L).nombre("Antes").build();
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorio.save(existente)).thenReturn(existente);

        Item resultado = servicio.actualizar(1L, Item.builder().nombre("Después").build());

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Después");
        assertThat(eventoPublicado().tipo()).isEqualTo(TipoEvento.ACTUALIZADO);
    }

    @Test
    void obtenerPorId_inexistente_lanza404() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe un item con ID 99");
    }

    @Test
    void eliminar_existente_borraYPublicaEventoEliminado() {
        when(repositorio.existsById(1L)).thenReturn(true);

        servicio.eliminar(1L);

        verify(repositorio).deleteById(1L);
        assertThat(eventoPublicado().tipo()).isEqualTo(TipoEvento.ELIMINADO);
    }

    @Test
    void eliminar_inexistente_lanza404YNoPublicaEventos() {
        when(repositorio.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> servicio.eliminar(99L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(repositorio, never()).deleteById(any());
        verify(publicador, never()).publishEvent(any(Object.class));
    }
}
