#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dto;

/**
 * DTO de salida: lo que el microservicio devuelve en formato JSON.
 * Separa el contrato de la API de la entidad JPA.
 */
public record ${entidad}Response(Long id, String nombre) {
}
