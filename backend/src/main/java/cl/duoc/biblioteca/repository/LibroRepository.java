package cl.duoc.biblioteca.repository;

import cl.duoc.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA (patrón Repository): Spring Data genera la implementación
 * de las operaciones CRUD contra la base de datos Oracle.
 */
@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
}
