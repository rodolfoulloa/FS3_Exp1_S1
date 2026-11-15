package cl.duoc.biblioteca.dto;

/**
 * DTO de salida: lo que el microservicio devuelve en formato JSON.
 * Separa el contrato de la API de la entidad JPA.
 */
public record LibroResponse(Long id, String nombre) {
}
