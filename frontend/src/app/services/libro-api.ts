import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Libro, LibroDatos } from '../models/libro';

/**
 * SERVICIO DE ACCESO A LA API (capa Modelo del arquetipo MVC)
 *
 * Encapsula la comunicación HTTP con el microservicio de libros. Cada método
 * corresponde a uno de los 5 endpoints del CRUD.
 *
 * Patrones de diseño:
 *  - Decorator: `@Injectable` agrega a la clase los metadatos para que Angular
 *    la pueda inyectar, sin modificar la clase.
 *  - Singleton: `providedIn: 'root'` hace que exista una única instancia del
 *    servicio en toda la aplicación.
 *  - Observer: cada método devuelve un `Observable`; quien se suscribe es
 *    notificado cuando llega la respuesta del microservicio.
 */
@Injectable({ providedIn: 'root' })
export class LibroApi {
  private readonly http = inject(HttpClient);
  private readonly url = environment.apiUrl;

  /** GET /libros: obtiene todos los libros. */
  listar(): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.url);
  }

  /** GET /libros/{id}: obtiene un libro por su ID. */
  obtenerPorId(id: number): Observable<Libro> {
    return this.http.get<Libro>(`${this.url}/${id}`);
  }

  /** POST /libros: agrega un nuevo libro. */
  crear(libro: LibroDatos): Observable<Libro> {
    return this.http.post<Libro>(this.url, libro);
  }

  /** PUT /libros/{id}: actualiza un libro existente. */
  actualizar(id: number, libro: LibroDatos): Observable<Libro> {
    return this.http.put<Libro>(`${this.url}/${id}`, libro);
  }

  /** DELETE /libros/{id}: elimina un libro por su ID. */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
