#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada (body de POST y PUT). Contiene las validaciones de los datos
 * que envía el cliente; el ID no se recibe en el body.
 */
public record ItemRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre admite hasta 200 caracteres")
        String nombre) {
}
