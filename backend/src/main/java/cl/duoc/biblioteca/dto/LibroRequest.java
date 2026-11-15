package cl.duoc.biblioteca.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada (body de POST y PUT) con las mismas validaciones de la semana 1.
 * El ID no se recibe en el body: lo genera Oracle o viene en la URL.
 */
public record LibroRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título admite hasta 200 caracteres")
        String titulo,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 150, message = "El autor admite hasta 150 caracteres")
        String autor,

        @NotNull(message = "El año de publicación es obligatorio")
        @Min(value = 0, message = "El año de publicación no es válido")
        @Max(value = 2100, message = "El año de publicación no es válido")
        Integer anioPublicacion,

        @NotBlank(message = "El género es obligatorio")
        @Size(max = 80, message = "El género admite hasta 80 caracteres")
        String genero) {
}
