#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejo centralizado de errores: transforma las excepciones en respuestas
 * JSON con el código HTTP correspondiente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 cuando la entidad no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /** 400 cuando el JSON no cumple las validaciones del DTO. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    /** 400 cuando el body no es un JSON válido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido", null);
    }

    /** 400 cuando el ID de la URL no es un número (ej: /recurso/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' no es válido", null);
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String mensaje, Map<String, String> detalle) {
        ErrorResponse cuerpo = ErrorResponse.builder()
                .status(status.value())
                .error(mensaje)
                .detalle(detalle)
                .build();
        return ResponseEntity.status(status).body(cuerpo);
    }
}
