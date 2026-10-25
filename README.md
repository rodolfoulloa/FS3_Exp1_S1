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
Evidencia de pruebas en POSTMAN
<img width="1566" height="841" alt="imagen" src="https://github.com/user-attachments/assets/eb52d6d0-5a31-43c0-b7b5-e917184c6844" />
<img width="1559" height="710" alt="imagen" src="https://github.com/user-attachments/assets/d415da7d-2f7f-4a69-99c8-dbc40008d76f" />
<img width="1558" height="713" alt="imagen" src="https://github.com/user-attachments/assets/e0644413-4e36-41b5-8b22-c7e293a92dd7" />
<img width="1555" height="726" alt="imagen" src="https://github.com/user-attachments/assets/e3cf399d-4845-4a76-b279-add3ad65691c" />
<img width="1561" height="630" alt="imagen" src="https://github.com/user-attachments/assets/397ebf3c-b67f-4a74-b87f-ebb46e7017c2" />


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

**Alternativa sin variables de entorno:** crear `application-local.properties` en la raíz del proyecto (está en `.gitignore`, **no se sube**):

```properties
spring.datasource.url=jdbc:oracle:thin:@biblioteca_tp?TNS_ADMIN=C:/proyectos/FS3_Exp1_S1/wallet
spring.datasource.username=ADMIN
spring.datasource.password=tu_clave
```

y ejecutar con el perfil `local`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

La tabla `LIBROS` se crea automáticamente al iniciar (`ddl-auto=update`). También se puede crear con `sql/crear_tabla_libros.sql`.

Evidencia de Base de Datos Creada:
<img width="1406" height="688" alt="imagen" src="https://github.com/user-attachments/assets/23408b0a-a3e5-46cf-80d2-dccc908520bc" />


## Ejecutar

El proyecto incluye **Maven Wrapper**, por lo que no es necesario tener Maven instalado:

```powershell
.\mvnw.cmd spring-boot:run
```

O con el botón **Run** de VS Code. Luego probar con la colección `postman/Biblioteca.postman_collection.json` (Postman → Import → *Run collection*). Las solicitudes se ejecutan en orden: el POST guarda el ID creado en la variable `{{libroId}}` y cada solicitud valida su código HTTP.

También se puede ejecutar por consola con Newman:

```powershell
npx newman run .\postman\Biblioteca.postman_collection.json
```

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas del controlador usan Mockito y no requieren conexión a la base de datos.

## Evidencias

**Conexión a Oracle Autonomous Database (Oracle Cloud)**

- El alias `fs3db_tp` del wallet apunta a `adb.sa-santiago-1.oraclecloud.com`, puerto `1522`, protocolo `tcps` (mTLS).
- Con la aplicación corriendo, el proceso Java mantiene una conexión TCP establecida con `129.148.153.172:1522`, que es la IP a la que resuelve ese host.
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

**Pruebas de los endpoints (curl)**

| Solicitud | HTTP | Respuesta |
|---|---|---|
| `POST /libros` | 201 | `{"id":5,"titulo":"Rayuela","autor":"Julio Cortazar","anioPublicacion":1963,"genero":"Novela"}` |
| `GET /libros/5` | 200 | `{"id":5,"titulo":"Rayuela",...}` |
| `PUT /libros/5` | 200 | `{"id":5,"titulo":"Rayuela (edicion conmemorativa)",...,"genero":"Novela experimental"}` |
| `DELETE /libros/5` | 204 | *(sin cuerpo)* |
| `GET /libros/5` | 404 | `{"status":404,"error":"No existe un libro con ID 5"}` |

**Colección Postman (Newman):** 6 solicitudes y 8 aserciones ejecutadas, 0 fallidas.

**Pruebas unitarias:** `Tests run: 6, Failures: 0, Errors: 0` → `BUILD SUCCESS`.
