package cl.duoc.biblioteca.service;

import cl.duoc.biblioteca.model.Libro;
import java.util.List;

public interface LibroService {

    List<Libro> listarLibros();

    Libro obtenerLibroPorId(Long id);

    Libro crearLibro(Libro libro);

    Libro actualizarLibro(Long id, Libro libro);

    void eliminarLibro(Long id);
}
