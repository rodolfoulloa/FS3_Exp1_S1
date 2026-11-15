package cl.duoc.biblioteca.dto;

/**
 * DTO de salida: el JSON que devuelve el microservicio. Mantiene el mismo
 * contrato de la semana 1 (id, titulo, autor, anioPublicacion, genero), por lo
 * que el frontend Angular de la semana 3 sigue funcionando sin cambios.
 */
public record LibroResponse(Long id, String titulo, String autor, Integer anioPublicacion, String genero) {
}
