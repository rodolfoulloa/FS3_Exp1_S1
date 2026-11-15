package cl.duoc.biblioteca.service;

import cl.duoc.biblioteca.model.Libro;
import cl.duoc.biblioteca.repository.LibroRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Servicio de libros. Hereda el CRUD completo de {@link AbstractCrudService}
 * (Template Method) y solo define los pasos propios de la entidad.
 *
 * Patrón SINGLETON: {@code @Service} registra una única instancia en el
 * contenedor de Spring, compartida por toda la aplicación.
 * Inyección de dependencias: el repositorio y el publicador de eventos llegan
 * por el constructor.
 */
@Service
public class LibroService extends AbstractCrudService<Libro, Long> {

    public LibroService(LibroRepository repositorio, ApplicationEventPublisher publicadorEventos) {
        super(repositorio, publicadorEventos);
    }

    @Override
    protected String nombreRecurso() {
        return "libro";
    }

    @Override
    protected void copiarDatos(Libro origen, Libro destino) {
        destino.setTitulo(origen.getTitulo());
        destino.setAutor(origen.getAutor());
        destino.setAnioPublicacion(origen.getAnioPublicacion());
        destino.setGenero(origen.getGenero());
    }
}
