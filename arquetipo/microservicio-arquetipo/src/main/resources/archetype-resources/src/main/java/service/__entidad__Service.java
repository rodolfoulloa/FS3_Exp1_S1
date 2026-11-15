#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import ${package}.model.${entidad};
import ${package}.repository.${entidad}Repository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Servicio de ${entidad}. Hereda el CRUD completo de {@link AbstractCrudService}
 * (Template Method) y solo define los pasos propios de la entidad.
 *
 * Patrón SINGLETON: {@code @Service} registra una única instancia en el
 * contenedor de Spring, compartida por toda la aplicación.
 * Inyección de dependencias: el repositorio y el publicador de eventos llegan
 * por el constructor.
 */
@Service
public class ${entidad}Service extends AbstractCrudService<${entidad}, Long> {

    public ${entidad}Service(${entidad}Repository repositorio, ApplicationEventPublisher publicadorEventos) {
        super(repositorio, publicadorEventos);
    }

    @Override
    protected String nombreRecurso() {
        return "${entidad.toLowerCase()}";
    }

    @Override
    protected void copiarDatos(${entidad} origen, ${entidad} destino) {
        destino.setNombre(origen.getNombre());
    }
}
