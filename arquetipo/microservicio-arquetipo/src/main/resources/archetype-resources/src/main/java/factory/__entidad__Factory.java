#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.factory;

import ${package}.dto.ItemRequest;
import ${package}.dto.ItemResponse;
import ${package}.model.Item;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Patrón FACTORY: centraliza la creación de objetos {@link Item} y
 * {@link ItemResponse}. El controlador no usa {@code new} ni conoce cómo se
 * arma cada objeto; si cambia la forma de construirlos, solo cambia esta clase.
 */
@Component
public class ItemFactory {

    /** Crea la entidad a partir de los datos recibidos (usa el patrón Builder). */
    public Item crearEntidad(ItemRequest request) {
        return Item.builder()
                .nombre(request.nombre().trim())
                .build();
    }

    /** Crea la respuesta JSON a partir de la entidad. */
    public ItemResponse crearRespuesta(Item item) {
        return new ItemResponse(item.getId(), item.getNombre());
    }

    /** Crea la lista de respuestas a partir de una lista de entidades. */
    public List<ItemResponse> crearRespuestas(List<Item> items) {
        return items.stream().map(this::crearRespuesta).toList();
    }
}
