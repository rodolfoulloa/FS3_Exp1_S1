#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import ${package}.model.Item;
import ${package}.repository.ItemRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Servicio de Item. Hereda el CRUD completo de {@link AbstractCrudService}
 * (Template Method) y solo define los pasos propios de la entidad.
 *
 * Patrón SINGLETON: {@code @Service} registra una única instancia en el
 * contenedor de Spring, compartida por toda la aplicación.
 * Inyección de dependencias: el repositorio y el publicador de eventos llegan
 * por el constructor.
 */
@Service
public class ItemService extends AbstractCrudService<Item, Long> {

    public ItemService(ItemRepository repositorio, ApplicationEventPublisher publicadorEventos) {
        super(repositorio, publicadorEventos);
    }

    @Override
    protected String nombreRecurso() {
        return "item";
    }

    @Override
    protected void copiarDatos(Item origen, Item destino) {
        destino.setNombre(origen.getNombre());
    }
}
