#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.factory;

import ${package}.dto.${entidad}Request;
import ${package}.dto.${entidad}Response;
import ${package}.model.${entidad};
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Patrón FACTORY: centraliza la creación de objetos {@link ${entidad}} y
 * {@link ${entidad}Response}. El controlador no usa {@code new} ni conoce cómo se
 * arma cada objeto; si cambia la forma de construirlos, solo cambia esta clase.
 */
@Component
public class ${entidad}Factory {

    /** Crea la entidad a partir de los datos recibidos (usa el patrón Builder). */
    public ${entidad} crearEntidad(${entidad}Request request) {
        return ${entidad}.builder()
                .nombre(request.nombre().trim())
                .build();
    }

    /** Crea la respuesta JSON a partir de la entidad. */
    public ${entidad}Response crearRespuesta(${entidad} entidad) {
        return new ${entidad}Response(entidad.getId(), entidad.getNombre());
    }

    /** Crea la lista de respuestas a partir de una lista de entidades. */
    public List<${entidad}Response> crearRespuestas(List<${entidad}> entidades) {
        return entidades.stream().map(this::crearRespuesta).toList();
    }
}
