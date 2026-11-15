#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.factory;

import static org.assertj.core.api.Assertions.assertThat;

import ${package}.dto.ItemRequest;
import ${package}.dto.ItemResponse;
import ${package}.model.Item;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas del patrón Factory (y del Builder que usa internamente). */
class ItemFactoryTest {

    private final ItemFactory factory = new ItemFactory();

    @Test
    void crearEntidad_construyeLaEntidadSinIdYConTextosLimpios() {
        Item item = factory.crearEntidad(new ItemRequest("  Ejemplo  "));

        assertThat(item.getId()).isNull();
        assertThat(item.getNombre()).isEqualTo("Ejemplo");
    }

    @Test
    void crearRespuesta_copiaTodosLosAtributos() {
        Item item = Item.builder().id(5L).nombre("Ejemplo").build();

        assertThat(factory.crearRespuesta(item)).isEqualTo(new ItemResponse(5L, "Ejemplo"));
    }

    @Test
    void crearRespuestas_convierteCadaElemento() {
        List<ItemResponse> respuestas = factory.crearRespuestas(List.of(
                Item.builder().id(1L).nombre("A").build(),
                Item.builder().id(2L).nombre("B").build()));

        assertThat(respuestas).extracting(ItemResponse::id).containsExactly(1L, 2L);
    }
}
