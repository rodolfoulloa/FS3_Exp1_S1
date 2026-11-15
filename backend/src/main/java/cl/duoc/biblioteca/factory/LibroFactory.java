package cl.duoc.biblioteca.factory;

import cl.duoc.biblioteca.dto.LibroRequest;
import cl.duoc.biblioteca.dto.LibroResponse;
import cl.duoc.biblioteca.model.Libro;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Patrón FACTORY: centraliza la creación de objetos {@link Libro} y
 * {@link LibroResponse}. El controlador no usa {@code new} ni conoce cómo se
 * arma cada objeto; si cambia la forma de construirlos, solo cambia esta clase.
 */
@Component
public class LibroFactory {

    /** Crea la entidad a partir de los datos recibidos (usa el patrón Builder). */
    public Libro crearEntidad(LibroRequest request) {
        return Libro.builder()
                .nombre(request.nombre().trim())
                .build();
    }

    /** Crea la respuesta JSON a partir de la entidad. */
    public LibroResponse crearRespuesta(Libro entidad) {
        return new LibroResponse(entidad.getId(), entidad.getNombre());
    }

    /** Crea la lista de respuestas a partir de una lista de entidades. */
    public List<LibroResponse> crearRespuestas(List<Libro> entidades) {
        return entidades.stream().map(this::crearRespuesta).toList();
    }
}
