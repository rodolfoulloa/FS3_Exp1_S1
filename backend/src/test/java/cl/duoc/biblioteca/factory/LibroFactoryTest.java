package cl.duoc.biblioteca.factory;

import static org.assertj.core.api.Assertions.assertThat;

import cl.duoc.biblioteca.dto.LibroRequest;
import cl.duoc.biblioteca.dto.LibroResponse;
import cl.duoc.biblioteca.model.Libro;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas del patrón Factory (y del Builder que usa internamente). */
class LibroFactoryTest {

    private final LibroFactory factory = new LibroFactory();

    @Test
    void crearEntidad_construyeLaEntidadSinIdYConTextosLimpios() {
        Libro entidad = factory.crearEntidad(new LibroRequest("  Ejemplo  "));

        assertThat(entidad.getId()).isNull();
        assertThat(entidad.getNombre()).isEqualTo("Ejemplo");
    }

    @Test
    void crearRespuesta_copiaTodosLosAtributos() {
        Libro entidad = Libro.builder().id(5L).nombre("Ejemplo").build();

        assertThat(factory.crearRespuesta(entidad)).isEqualTo(new LibroResponse(5L, "Ejemplo"));
    }

    @Test
    void crearRespuestas_convierteCadaElemento() {
        List<LibroResponse> respuestas = factory.crearRespuestas(List.of(
                Libro.builder().id(1L).nombre("A").build(),
                Libro.builder().id(2L).nombre("B").build()));

        assertThat(respuestas).extracting(LibroResponse::id).containsExactly(1L, 2L);
    }
}
