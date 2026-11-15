package cl.duoc.biblioteca.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cuerpo JSON de las respuestas de error.
 *
 * Patrón BUILDER: {@code ErrorResponse.builder().status(404).error("...").build()}
 * arma la respuesta paso a paso; {@code detalle} es opcional y solo aparece
 * en los errores de validación.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String timestamp, int status, String error, Map<String, String> detalle) {

    public static Builder builder() {
        return new Builder();
    }

    /** Patrón Builder: construye un {@link ErrorResponse} paso a paso. */
    public static final class Builder {
        private String timestamp = LocalDateTime.now().toString();
        private int status;
        private String error;
        private Map<String, String> detalle;

        private Builder() {
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder detalle(Map<String, String> detalle) {
            this.detalle = detalle;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(timestamp, status, error, detalle);
        }
    }
}
