#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repository;

import ${package}.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA (patrón Repository): Spring Data genera la implementación
 * de las operaciones CRUD contra la base de datos Oracle.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
