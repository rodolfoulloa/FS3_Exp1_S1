package cl.duoc.biblioteca.service;

import cl.duoc.biblioteca.exception.LibroNoEncontradoException;
import cl.duoc.biblioteca.model.Libro;
import cl.duoc.biblioteca.repository.LibroRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Libro obtenerLibroPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new LibroNoEncontradoException(id));
    }

    @Override
    @Transactional
    public Libro crearLibro(Libro libro) {
        libro.setId(null); // el ID lo genera la base de datos
        return libroRepository.save(libro);
    }

    @Override
    @Transactional
    public Libro actualizarLibro(Long id, Libro datos) {
        Libro existente = obtenerLibroPorId(id);
        existente.setTitulo(datos.getTitulo());
        existente.setAutor(datos.getAutor());
        existente.setAnioPublicacion(datos.getAnioPublicacion());
        existente.setGenero(datos.getGenero());
        return libroRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new LibroNoEncontradoException(id);
        }
        libroRepository.deleteById(id);
    }
}
