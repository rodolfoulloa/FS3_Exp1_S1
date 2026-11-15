package cl.duoc.biblioteca.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observador de los cambios en las entidades (patrón OBSERVER).
 *
 * Spring le entrega cada {@link EventoEntidad} que publica el servicio. Aquí se
 * registra una auditoría en el log y se guardan los últimos eventos en memoria.
 * Se pueden agregar más observadores (enviar un correo, actualizar una caché)
 * sin modificar el servicio.
 */
@Component
public class AuditoriaListener {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaListener.class);
    private static final int MAXIMO_EVENTOS = 50;

    private final List<EventoEntidad> ultimosEventos = Collections.synchronizedList(new ArrayList<>());

    @EventListener
    public void alCambiarEntidad(EventoEntidad evento) {
        log.info("[AUDITORIA] {} {} con ID {} ({})", evento.recurso(), evento.tipo(), evento.id(), evento.fecha());
        ultimosEventos.add(evento);
        if (ultimosEventos.size() > MAXIMO_EVENTOS) {
            ultimosEventos.remove(0);
        }
    }

    /** Últimos eventos recibidos (más reciente al final). */
    public List<EventoEntidad> getUltimosEventos() {
        synchronized (ultimosEventos) {
            return List.copyOf(ultimosEventos);
        }
    }
}
