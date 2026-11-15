#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import ${package}.dto.ItemRequest;
import ${package}.dto.ItemResponse;
import ${package}.factory.ItemFactory;
import ${package}.model.Item;
import ${package}.service.CrudService;
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
 * Controlador REST con los 5 endpoints del CRUD en /items.
 *
 * Depende de la interfaz {@link CrudService} (no de la implementación) y usa
 * {@link ItemFactory} para convertir entre DTOs y entidades.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final CrudService<Item, Long> servicio;
    private final ItemFactory factory;

    public ItemController(CrudService<Item, Long> servicio, ItemFactory factory) {
        this.servicio = servicio;
        this.factory = factory;
    }

    /** GET /items: lista todos. */
    @GetMapping
    public List<ItemResponse> listar() {
        return factory.crearRespuestas(servicio.listar());
    }

    /** GET /items/{id}: obtiene uno por su ID. */
    @GetMapping("/{id}")
    public ItemResponse obtenerPorId(@PathVariable Long id) {
        return factory.crearRespuesta(servicio.obtenerPorId(id));
    }

    /** POST /items: crea uno nuevo (201 Created). */
    @PostMapping
    public ResponseEntity<ItemResponse> crear(@Valid @RequestBody ItemRequest request) {
        Item creado = servicio.crear(factory.crearEntidad(request));
        return ResponseEntity.created(URI.create("/items/" + creado.getId()))
                .body(factory.crearRespuesta(creado));
    }

    /** PUT /items/{id}: actualiza uno existente. */
    @PutMapping("/{id}")
    public ItemResponse actualizar(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return factory.crearRespuesta(servicio.actualizar(id, factory.crearEntidad(request)));
    }

    /** DELETE /items/{id}: elimina uno (204 No Content). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
