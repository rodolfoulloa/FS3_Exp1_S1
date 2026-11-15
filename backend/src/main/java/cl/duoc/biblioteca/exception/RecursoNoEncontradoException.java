package cl.duoc.biblioteca.exception;

/** Se lanza cuando no existe una entidad con el ID solicitado (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("No existe un " + recurso + " con ID " + id);
    }
}
