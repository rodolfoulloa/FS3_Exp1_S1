#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import java.util.List;

/**
 * Operaciones CRUD que ofrece todo servicio del microservicio.
 *
 * @param <T>  entidad
 * @param <ID> tipo del identificador
 */
public interface CrudService<T, ID> {

    List<T> listar();

    T obtenerPorId(ID id);

    T crear(T entidad);

    T actualizar(ID id, T datos);

    void eliminar(ID id);
}
