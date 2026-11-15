# microservicio-arquetipo

Arquetipo Maven propio (`cl.duoc.arquetipos:microservicio-arquetipo:1.0.0`) para generar microservicios **Spring Boot 3 + Java 21 + Oracle Autonomous Database**. Cada proyecto generado trae el CRUD completo de una entidad, organizado en capas y con patrones de diseño ya aplicados.

Con él se volvió a programar el microservicio de libros de la semana 1 (carpeta [`../../backend`](../../backend)).

## Qué genera

```
<artifactId>/
├── pom.xml                      # Spring Boot 3.5, web, JPA, validation, ojdbc11 + oraclepki
├── mvnw, mvnw.cmd, .mvn/        # Maven Wrapper (no requiere Maven instalado)
├── Dockerfile, .dockerignore    # Imagen multi-etapa
├── .gitignore                   # Excluye wallet/, credenciales y target/
├── README.md
└── src/
    ├── main/java/<package>/
    │   ├── Application.java
    │   ├── model/       <Entidad>.java (JPA + Builder), Identificable.java
    │   ├── repository/  <Entidad>Repository.java (JpaRepository)
    │   ├── dto/         <Entidad>Request.java (validaciones), <Entidad>Response.java
    │   ├── factory/     <Entidad>Factory.java (DTO <-> entidad)
    │   ├── service/     CrudService.java, AbstractCrudService.java, <Entidad>Service.java
    │   ├── event/       TipoEvento, EventoEntidad, AuditoriaListener
    │   ├── controller/  <Entidad>Controller.java (/<recurso>), AuditoriaController.java (/auditoria)
    │   └── exception/   ErrorResponse (Builder), RecursoNoEncontradoException, GlobalExceptionHandler
    ├── main/resources/application.properties   # Oracle con wallet vía variables de entorno
    └── test/java/<package>/                    # 20 pruebas (controller, service, factory, observer)
```

## Patrones de diseño que incluye

| Patrón | Tipo | Clase generada | Para qué sirve |
|---|---|---|---|
| **Template Method** | Comportamiento | `service/AbstractCrudService` | Define una sola vez el algoritmo de cada operación CRUD (buscar → validar → guardar → notificar). Cada servicio solo implementa `nombreRecurso()` y `copiarDatos()`, y opcionalmente `validarAntesDeGuardar()`. |
| **Observer** | Comportamiento | `AbstractCrudService` publica `event/EventoEntidad`; `event/AuditoriaListener` lo recibe | Notifica las operaciones de crear, actualizar y eliminar sin acoplar el servicio a quien reacciona. Se puede consultar en `GET /auditoria`. |
| **Builder** | Creacional | `model/<Entidad>.Builder`, `exception/ErrorResponse.Builder` | Construye objetos paso a paso de forma legible. |
| **Factory** | Creacional | `factory/<Entidad>Factory` | Centraliza la creación de entidades y respuestas a partir de los DTOs. |
| **Singleton** | Creacional | Beans `@Service`, `@Component`, `@RestController` | Spring crea una única instancia de cada uno. |
| Repository, DTO e inyección de dependencias | Arquitectura | `repository/`, `dto/`, constructores | Acceso a datos desacoplado, contrato de API separado de la entidad, dependencias recibidas por el constructor. |

## Parámetros

| Parámetro | Ejemplo | Uso |
|---|---|---|
| `groupId` | `cl.duoc` | groupId del proyecto generado |
| `artifactId` | `biblioteca` | Nombre del proyecto y de la carpeta |
| `version` | `2.0.0` | Versión del proyecto generado |
| `package` | `cl.duoc.biblioteca` | Paquete base de las clases |
| `entidad` | `Libro` | Nombre de la entidad (PascalCase). Da nombre a las clases `Libro`, `LibroService`, `LibroController`, etc. |
| `recurso` | `libros` | Ruta REST: `/libros` |
| `tabla` | `LIBROS` | Tabla en Oracle (`@Table(name = "LIBROS")`) |

`entidad`, `recurso` y `tabla` se validan con expresiones regulares en `archetype-metadata.xml`.

## Cómo se construyó

1. Se programó un proyecto base funcional, con la entidad de ejemplo `Item`, sus capas, los patrones y las pruebas.
2. Se generó el arquetipo desde ese proyecto con:
   ```powershell
   mvn archetype:create-from-project -Darchetype.properties=archetype.properties
   ```
3. Se ajustó la plantilla (`src/main/resources/archetype-resources`):
   - `Item`, `/items` e `ITEMS` se reemplazaron por las variables Velocity `${entidad}`, `${recurso}` y `${tabla}`.
   - Los archivos se renombraron a `__entidad__*.java` para que tomen el nombre de la entidad.
4. En `META-INF/maven/archetype-metadata.xml` se declararon los parámetros obligatorios y sus validaciones, y los grupos de archivos que se copian.
5. Se agregó `META-INF/archetype-post-generate.groovy`. Renombra `gitignore` a `.gitignore`, porque Maven no empaqueta archivos con ese nombre, y muestra los siguientes pasos al terminar.
6. Se agregó una prueba de integración (`src/test/resources/projects/basic`). Al instalar, genera un microservicio de ejemplo (`Producto` en `/productos`) y ejecuta `mvn verify` sobre él: compila y corren sus 20 pruebas.

## Instalar el arquetipo

```powershell
cd arquetipo\microservicio-arquetipo
.\mvnw.cmd install
```

Lo deja en el repositorio local (`~/.m2/repository/cl/duoc/arquetipos/microservicio-arquetipo/1.0.0`). Antes de instalar, ejecuta la prueba de integración.

## Generar un microservicio

```powershell
mvn archetype:generate `
  -DarchetypeGroupId=cl.duoc.arquetipos `
  -DarchetypeArtifactId=microservicio-arquetipo `
  -DarchetypeVersion=1.0.0 `
  -DgroupId=cl.duoc -DartifactId=biblioteca -Dversion=2.0.0 `
  -Dpackage=cl.duoc.biblioteca `
  -Dentidad=Libro -Drecurso=libros -Dtabla=LIBROS `
  -B
```

Sin Maven instalado se puede usar el wrapper de esta carpeta: `.\mvnw.cmd archetype:generate ...`. Sin `-B`, Maven pide los parámetros de forma interactiva.

Después de generar, agrega los atributos propios de la entidad. La plantilla trae solo `nombre` como ejemplo; hay que completar la entidad y su Builder, los DTOs con sus validaciones, la Factory y `copiarDatos()` del servicio.

## Versionado

El arquetipo usa versionado semántico (`MAJOR.MINOR.PATCH`). Los cambios que rompen los proyectos generados suben la versión mayor. El historial está en el [CHANGELOG](../../CHANGELOG.md) del repositorio.
