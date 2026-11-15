package cl.duoc.biblioteca.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.biblioteca.exception.RecursoNoEncontradoException;
import cl.duoc.biblioteca.factory.LibroFactory;
import cl.duoc.biblioteca.model.Libro;
import cl.duoc.biblioteca.service.LibroService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas de los 5 endpoints de /libros sin conectarse a Oracle: el servicio se simula con Mockito.
 */
@WebMvcTest(LibroController.class)
@Import(LibroFactory.class)
class LibroControllerTest {

    private static final String JSON_VALIDO = """
            {"titulo":"Clean Code","autor":"Robert C. Martin","anioPublicacion":2008,"genero":"Tecnología"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibroService servicio;

    private Libro cleanCode() {
        return Libro.builder()
                .id(1L)
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .anioPublicacion(2008)
                .genero("Tecnología")
                .build();
    }

    @Test
    void listar_devuelve200ConLaLista() throws Exception {
        when(servicio.listar()).thenReturn(List.of(cleanCode()));

        mockMvc.perform(get("/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                .andExpect(jsonPath("$[0].autor").value("Robert C. Martin"))
                .andExpect(jsonPath("$[0].anioPublicacion").value(2008))
                .andExpect(jsonPath("$[0].genero").value("Tecnología"));
    }

    @Test
    void obtenerPorId_devuelve200() throws Exception {
        when(servicio.obtenerPorId(1L)).thenReturn(cleanCode());

        mockMvc.perform(get("/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void obtenerPorId_inexistente_devuelve404() throws Exception {
        when(servicio.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("libro", 99L));

        mockMvc.perform(get("/libros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("No existe un libro con ID 99"));
    }

    @Test
    void obtenerPorId_conIdNoNumerico_devuelve400() throws Exception {
        mockMvc.perform(get("/libros/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void crear_devuelve201ConLocation() throws Exception {
        when(servicio.crear(any(Libro.class))).thenReturn(cleanCode());

        mockMvc.perform(post("/libros").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/libros/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_conCamposVacios_devuelve400ConDetallePorCampo() throws Exception {
        mockMvc.perform(post("/libros").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Datos inválidos"))
                .andExpect(jsonPath("$.detalle.titulo").value("El título es obligatorio"))
                .andExpect(jsonPath("$.detalle.autor").value("El autor es obligatorio"))
                .andExpect(jsonPath("$.detalle.anioPublicacion").value("El año de publicación es obligatorio"))
                .andExpect(jsonPath("$.detalle.genero").value("El género es obligatorio"));
    }

    @Test
    void crear_conAnioFueraDeRango_devuelve400() throws Exception {
        String json = """
                {"titulo":"Futuro","autor":"Anónimo","anioPublicacion":2500,"genero":"Ficción"}
                """;

        mockMvc.perform(post("/libros").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalle.anioPublicacion").value("El año de publicación no es válido"));
    }

    @Test
    void crear_conJsonMalformado_devuelve400() throws Exception {
        mockMvc.perform(post("/libros").contentType(MediaType.APPLICATION_JSON).content("{titulo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_devuelve200() throws Exception {
        when(servicio.actualizar(eq(1L), any(Libro.class))).thenReturn(cleanCode());

        mockMvc.perform(put("/libros/1").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizar_inexistente_devuelve404() throws Exception {
        when(servicio.actualizar(eq(99L), any(Libro.class)))
                .thenThrow(new RecursoNoEncontradoException("libro", 99L));

        mockMvc.perform(put("/libros/99").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/libros/1")).andExpect(status().isNoContent());
        verify(servicio).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_devuelve404() throws Exception {
        doThrow(new RecursoNoEncontradoException("libro", 99L)).when(servicio).eliminar(99L);

        mockMvc.perform(delete("/libros/99")).andExpect(status().isNotFound());
    }
}
