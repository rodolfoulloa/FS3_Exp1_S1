#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import ${package}.dto.${entidad}Request;
import ${package}.dto.${entidad}Response;
import ${package}.factory.${entidad}Factory;
import ${package}.model.${entidad};
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
 * Controlador REST con los 5 endpoints del CRUD en /${recurso}.
 *
 * Depende de la interfaz {@link CrudService} (no de la implementación) y usa
 * {@link ${entidad}Factory} para convertir entre DTOs y entidades.
 */
@RestController
@RequestMapping("/${recurso}")
public class ${entidad}Controller {

    private final CrudService<${entidad}, Long> servicio;
    private final ${entidad}Factory factory;

    public ${entidad}Controller(CrudService<${entidad}, Long> servicio, ${entidad}Factory factory) {
        this.servicio = servicio;
        this.factory = factory;
    }

    /** GET /${recurso}: lista todos. */
    @GetMapping
    public List<${entidad}Response> listar() {
        return factory.crearRespuestas(servicio.listar());
    }

    /** GET /${recurso}/{id}: obtiene uno por su ID. */
    @GetMapping("/{id}")
    public ${entidad}Response obtenerPorId(@PathVariable Long id) {
        return factory.crearRespuesta(servicio.obtenerPorId(id));
    }

    /** POST /${recurso}: crea uno nuevo (201 Created). */
    @PostMapping
    public ResponseEntity<${entidad}Response> crear(@Valid @RequestBody ${entidad}Request request) {
        ${entidad} creado = servicio.crear(factory.crearEntidad(request));
        return ResponseEntity.created(URI.create("/${recurso}/" + creado.getId()))
                .body(factory.crearRespuesta(creado));
    }

    /** PUT /${recurso}/{id}: actualiza uno existente. */
    @PutMapping("/{id}")
    public ${entidad}Response actualizar(@PathVariable Long id, @Valid @RequestBody ${entidad}Request request) {
        return factory.crearRespuesta(servicio.actualizar(id, factory.crearEntidad(request)));
    }

    /** DELETE /${recurso}/{id}: elimina uno (204 No Content). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
