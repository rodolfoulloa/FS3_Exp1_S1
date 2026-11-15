package cl.duoc.biblioteca.controller;

import cl.duoc.biblioteca.dto.LibroRequest;
import cl.duoc.biblioteca.dto.LibroResponse;
import cl.duoc.biblioteca.factory.LibroFactory;
import cl.duoc.biblioteca.model.Libro;
import cl.duoc.biblioteca.service.CrudService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST con los 5 endpoints del CRUD en /libros.
 *
 * Depende de la interfaz {@link CrudService} (no de la implementación) y usa
 * {@link LibroFactory} para convertir entre DTOs y entidades.
 */
@RestController
@RequestMapping("/libros")
public class LibroController {

    private final CrudService<Libro, Long> servicio;
    private final LibroFactory factory;

    public LibroController(CrudService<Libro, Long> servicio, LibroFactory factory) {
        this.servicio = servicio;
        this.factory = factory;
    }

    /** GET /libros: lista todos. */
    @GetMapping
    public List<LibroResponse> listar() {
        return factory.crearRespuestas(servicio.listar());
    }

    /** GET /libros/{id}: obtiene uno por su ID. */
    @GetMapping("/{id}")
    public LibroResponse obtenerPorId(@PathVariable Long id) {
        return factory.crearRespuesta(servicio.obtenerPorId(id));
    }

    /** POST /libros: crea uno nuevo (201 Created). */
    @PostMapping
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        Libro creado = servicio.crear(factory.crearEntidad(request));
        return ResponseEntity.created(URI.create("/libros/" + creado.getId()))
                .body(factory.crearRespuesta(creado));
    }

    /** PUT /libros/{id}: actualiza uno existente. */
    @PutMapping("/{id}")
    public LibroResponse actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return factory.crearRespuesta(servicio.actualizar(id, factory.crearEntidad(request)));
    }

    /** DELETE /libros/{id}: elimina uno (204 No Content). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
