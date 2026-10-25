# FS3_Exp1_S1 — Microservicio Biblioteca

DSY2205 Desarrollo Full Stack III · Experiencia 1 · Semana 1 — *"Desarrollando nuestro microservicio"*

Microservicio en **Java 21 + Spring Boot 3** para gestionar libros de una biblioteca, usando **Spring Data JPA** y una base de datos **Oracle Autonomous Database (Oracle Cloud)**.

## Estructura

```
src/main/java/cl/duoc/biblioteca
├── BibliotecaApplication.java      # Clase principal
├── model/Libro.java                # Entidad JPA (tabla LIBROS)
├── repository/LibroRepository.java # Extiende JpaRepository
├── service/LibroService.java       # Interfaz con operaciones CRUD
├── service/LibroServiceImpl.java   # Implementación del CRUD
├── controller/LibroController.java # Controlador REST /libros
└── exception/                      # Manejo de errores (404, 400)
sql/crear_tabla_libros.sql          # Script opcional de la tabla + datos de prueba
postman/                            # Colección Postman para probar los endpoints
```

## Entidad Libro

| Atributo | Tipo | Columna |
|---|---|---|
| id | Long (autogenerado) | ID |
| titulo | String | TITULO |
| autor | String | AUTOR |
| anioPublicacion | Integer | ANIO_PUBLICACION |
| genero | String | GENERO |

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| GET | `/libros` | Lista todos los libros |
| GET | `/libros/{id}` | Obtiene un libro por su ID |
| POST | `/libros` | Agrega un nuevo libro |
| PUT | `/libros/{id}` | Actualiza un libro existente |
| DELETE | `/libros/{id}` | Elimina un libro por su ID |

Ejemplo de body (POST / PUT):

```json
{
  "titulo": "El Principito",
  "autor": "Antoine de Saint-Exupéry",
  "anioPublicacion": 1943,
  "genero": "Fábula"
}
```

## Configuración de Oracle Cloud

1. En Oracle Cloud crear una **Autonomous Database** (Always Free).
2. Descargar el **Wallet** (*Database connection → Download wallet*) y descomprimirlo en la carpeta `wallet/` en la raíz del proyecto (está en `.gitignore`, **no se sube**).
3. Revisar en `wallet/tnsnames.ora` el nombre del servicio (ej: `biblioteca_tp`).
4. Definir las variables de entorno (PowerShell):

```powershell
$env:DB_SERVICE="biblioteca_tp"
$env:TNS_ADMIN="C:/proyectos/FS3_Exp1_S1/wallet"
$env:DB_USER="ADMIN"
$env:DB_PASSWORD="tu_clave"
```

La tabla `LIBROS` se crea automáticamente al iniciar (`ddl-auto=update`). También se puede crear con `sql/crear_tabla_libros.sql`.

## Ejecutar

El proyecto incluye **Maven Wrapper**, por lo que no es necesario tener Maven instalado:

```powershell
.\mvnw.cmd spring-boot:run
```

O con el botón **Run** de VS Code. Luego probar con la colección `postman/Biblioteca.postman_collection.json` (Postman → Import).

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas del controlador usan Mockito y no requieren conexión a la base de datos.
