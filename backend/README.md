# biblioteca

Microservicio generado con el arquetipo Maven **microservicio-arquetipo** (Spring Boot 3 + Java 21 + Oracle Autonomous Database).

## Estructura

```
src/main/java/cl/duoc/biblioteca
├── Application.java
├── model/        # Entidad JPA con Builder + interfaz Identificable
├── repository/   # JpaRepository
├── dto/          # Request (con validaciones) y Response
├── factory/      # Factory: DTO <-> entidad
├── service/      # CrudService + AbstractCrudService (Template Method) + servicio de la entidad
├── event/        # EventoEntidad + AuditoriaListener (Observer)
├── controller/   # CRUD REST /libros + GET /auditoria
└── exception/    # ErrorResponse (Builder) + manejo de errores 400/404
```

## Patrones de diseño incluidos

| Patrón | Dónde |
|---|---|
| Template Method | `service/AbstractCrudService` |
| Builder | `model/Libro.Builder`, `exception/ErrorResponse.Builder` |
| Factory | `factory/LibroFactory` |
| Observer | `service/AbstractCrudService` publica `EventoEntidad`; `event/AuditoriaListener` lo recibe |
| Singleton | Beans de Spring (`@Service`, `@Component`, `@RestController`) |
| Repository + DTO + Inyección de dependencias | `repository/`, `dto/`, constructores |

## Ejecutar

1. Descomprimir el wallet de Oracle en `wallet/` (no se sube a Git).
2. Definir `DB_SERVICE`, `TNS_ADMIN`, `DB_USER` y `DB_PASSWORD`, o crear `application-local.properties` y usar el perfil `local`.
3. `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`).

Pruebas (no requieren base de datos): `./mvnw test`.
