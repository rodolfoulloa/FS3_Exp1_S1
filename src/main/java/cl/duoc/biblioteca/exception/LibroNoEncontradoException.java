package cl.duoc.biblioteca.exception;

public class LibroNoEncontradoException extends RuntimeException {

    public LibroNoEncontradoException(Long id) {
        super("No existe un libro con ID " + id);
    }
}
