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
    void crearEntidad_construyeElLibroSinIdYConTextosLimpios() {
        Libro libro = factory.crearEntidad(new LibroRequest("  Rayuela ", " Julio Cortázar ", 1963, " Novela "));

        assertThat(libro.getId()).isNull();
        assertThat(libro.getTitulo()).isEqualTo("Rayuela");
        assertThat(libro.getAutor()).isEqualTo("Julio Cortázar");
        assertThat(libro.getAnioPublicacion()).isEqualTo(1963);
        assertThat(libro.getGenero()).isEqualTo("Novela");
    }

    @Test
    void crearRespuesta_copiaLosCincoAtributos() {
        Libro libro = Libro.builder()
                .id(5L).titulo("Rayuela").autor("Julio Cortázar").anioPublicacion(1963).genero("Novela")
                .build();

        assertThat(factory.crearRespuesta(libro))
                .isEqualTo(new LibroResponse(5L, "Rayuela", "Julio Cortázar", 1963, "Novela"));
    }

    @Test
    void crearRespuestas_convierteCadaLibro() {
        List<LibroResponse> respuestas = factory.crearRespuestas(List.of(
                Libro.builder().id(1L).titulo("A").build(),
                Libro.builder().id(2L).titulo("B").build()));

        assertThat(respuestas).extracting(LibroResponse::titulo).containsExactly("A", "B");
    }
}
