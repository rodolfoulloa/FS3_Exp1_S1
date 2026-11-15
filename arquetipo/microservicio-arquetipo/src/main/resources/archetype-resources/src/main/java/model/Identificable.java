#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

/**
 * Contrato común de las entidades del microservicio: toda entidad tiene un ID.
 * Permite que {@code AbstractCrudService} trabaje con cualquier entidad.
 *
 * @param <ID> tipo del identificador
 */
public interface Identificable<ID> {

    ID getId();

    void setId(ID id);
}
