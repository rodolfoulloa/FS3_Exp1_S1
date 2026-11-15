package cl.duoc.biblioteca.service;

import cl.duoc.biblioteca.event.EventoEntidad;
import cl.duoc.biblioteca.event.TipoEvento;
import cl.duoc.biblioteca.exception.RecursoNoEncontradoException;
import cl.duoc.biblioteca.model.Identificable;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Patrón TEMPLATE METHOD: define el algoritmo de cada operación CRUD una sola
 * vez (buscar, validar, guardar, notificar) y deja a las subclases solo los
 * pasos que cambian según la entidad:
 * <ul>
 *   <li>{@link #nombreRecurso()}: nombre usado en los mensajes ("libro", "producto").</li>
 *   <li>{@link #copiarDatos(Object, Object)}: qué atributos se actualizan en un PUT.</li>
 *   <li>{@link #validarAntesDeGuardar(Object)}: reglas de negocio adicionales (opcional).</li>
 * </ul>
 *
 * Patrón OBSERVER: después de crear, actualizar o eliminar se publica un
 * {@link EventoEntidad}. Los observadores ({@code @EventListener}) reaccionan
 * sin que el servicio los conozca (ver {@code AuditoriaListener}).
 *
 * @param <T>  entidad
 * @param <ID> tipo del identificador
 */
public abstract class AbstractCrudService<T extends Identificable<ID>, ID> implements CrudService<T, ID> {

    private final JpaRepository<T, ID> repositorio;
    private final ApplicationEventPublisher publicadorEventos;

    protected AbstractCrudService(JpaRepository<T, ID> repositorio, ApplicationEventPublisher publicadorEventos) {
        this.repositorio = repositorio;
        this.publicadorEventos = publicadorEventos;
    }

    // ===== Pasos que define cada subclase =====

    /** Nombre del recurso en singular, para los mensajes de error y eventos. */
    protected abstract String nombreRecurso();

    /** Copia en {@code destino} los atributos editables de {@code origen} (PUT). */
    protected abstract void copiarDatos(T origen, T destino);

    /** Gancho opcional para reglas de negocio antes de guardar. Por defecto no hace nada. */
    protected void validarAntesDeGuardar(T entidad) {
        // Las subclases pueden sobrescribirlo.
    }

    // ===== Métodos biblioteca (algoritmo fijo) =====

    @Override
    @Transactional(readOnly = true)
    public List<T> listar() {
        return repositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public T obtenerPorId(ID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(nombreRecurso(), id));
    }

    @Override
    @Transactional
    public T crear(T entidad) {
        entidad.setId(null); // el ID lo genera la base de datos
        validarAntesDeGuardar(entidad);
        T guardada = repositorio.save(entidad);
        notificar(TipoEvento.CREADO, guardada.getId());
        return guardada;
    }

    @Override
    @Transactional
    public T actualizar(ID id, T datos) {
        T existente = obtenerPorId(id);
        copiarDatos(datos, existente);
        validarAntesDeGuardar(existente);
        T guardada = repositorio.save(existente);
        notificar(TipoEvento.ACTUALIZADO, id);
        return guardada;
    }

    @Override
    @Transactional
    public void eliminar(ID id) {
        if (!repositorio.existsById(id)) {
            throw new RecursoNoEncontradoException(nombreRecurso(), id);
        }
        repositorio.deleteById(id);
        notificar(TipoEvento.ELIMINADO, id);
    }

    /** Publica el evento para los observadores (patrón Observer). */
    private void notificar(TipoEvento tipo, ID id) {
        publicadorEventos.publishEvent(EventoEntidad.de(tipo, nombreRecurso(), id));
    }
}
