#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.factory;

import static org.assertj.core.api.Assertions.assertThat;

import ${package}.dto.${entidad}Request;
import ${package}.dto.${entidad}Response;
import ${package}.model.${entidad};
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas del patrón Factory (y del Builder que usa internamente). */
class ${entidad}FactoryTest {

    private final ${entidad}Factory factory = new ${entidad}Factory();

    @Test
    void crearEntidad_construyeLaEntidadSinIdYConTextosLimpios() {
        ${entidad} entidad = factory.crearEntidad(new ${entidad}Request("  Ejemplo  "));

        assertThat(entidad.getId()).isNull();
        assertThat(entidad.getNombre()).isEqualTo("Ejemplo");
    }

    @Test
    void crearRespuesta_copiaTodosLosAtributos() {
        ${entidad} entidad = ${entidad}.builder().id(5L).nombre("Ejemplo").build();

        assertThat(factory.crearRespuesta(entidad)).isEqualTo(new ${entidad}Response(5L, "Ejemplo"));
    }

    @Test
    void crearRespuestas_convierteCadaElemento() {
        List<${entidad}Response> respuestas = factory.crearRespuestas(List.of(
                ${entidad}.builder().id(1L).nombre("A").build(),
                ${entidad}.builder().id(2L).nombre("B").build()));

        assertThat(respuestas).extracting(${entidad}Response::id).containsExactly(1L, 2L);
    }
}
