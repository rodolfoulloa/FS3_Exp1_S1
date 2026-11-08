/**
 * Configuración de la aplicación.
 *
 * La URL es relativa: el frontend y la API quedan en el mismo origen, así no se
 * necesita CORS.
 *  - Con `ng serve`, proxy.conf.json redirige /api/** a http://localhost:8080/**.
 *  - Con Docker, nginx redirige /api/** al contenedor `backend` (puerto 8080).
 */
export const environment = {
  apiUrl: '/api/libros',
};
