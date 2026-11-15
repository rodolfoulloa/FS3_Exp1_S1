#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.event;

/** Tipos de cambio que se notifican a los observadores. */
public enum TipoEvento {
    CREADO,
    ACTUALIZADO,
    ELIMINADO
}
