# Changelog

Todos los cambios relevantes del proyecto. El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y las versiones usan [versionado semántico](https://semver.org/lang/es/).

## [Sin publicar]

### Cambiado
- **Mejoras de diseño según los comentarios del profesor:** el frontend ahora usa Bootstrap 5.3.9 y Bootstrap Icons, personalizados con un tema CSS propio (`frontend/src/styles.css`).
  - Paleta verde profundo `#173B2E`, oro mate `#C6A563` y hueso `#F1EADD`.
  - Tipografías Playfair Display y Source Sans 3.
  - Vistas rediseñadas: catálogo con tabla e íconos de acción, ficha de detalle con portada, formulario con validación de Bootstrap y estados de carga y de libro no encontrado.
  - Diseño responsive para móviles.

## [2.0.0] - 2026-11-15 · Semana 4: "Implementando lineamientos empresariales"

### Agregado
- **Arquetipo Maven propio** `cl.duoc.arquetipos:microservicio-arquetipo:1.0.0` (`arquetipo/microservicio-arquetipo`).
  - Se generó con `archetype:create-from-project` desde un proyecto base y se parametrizó con `entidad`, `recurso` y `tabla`, validados con expresiones regulares.
  - La plantilla trae arquitectura en capas y los patrones Template Method, Observer, Builder, Factory y Singleton.
  - Incluye Maven Wrapper, Dockerfile, `.gitignore` (lo renombra un script post-generación) y 20 pruebas.
  - Tiene una prueba de integración: al instalar, genera un microservicio de ejemplo y ejecuta `mvn verify` sobre él.
- Endpoint `GET /auditoria` con los últimos eventos del CRUD (patrón Observer).
- Colección Postman v2: 15 solicitudes y 30 aserciones. Cubre el CRUD, las validaciones y errores 400/404, y la auditoría.
- Lineamientos de versionado (Git Flow + Conventional Commits) documentados en el README.

### Cambiado
- **El microservicio de libros se volvió a programar a partir del arquetipo** (`backend/`, `biblioteca` 2.0.0).
  - `LibroServiceImpl` se reemplazó por `LibroService`, que extiende `AbstractCrudService` (Template Method).
  - Se agregaron DTOs (`LibroRequest` y `LibroResponse`) y `LibroFactory`. La entidad `Libro` ahora tiene Builder.
  - Los errores se construyen con `ErrorResponse.builder()`.
  - Las pruebas pasaron de 6 a 22.
- La API mantiene el mismo contrato (rutas, JSON y mensajes de error), así que el frontend de la semana 3 funciona sin cambios.

### Corregido
- Un JSON malformado o un ID no numérico ahora responden 400 con un mensaje claro, en vez de un error genérico.

## [1.1.0] - 2026-11-08 · Semana 3: "Aplicando un patrón de diseño a nuestro desarrollo"

### Agregado
- Frontend Angular 22 (`frontend/`) con arquetipo MVC y patrones Singleton, Observer, Facade y Decorator.
- Docker Compose para levantar el frontend (nginx) y el backend en el mismo origen.

### Cambiado
- El microservicio se movió a la carpeta `backend/`.

## [1.0.0] - 2026-10-25 · Semana 1: "Desarrollando nuestro microservicio"

### Agregado
- Microservicio Spring Boot 3 con el CRUD de libros (`/libros`) sobre Oracle Autonomous Database con wallet.
- Script SQL de la tabla `LIBROS` y colección Postman.

[2.0.0]: https://github.com/rodolfoulloa/FS3_Exp1_S1/compare/v1.1.0...v2.0.0
[1.1.0]: https://github.com/rodolfoulloa/FS3_Exp1_S1/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/rodolfoulloa/FS3_Exp1_S1/releases/tag/v1.0.0
