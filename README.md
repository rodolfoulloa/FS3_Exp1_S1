# FS3_Exp1_S1 — Biblioteca (microservicio + frontend Angular)

DSY2205 Desarrollo Full Stack III

| Semana | Actividad | Qué se agrega |
|---|---|---|
| 1 | *"Desarrollando nuestro microservicio"* | Microservicio **Java 21 + Spring Boot 3** con CRUD de libros sobre **Oracle Autonomous Database** (carpeta `backend/`) |
| 3 | *"Aplicando un patrón de diseño a nuestro desarrollo"* (Exp. 2, formativa 2) | Aplicación web **Angular 22** que gestiona los libros consumiendo los 5 endpoints del microservicio, construida con el arquetipo **MVC** y patrones de diseño (carpeta `frontend/`) |
| **4** | *"Implementando lineamientos empresariales"* (Exp. 2, formativa 4) | **Arquetipo Maven propio** con patrones de diseño (carpeta `arquetipo/`). Con él se volvió a programar el microservicio (carpeta `backend/`, v2.0.0). Versionado con **Git Flow** y tags semánticos |

Versiones publicadas (tags): `v1.0.0` semana 1 · `v1.1.0` semana 3 · `v2.0.0` semana 4 · `v2.1.0` diseño Bootstrap personalizado (según comentarios del profesor) · `v2.1.1` corrección de diagramas del README. Detalle en el [CHANGELOG](CHANGELOG.md).

## Arquitectura

```mermaid
flowchart LR
    U[Navegador] -->|http://localhost:4200| N[frontend<br/>nginx + Angular]
    N -->|/api/libros/** → /libros/**| B[backend<br/>Spring Boot :8080]
    B -->|JPA + Wallet mTLS| DB[(Oracle Autonomous DB<br/>FS3DB · tabla LIBROS)]
```

- El frontend llama siempre a la URL relativa `/api/libros`. Un proxy (nginx en Docker o `proxy.conf.json` con `ng serve`) la reenvía al microservicio quitando el prefijo `/api`.
- Así el navegador ve el frontend y la API en el **mismo origen** y no se necesita configurar CORS en el backend.

## Semana 4: arquetipo Maven propio y microservicio regenerado

### 1. Arquetipo `microservicio-arquetipo`

[`arquetipo/microservicio-arquetipo`](arquetipo/microservicio-arquetipo) es un arquetipo Maven propio (`cl.duoc.arquetipos:microservicio-arquetipo:1.0.0`). Genera un microservicio Spring Boot 3 + Oracle completo para cualquier entidad, a partir de tres parámetros: `entidad`, `recurso` y `tabla`. El paso a paso de su construcción está en su [README](arquetipo/microservicio-arquetipo/README.md).

```mermaid
flowchart LR
    PB["Proyecto base<br/>entidad Item"] -->|"archetype:create-from-project"| AR["microservicio-arquetipo<br/>plantilla Velocity<br/>entidad · recurso · tabla"]
    AR -->|"mvnw install<br/>+ prueba de integración"| M2[("Repositorio Maven local")]
    M2 -->|"archetype:generate<br/>-Dentidad=Libro -Drecurso=libros -Dtabla=LIBROS"| BK["backend/<br/>biblioteca 2.0.0"]
    BK -->|"se completan los atributos de Libro"| OK["CRUD /libros contra Oracle"]
```

```powershell
# 1) Instalar el arquetipo (corre su prueba de integración: genera un ms-productos y ejecuta sus 20 pruebas)
cd arquetipo\microservicio-arquetipo
.\mvnw.cmd install

# 2) Generar el microservicio de libros desde el arquetipo
.\mvnw.cmd archetype:generate -B `
  -DarchetypeGroupId=cl.duoc.arquetipos -DarchetypeArtifactId=microservicio-arquetipo -DarchetypeVersion=1.0.0 `
  -DgroupId=cl.duoc -DartifactId=biblioteca -Dversion=2.0.0 -Dpackage=cl.duoc.biblioteca `
  -Dentidad=Libro -Drecurso=libros -Dtabla=LIBROS
```

### 2. Patrones de diseño en el microservicio

Los patrones vienen incluidos en el arquetipo, así que todo microservicio generado los trae. En `backend/` se aplican a la entidad `Libro`:

| Patrón | Tipo | Dónde (`backend/src/main/java/cl/duoc/biblioteca/`) | Cómo se aplica |
|---|---|---|---|
| **Template Method** | Comportamiento | `service/AbstractCrudService.java` → `service/LibroService.java` | La clase abstracta fija el algoritmo de cada operación del CRUD: buscar, validar, guardar y notificar. `LibroService` solo define `nombreRecurso()` y `copiarDatos()`. |
| **Observer** | Comportamiento | `event/EventoEntidad.java`, `event/AuditoriaListener.java`, `controller/AuditoriaController.java` | Al crear, actualizar o eliminar se publica un `EventoEntidad` con `ApplicationEventPublisher`. `AuditoriaListener` lo recibe con `@EventListener`, lo registra en el log y lo deja disponible en `GET /auditoria`. |
| **Builder** | Creacional | `model/Libro.java` (`Libro.builder()`), `exception/ErrorResponse.java` | Construye libros y respuestas de error paso a paso. |
| **Factory** | Creacional | `factory/LibroFactory.java` | Crea la entidad `Libro` desde `LibroRequest` y la respuesta `LibroResponse` desde la entidad. El controlador no usa `new`. |
| **Singleton** | Creacional | `@Service`, `@Component`, `@RestController`, `@Repository` | Spring crea una sola instancia de cada bean. |
| Repository · DTO · Inyección de dependencias | Arquitectura | `repository/`, `dto/`, constructores | Spring Data implementa el acceso a Oracle. Los DTOs separan el contrato JSON de la entidad JPA, y las dependencias llegan por constructor. |

```mermaid
flowchart LR
    C["LibroController"] -->|"LibroRequest"| F["LibroFactory<br/>Factory + Builder"]
    C --> S["LibroService<br/>extends AbstractCrudService<br/>Template Method"]
    S --> R["LibroRepository<br/>JpaRepository"]
    R --> DB[("Oracle · LIBROS")]
    S -->|"publishEvent"| E(("EventoEntidad"))
    E -->|"#64;EventListener"| A["AuditoriaListener<br/>Observer"]
    A --> AU["GET /auditoria"]
```

El contrato de la API es el mismo de la semana 1, con los mismos 5 endpoints, el mismo JSON y los mismos errores. Por eso el frontend Angular de la semana 3 funciona sin cambios con el microservicio regenerado.

### 3. Lineamientos de versionado (Git Flow)

| Elemento | Regla |
|---|---|
| `main` | Solo versiones entregadas. Cada una lleva un tag semántico (`vMAJOR.MINOR.PATCH`). |
| `develop` | Rama de integración del trabajo en curso. |
| `feature/<tema>` | Una rama por funcionalidad, creada desde `develop`. Vuelve a `develop` con `merge --no-ff` para que quede registrada en el historial. |
| `release/<versión>` | Prepara la entrega (versión y CHANGELOG). Se fusiona en `main` y en `develop`, y se etiqueta. |
| Commits | [Conventional Commits](https://www.conventionalcommits.org/es/) en español: `feat`, `fix`, `test`, `docs`, `build`, `chore`, con el ámbito entre paréntesis (`feat(arquetipo): ...`). |
| Versiones | `MAJOR` cuando cambia la arquitectura o algo deja de ser compatible (2.0.0: microservicio regenerado), `MINOR` para funcionalidades nuevas y `PATCH` para correcciones. |

Ramas de la semana 4: `feature/arquetipo-maven` → `feature/microservicio-desde-arquetipo` → `feature/postman-evidencias` → `release/2.0.0`.

```mermaid
gitGraph
    commit id: "v1.1.0 (semana 3)" tag: "v1.1.0"
    branch develop
    branch feature/arquetipo-maven
    commit id: "create-from-project"
    commit id: "parametriza plantilla"
    commit id: "prueba de integración"
    checkout develop
    merge feature/arquetipo-maven
    branch feature/microservicio-desde-arquetipo
    commit id: "archetype:generate"
    commit id: "atributos de Libro"
    checkout develop
    merge feature/microservicio-desde-arquetipo
    branch feature/postman-evidencias
    commit id: "colección Postman v2"
    commit id: "evidencias + docs"
    checkout develop
    merge feature/postman-evidencias
    branch release/2.0.0
    commit id: "versión 2.0.0"
    checkout main
    merge release/2.0.0 tag: "v2.0.0"
```

## Estructura del repositorio

```
FS3_Exp1_S1/
├── docker-compose.yml          # Levanta backend + frontend
├── .env.example                # Plantilla de credenciales (copiar como .env, no se sube)
├── CHANGELOG.md                # Historial de versiones
├── docs/evidencias/            # Pantallazos (semana 3: app Angular · semana 4: Postman/Newman)
├── arquetipo/
│   └── microservicio-arquetipo/ # Arquetipo Maven propio (semana 4)
│       ├── pom.xml                                   # packaging maven-archetype
│       ├── src/main/resources/META-INF/maven/archetype-metadata.xml   # parámetros
│       ├── src/main/resources/META-INF/archetype-post-generate.groovy
│       ├── src/main/resources/archetype-resources/   # plantilla del microservicio
│       └── src/test/resources/projects/basic/        # prueba de integración
├── backend/                    # Microservicio de libros v2, generado con el arquetipo
│   ├── src/main/java/cl/duoc/biblioteca
│   │   ├── model/           # Libro (JPA + Builder), Identificable
│   │   ├── repository/      # LibroRepository (JpaRepository)
│   │   ├── dto/             # LibroRequest (validaciones), LibroResponse
│   │   ├── factory/         # LibroFactory (Factory)
│   │   ├── service/         # CrudService, AbstractCrudService (Template Method), LibroService
│   │   ├── event/           # EventoEntidad, AuditoriaListener (Observer)
│   │   ├── controller/      # LibroController (/libros), AuditoriaController (/auditoria)
│   │   └── exception/       # ErrorResponse (Builder), GlobalExceptionHandler
│   ├── src/test/java/...    # 22 pruebas (controller, service, factory, observer)
│   ├── sql/crear_tabla_libros.sql           # Script de la tabla + datos de prueba
│   ├── postman/                             # Colección Postman v2
│   ├── Dockerfile  .dockerignore
│   └── pom.xml  mvnw  mvnw.cmd
└── frontend/                   # Aplicación Angular de la semana 3
    ├── src/app
    │   ├── models/libro.ts                  # Modelo
    │   ├── services/libro-api.ts            # Acceso HTTP a los 5 endpoints
    │   ├── facades/libro-facade.ts          # Controlador / Facade con el estado
    │   ├── components/
    │   │   ├── libro-lista/                 # Vista: tabla, buscar por ID, ver/editar/eliminar
    │   │   ├── libro-form/                  # Vista: crear y editar
    │   │   └── libro-detalle/               # Vista: detalle por ID
    │   ├── app.routes.ts                    # Rutas
    │   └── app.config.ts                    # HttpClient + router
    ├── src/environments/environment.ts      # apiUrl = '/api/libros'
    ├── src/styles.css                       # Tema CSS personalizado sobre Bootstrap (paleta verde · oro · hueso)
    ├── proxy.conf.json                      # Proxy para ng serve
    ├── nginx.conf  Dockerfile  .dockerignore
    └── package.json  angular.json
```

## Arquetipo y patrones de diseño (frontend)

El frontend sigue el arquetipo **Modelo-Vista-Controlador (MVC)**:

```mermaid
flowchart LR
    V[Vistas<br/>libro-lista · libro-form · libro-detalle] -->|llama métodos| F[LibroFacade<br/>Controlador]
    F -->|libros$ · cargando$ · error$<br/>pipe async| V
    F --> S[LibroApi<br/>HttpClient]
    S --> M[Libro<br/>Modelo]
    S -->|Observable| F
```

| Patrón | Tipo | Dónde | Cómo se aplica |
|---|---|---|---|
| **MVC** | Arquetipo / patrón arquitectónico | `models/`, `services/`, `facades/`, `components/`, `app.routes.ts` | **Modelo:** interfaz `Libro` + `LibroApi`. **Vista:** componentes con sus plantillas HTML. **Controlador:** `LibroFacade` y el enrutador, que deciden qué vista se muestra y comunican la vista con el modelo. |
| **Singleton** | Diseño (creacional) | `services/libro-api.ts`, `facades/libro-facade.ts` | `@Injectable({ providedIn: 'root' })`: Angular crea una sola instancia para toda la aplicación, así todas las vistas comparten el mismo estado. |
| **Observer** | Diseño (comportamiento) | `services/libro-api.ts`, `facades/libro-facade.ts`, plantillas `*.html` | `LibroApi` devuelve `Observable`. El facade guarda el estado en `BehaviorSubject` y lo expone como `libros$`, `cargando$`, `error$` y `seleccionado$`. Las vistas se suscriben con el pipe `async` y se actualizan solas cuando cambia la lista. |
| **Facade** | Diseño (estructural) | `facades/libro-facade.ts` | Interfaz simple (`cargarLibros`, `seleccionar`, `agregar`, `actualizar`, `eliminar`) que oculta el HTTP, el estado y el manejo de errores. Traduce las respuestas 400/404 del backend a mensajes legibles. Las vistas no conocen `HttpClient` ni la API. |
| **Decorator** | Diseño (estructural) | Todos los componentes y servicios | `@Component` y `@Injectable` agregan metadatos y comportamiento a las clases (selector, plantilla, inyección de dependencias) sin modificarlas. |

Cada archivo tiene comentarios que explican su rol en el arquetipo y el patrón que aplica.

## Diseño de la interfaz: Bootstrap con CSS personalizado

La interfaz usa **Bootstrap 5.3.9** y **Bootstrap Icons 1.13.2**, personalizados con un tema CSS propio: [`frontend/src/styles.css`](frontend/src/styles.css). Son mejoras de diseño hechas según los comentarios del profesor.

- **Cómo se personaliza:** `angular.json` carga primero `bootstrap.min.css`, luego los íconos y al final `styles.css`. El tema no modifica Bootstrap. Redefine sus variables CSS (`--bs-primary`, `--bs-body-bg`, `--bs-border-color`, `--bs-btn-*`, `--bs-table-*`, etc.) y agrega clases propias: `navbar-biblioteca`, `btn-oro`, `badge-oro`, `card-ficha`, `portada`, `encabezado-pagina` y `pie`.
- **Tipografía:** *Playfair Display* para los títulos (estilo editorial) y *Source Sans 3* para el texto, desde Google Fonts. Si no cargan, se usan fuentes del sistema.
- **Componentes de Bootstrap usados:** navbar, card, table, input-group, form-control con validación (`is-invalid` / `invalid-feedback`), botones, badges, alertas descartables y spinners.
- **Responsive:** en pantallas angostas la tabla oculta Autor, Año y Género (los muestra bajo el título) y la barra deja solo los íconos.

| Color | Hex | Uso |
|---|---|---|
| Verde profundo | `#173B2E` | Barra de navegación, botones principales, encabezado de la tabla, títulos |
| Oro mate | `#C6A563` | Acentos: borde de la barra y de las fichas, botón "Nuevo libro", insignias de género, foco de los campos |
| Hueso | `#F1EADD` | Fondo de la aplicación (con tarjetas en un tono más claro, `#FAF7F1`) |

## Microservicio (backend)

### Entidad Libro

| Atributo | Tipo | Columna |
|---|---|---|
| id | Long (autogenerado) | ID |
| titulo | String | TITULO |
| autor | String | AUTOR |
| anioPublicacion | Integer (0–2100) | ANIO_PUBLICACION |
| genero | String | GENERO |

El modelo del frontend (`frontend/src/app/models/libro.ts`) tiene exactamente los mismos atributos.

### Endpoints y su uso en el frontend

| Método | URL del microservicio | Método de `LibroApi` | Vista que lo usa |
|---|---|---|---|
| GET | `/libros` | `listar()` | `libro-lista` |
| GET | `/libros/{id}` | `obtenerPorId(id)` | `libro-detalle` (y buscar por ID), `libro-form` al editar |
| POST | `/libros` | `crear(libro)` | `libro-form` (nuevo) |
| PUT | `/libros/{id}` | `actualizar(id, libro)` | `libro-form` (editar) |
| DELETE | `/libros/{id}` | `eliminar(id)` | `libro-lista` |
| GET | `/auditoria` | — | Solo Postman: últimos eventos del patrón Observer (semana 4) |

Ejemplo de body (POST / PUT):

```json
{
  "titulo": "El Principito",
  "autor": "Antoine de Saint-Exupéry",
  "anioPublicacion": 1943,
  "genero": "Fábula"
}
```

Errores: `404` → `{"status":404,"error":"No existe un libro con ID 5"}` · `400` → `{"error":"Datos inválidos","detalle":{"titulo":"El título es obligatorio"}}`. Desde la v2 también responden 400 un JSON malformado y un ID no numérico (`/libros/abc`).

### Configuración de Oracle Cloud

1. En Oracle Cloud crear una **Autonomous Database** (Always Free).
2. Descargar el **Wallet** (*Database connection → Download wallet*) y descomprimirlo en `backend/wallet/`. Está en `.gitignore` y **no se sube**.
3. Revisar en `backend/wallet/tnsnames.ora` el nombre del servicio (ej: `fs3db_tp`).
4. La tabla `LIBROS` se crea automáticamente al iniciar (`ddl-auto=update`). También se puede crear con `backend/sql/crear_tabla_libros.sql`.

## Cómo levantar todo con Docker (recomendado)

Requisitos: Docker Desktop y el wallet en `backend/wallet/`.

```powershell
cd C:\proyectos\FS3_Exp1_S1
copy .env.example .env      # y completar DB_PASSWORD (y DB_SERVICE/DB_USER si cambian)
docker compose up --build
```

| Servicio | URL |
|---|---|
| Aplicación web (Angular + nginx) | http://localhost:4200 |
| Microservicio (acceso directo) | http://localhost:8080/libros |

- **backend:** se compila con Maven dentro de la imagen y lee `DB_SERVICE`, `DB_USER` y `DB_PASSWORD` desde `.env`. El wallet se monta en `/app/wallet` como solo lectura.
- **frontend:** se compila con `npm run build` y lo sirve nginx. nginx reenvía `/api/**` al contenedor `backend` y hace el *fallback* a `index.html` para las rutas de Angular.

Detener: `docker compose down`.

## Cómo levantarlo sin Docker

**1. Microservicio** (terminal 1). Se puede usar el archivo `backend/application-local.properties`, que tampoco se sube:

```properties
spring.datasource.url=jdbc:oracle:thin:@fs3db_tp?TNS_ADMIN=C:/proyectos/FS3_Exp1_S1/backend/wallet
spring.datasource.username=ADMIN
spring.datasource.password=tu_clave
```

```powershell
cd C:\proyectos\FS3_Exp1_S1\backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

O con variables de entorno (`DB_SERVICE`, `TNS_ADMIN`, `DB_USER`, `DB_PASSWORD`) y `.\mvnw.cmd spring-boot:run`.

**2. Frontend** (terminal 2). Requiere Node.js 22 o superior:

```powershell
cd C:\proyectos\FS3_Exp1_S1\frontend
npm install
npm start          # ng serve → http://localhost:4200
```

`ng serve` usa `proxy.conf.json`, que redirige `/api/**` a `http://localhost:8080/**`.

## Pruebas

```powershell
cd arquetipo\microservicio-arquetipo; .\mvnw.cmd install   # prueba de integración del arquetipo (genera ms-productos: 20 pruebas)
cd backend;  .\mvnw.cmd test             # 22 pruebas (MockMvc + Mockito, sin BD)
cd frontend; npx ng test --watch=false   # 19 pruebas Vitest
```

Pruebas del backend:

- `LibroControllerTest`: los 5 endpoints, el 404, los 400 por validación, el año fuera de rango, el JSON malformado y el ID no numérico.
- `LibroServiceTest`: el algoritmo del Template Method (crear sin ID, copiar datos al actualizar) y que se publique el evento correcto en cada operación (Observer), o ninguno si falla.
- `LibroFactoryTest`: la creación de entidades y respuestas con Factory y Builder.
- `AuditoriaListenerTest`: el observador registra los eventos en orden y conserva los últimos 50.

Pruebas del frontend:

- `libro-api.spec.ts`: verifica con `HttpTestingController` el método HTTP, la URL y el body de cada uno de los 5 endpoints, y que el servicio es singleton.
- `libro-facade.spec.ts`: la lista observada se actualiza al cargar, agregar, actualizar y eliminar. También cubre la traducción de errores 404, 400 y "servicio no disponible".

La colección Postman (`backend/postman/Biblioteca.postman_collection.json`) prueba el microservicio directo en `http://localhost:8080`. Se puede importar en Postman y ejecutar con *Run collection*, o correr con Newman:

```powershell
npx newman run .\backend\postman\Biblioteca.postman_collection.json
```

Tiene 15 solicitudes y 30 aserciones, en tres carpetas: **1. CRUD** (los 5 endpoints, más verificaciones), **2. Validaciones y errores** (400 y 404) y **3. Patrón Observer** (`GET /auditoria` debe mostrar CREADO, ACTUALIZADO y ELIMINADO del libro de prueba).

## Evidencias

### Semana 4: microservicio generado con el arquetipo, probado con Postman contra Oracle

Colección Postman ejecutada con Newman (reporte `htmlextra`) contra el microservicio v2 corriendo en Docker y conectado a Oracle Autonomous Database: **15 solicitudes, 30 aserciones, 0 fallidas**.

![Resumen de la ejecución](docs/evidencias/s4-01-newman-resumen.png)
![Solicitudes por carpeta](docs/evidencias/s4-02-newman-solicitudes.png)

**GET /libros: listar todos**

![GET listar](docs/evidencias/s4-03-get-listar.png)

**POST /libros: crear (201, Oracle genera el ID)**

![POST crear](docs/evidencias/s4-04-post-crear.png)

**GET /libros/{id}: obtener por ID**

![GET por ID](docs/evidencias/s4-05-get-por-id.png)

**PUT /libros/{id}: actualizar**

![PUT actualizar](docs/evidencias/s4-06-put-actualizar.png)

**DELETE /libros/{id}: eliminar (204)**

![DELETE eliminar](docs/evidencias/s4-07-delete-eliminar.png)

**GET /libros/{id} del libro eliminado: 404**

![GET 404](docs/evidencias/s4-08-get-404.png)

**POST con campos vacíos: 400 con detalle por campo (ErrorResponse con Builder)**

![POST 400](docs/evidencias/s4-09-post-400-validaciones.png)

**GET /auditoria: el observador registró CREADO, ACTUALIZADO y ELIMINADO (patrón Observer)**

![Auditoría Observer](docs/evidencias/s4-10-observer-auditoria.png)

**Instalación del arquetipo (`.\mvnw.cmd install`) con su prueba de integración**

```
[INFO] --- archetype:3.4.1:integration-test (default-integration-test) @ microservicio-arquetipo ---
Microservicio ms-productos generado con microservicio-arquetipo
  Entidad : Producto  ->  /productos  (tabla PRODUCTOS)
[INFO] [INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] [INFO] BUILD SUCCESS
[INFO] Installing ...\target\microservicio-arquetipo-1.0.0.jar to ~\.m2\repository\cl\duoc\arquetipos\microservicio-arquetipo\1.0.0\microservicio-arquetipo-1.0.0.jar
[INFO] BUILD SUCCESS
```

**Generación del microservicio de libros (`archetype:generate`)**

```
Microservicio biblioteca generado con microservicio-arquetipo
  Entidad : Libro  ->  /libros  (tabla LIBROS)
  Siguiente paso: agrega los atributos de Libro (entidad, DTOs, Factory y copiarDatos del servicio)
```

**Pruebas del backend (`.\mvnw.cmd test`)**

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0 -- in cl.duoc.biblioteca.controller.LibroControllerTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in cl.duoc.biblioteca.event.AuditoriaListenerTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in cl.duoc.biblioteca.factory.LibroFactoryTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- in cl.duoc.biblioteca.service.LibroServiceTest
```

**Microservicio en Docker conectado a Oracle y log del observador**

```
Database version: 19.33
Started Application in 10.581 seconds
[AUDITORIA] libro CREADO con ID 41
[AUDITORIA] libro ACTUALIZADO con ID 41
[AUDITORIA] libro ELIMINADO con ID 41
```

El frontend de la semana 3 (http://localhost:4200) siguió funcionando sin cambios contra el microservicio regenerado.

### Semana 3: aplicación Angular

Pantallazos actualizados con el diseño Bootstrap personalizado, tomados en http://localhost:4200 con la aplicación levantada con `docker compose up --build` y conectada a Oracle Autonomous Database. Las imágenes están en [`docs/evidencias/`](docs/evidencias).

**1. Listado de libros (GET /libros)**

![Listado de libros](docs/evidencias/01-listado.png)

**2. Nuevo libro: validaciones del formulario (año 0–2100 y campos obligatorios)**

![Validaciones del formulario](docs/evidencias/02-validaciones.png)

**3. Libro creado (POST /libros) → la app muestra el detalle con el ID generado por Oracle**

![Libro creado](docs/evidencias/03-libro-creado.png)

**4. El listado se actualiza con el libro nuevo (patrón Observer)**

![Listado con el libro nuevo](docs/evidencias/04-listado-con-nuevo.png)

**5. Buscar por ID (GET /libros/{id})**

![Buscar por ID](docs/evidencias/05-buscar-por-id.png)
![Detalle del libro](docs/evidencias/06-detalle.png)

**6. Editar libro (PUT /libros/{id})**

![Editar libro](docs/evidencias/07-editar.png)
![Libro actualizado](docs/evidencias/08-libro-actualizado.png)

**7. Eliminar libro (DELETE /libros/{id}): desaparece del listado**

![Libro eliminado](docs/evidencias/09-eliminado.png)

**8. Consultar el libro eliminado: el 404 del microservicio se muestra como mensaje legible**

![Error 404](docs/evidencias/10-error-404.png)

**9. Vista móvil (responsive)**

<img src="docs/evidencias/11-movil.png" alt="Vista móvil" width="320" />

**Contenedores corriendo (`docker compose ps`)**

```
NAME                  STATUS         PORTS
biblioteca-backend    Up 9 minutes   0.0.0.0:8080->8080/tcp
biblioteca-frontend   Up 7 minutes   0.0.0.0:4200->80/tcp
```

Log del backend dentro del contenedor:

```
HikariPool-1 - Start completed.
Database version: 19.33
Tomcat started on port 8080 (http) with context path '/'
Started BibliotecaApplication in 12.592 seconds
```

**Pruebas unitarias**

```
> npx ng test --watch=false
 Test Files  3 passed (3)
      Tests  19 passed (19)

> .\mvnw.cmd test
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Resultados de la verificación:

- `docker compose up --build`: el backend arranca con `HikariPool-1 - Start completed`, `Database version: 19.33` y `Started BibliotecaApplication` dentro del contenedor.
- Desde http://localhost:4200 se probó el CRUD completo contra Oracle: listar, buscar por ID, crear (con las validaciones de año 0–2100 y campos obligatorios), editar, eliminar, y el aviso del 404 al consultar el libro eliminado.
- `ng test`: 3 archivos y 19 pruebas aprobadas. `ng build`: sin errores. `mvnw test`: `Tests run: 6, Failures: 0` → `BUILD SUCCESS`.

### Semana 1: microservicio

**Base de datos en Oracle Cloud**

<img width="1406" height="688" alt="Base de datos FS3DB en Oracle Cloud" src="https://github.com/user-attachments/assets/23408b0a-a3e5-46cf-80d2-dccc908520bc" />

**Conexión a Oracle Autonomous Database (Oracle Cloud)**

- El alias `fs3db_tp` del wallet apunta a `adb.sa-santiago-1.oraclecloud.com`, puerto `1522`, protocolo `tcps` (mTLS).
- Log de inicio:

```
The following 1 profile is active: "local"
HikariPool-1 - Start completed.
Database version: 19.33
Started BibliotecaApplication in 5.732 seconds
```

**Tabla `LIBROS` generada por Hibernate (JPA) en Oracle**

```sql
create table libros (
    id number(19,0) generated by default as identity,
    anio_publicacion number(10,0) not null check ((anio_publicacion<=2100) and (anio_publicacion>=0)),
    autor varchar2(150 char) not null,
    genero varchar2(80 char) not null,
    titulo varchar2(200 char) not null,
    primary key (id)
)
```

**Pruebas de los endpoints en Postman**

<img width="1566" height="841" alt="Prueba en Postman 1" src="https://github.com/user-attachments/assets/eb52d6d0-5a31-43c0-b7b5-e917184c6844" />
<img width="1559" height="710" alt="Prueba en Postman 2" src="https://github.com/user-attachments/assets/d415da7d-2f7f-4a69-99c8-dbc40008d76f" />
<img width="1558" height="713" alt="Prueba en Postman 3" src="https://github.com/user-attachments/assets/e0644413-4e36-41b5-8b22-c7e293a92dd7" />
<img width="1555" height="726" alt="Prueba en Postman 4" src="https://github.com/user-attachments/assets/e3cf399d-4845-4a76-b279-add3ad65691c" />
<img width="1561" height="630" alt="Prueba en Postman 5" src="https://github.com/user-attachments/assets/397ebf3c-b67f-4a74-b87f-ebb46e7017c2" />

**Pruebas de los endpoints (curl)**

| Solicitud | HTTP | Respuesta |
|---|---|---|
| `POST /libros` | 201 | `{"id":5,"titulo":"Rayuela","autor":"Julio Cortazar","anioPublicacion":1963,"genero":"Novela"}` |
| `GET /libros/5` | 200 | `{"id":5,"titulo":"Rayuela",...}` |
| `PUT /libros/5` | 200 | `{"id":5,"titulo":"Rayuela (edicion conmemorativa)",...,"genero":"Novela experimental"}` |
| `DELETE /libros/5` | 204 | *(sin cuerpo)* |
| `GET /libros/5` | 404 | `{"status":404,"error":"No existe un libro con ID 5"}` |
