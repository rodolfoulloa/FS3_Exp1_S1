#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

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

import ${package}.exception.RecursoNoEncontradoException;
import ${package}.factory.ItemFactory;
import ${package}.model.Item;
import ${package}.service.ItemService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas de los 5 endpoints sin conectarse a Oracle: el servicio se simula con Mockito.
 */
@WebMvcTest(ItemController.class)
@Import(ItemFactory.class)
class ItemControllerTest {

    private static final String JSON_VALIDO = """
            {"nombre":"Ejemplo"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService servicio;

    private Item ejemplo() {
        return Item.builder().id(1L).nombre("Ejemplo").build();
    }

    @Test
    void listar_devuelve200ConLaLista() throws Exception {
        when(servicio.listar()).thenReturn(List.of(ejemplo()));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("${symbol_dollar}[0].id").value(1))
                .andExpect(jsonPath("${symbol_dollar}[0].nombre").value("Ejemplo"));
    }

    @Test
    void obtenerPorId_devuelve200() throws Exception {
        when(servicio.obtenerPorId(1L)).thenReturn(ejemplo());

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("${symbol_dollar}.nombre").value("Ejemplo"));
    }

    @Test
    void obtenerPorId_inexistente_devuelve404() throws Exception {
        when(servicio.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("item", 99L));

        mockMvc.perform(get("/items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("${symbol_dollar}.error").value("No existe un item con ID 99"));
    }

    @Test
    void obtenerPorId_conIdNoNumerico_devuelve400() throws Exception {
        mockMvc.perform(get("/items/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void crear_devuelve201ConLocation() throws Exception {
        when(servicio.crear(any(Item.class))).thenReturn(ejemplo());

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/items/1"))
                .andExpect(jsonPath("${symbol_dollar}.id").value(1));
    }

    @Test
    void crear_conDatosInvalidos_devuelve400ConDetalle() throws Exception {
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON).content("{${symbol_escape}"nombre${symbol_escape}":${symbol_escape}"${symbol_escape}"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("${symbol_dollar}.error").value("Datos inválidos"))
                .andExpect(jsonPath("${symbol_dollar}.detalle.nombre").exists());
    }

    @Test
    void crear_conJsonMalformado_devuelve400() throws Exception {
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON).content("{nombre"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_devuelve200() throws Exception {
        when(servicio.actualizar(eq(1L), any(Item.class))).thenReturn(ejemplo());

        mockMvc.perform(put("/items/1").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("${symbol_dollar}.id").value(1));
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/items/1")).andExpect(status().isNoContent());
        verify(servicio).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_devuelve404() throws Exception {
        doThrow(new RecursoNoEncontradoException("item", 99L)).when(servicio).eliminar(99L);

        mockMvc.perform(delete("/items/99")).andExpect(status().isNotFound());
    }
}
