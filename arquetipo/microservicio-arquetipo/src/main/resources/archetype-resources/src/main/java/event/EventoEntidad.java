#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.event;

import java.time.LocalDateTime;

/**
 * Evento que publica el servicio cuando una entidad cambia (patrón Observer).
 *
 * @param tipo    CREADO, ACTUALIZADO o ELIMINADO
 * @param recurso nombre del recurso ("libro", "producto", ...)
 * @param id      ID de la entidad afectada
 * @param fecha   momento del cambio
 */
public record EventoEntidad(TipoEvento tipo, String recurso, Object id, LocalDateTime fecha) {

    /** Método fábrica: crea el evento con la fecha actual. */
    public static EventoEntidad de(TipoEvento tipo, String recurso, Object id) {
        return new EventoEntidad(tipo, recurso, id, LocalDateTime.now());
    }
}
