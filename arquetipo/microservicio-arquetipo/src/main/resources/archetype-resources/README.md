#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
${symbol_pound} ${artifactId}

Microservicio generado con el arquetipo Maven **microservicio-arquetipo** (Spring Boot 3 + Java 21 + Oracle Autonomous Database).

${symbol_pound}${symbol_pound} Estructura

```
src/main/java/${packageInPathFormat}
├── Application.java
├── model/        ${symbol_pound} Entidad JPA con Builder + interfaz Identificable
├── repository/   ${symbol_pound} JpaRepository
├── dto/          ${symbol_pound} Request (con validaciones) y Response
├── factory/      ${symbol_pound} Factory: DTO <-> entidad
├── service/      ${symbol_pound} CrudService + AbstractCrudService (Template Method) + servicio de la entidad
├── event/        ${symbol_pound} EventoEntidad + AuditoriaListener (Observer)
├── controller/   ${symbol_pound} CRUD REST /${recurso} + GET /auditoria
└── exception/    ${symbol_pound} ErrorResponse (Builder) + manejo de errores 400/404
```

${symbol_pound}${symbol_pound} Patrones de diseño incluidos

| Patrón | Dónde |
|---|---|
| Template Method | `service/AbstractCrudService` |
| Builder | `model/${entidad}.Builder`, `exception/ErrorResponse.Builder` |
| Factory | `factory/${entidad}Factory` |
| Observer | `service/AbstractCrudService` publica `EventoEntidad`; `event/AuditoriaListener` lo recibe |
| Singleton | Beans de Spring (`@Service`, `@Component`, `@RestController`) |
| Repository + DTO + Inyección de dependencias | `repository/`, `dto/`, constructores |

${symbol_pound}${symbol_pound} Ejecutar

1. Descomprimir el wallet de Oracle en `wallet/` (no se sube a Git).
2. Definir `DB_SERVICE`, `TNS_ADMIN`, `DB_USER` y `DB_PASSWORD`, o crear `application-local.properties` y usar el perfil `local`.
3. `./mvnw spring-boot:run` (Windows: `.${symbol_escape}mvnw.cmd spring-boot:run`).

Pruebas (no requieren base de datos): `./mvnw test`.
