package cl.duoc.biblioteca;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.biblioteca.controller.LibroController;
import cl.duoc.biblioteca.exception.LibroNoEncontradoException;
import cl.duoc.biblioteca.model.Libro;
import cl.duoc.biblioteca.service.LibroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas del controlador sin conectarse a Oracle (el servicio se simula con Mockito).
 */
@WebMvcTest(LibroController.class)
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibroService libroService;

    private Libro libroEjemplo() {
        Libro l = new Libro("Clean Code", "Robert C. Martin", 2008, "Tecnología");
        l.setId(1L);
        return l;
    }

    private static final String JSON = """
            {"titulo":"Clean Code","autor":"Robert C. Martin","anioPublicacion":2008,"genero":"Tecnología"}
            """;

    @Test
    void getPorId_devuelveLibro() throws Exception {
        when(libroService.obtenerLibroPorId(1L)).thenReturn(libroEjemplo());
        mockMvc.perform(get("/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void getPorId_inexistente_devuelve404() throws Exception {
        when(libroService.obtenerLibroPorId(99L)).thenThrow(new LibroNoEncontradoException(99L));
        mockMvc.perform(get("/libros/99")).andExpect(status().isNotFound());
    }

    @Test
    void post_creaLibro() throws Exception {
        when(libroService.crearLibro(any(Libro.class))).thenReturn(libroEjemplo());
        mockMvc.perform(post("/libros").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void post_sinTitulo_devuelve400() throws Exception {
        mockMvc.perform(post("/libros").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"autor\":\"X\",\"anioPublicacion\":2000,\"genero\":\"Y\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void put_actualizaLibro() throws Exception {
        when(libroService.actualizarLibro(eq(1L), any(Libro.class))).thenReturn(libroEjemplo());
        mockMvc.perform(put("/libros/1").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_eliminaLibro() throws Exception {
        mockMvc.perform(delete("/libros/1")).andExpect(status().isNoContent());
    }
}
