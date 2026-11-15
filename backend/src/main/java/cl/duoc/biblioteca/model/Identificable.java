package cl.duoc.biblioteca.model;

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
