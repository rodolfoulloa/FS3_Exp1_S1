import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, EMPTY, Observable, catchError, defer, finalize, tap } from 'rxjs';
import { Libro, LibroDatos } from '../models/libro';
import { LibroApi } from '../services/libro-api';

/**
 * FACADE DE LIBROS (rol de Controlador en el arquetipo MVC)
 *
 * Es el único punto de entrada que usan las vistas (componentes). Oculta la
 * complejidad del acceso HTTP, el manejo del estado y de los errores, y
 * entrega una interfaz simple: cargar, seleccionar, agregar, actualizar y
 * eliminar libros.
 *
 * Patrones de diseño:
 *  - Facade: simplifica el uso del subsistema (LibroApi + estado + errores).
 *  - Observer: expone el estado como Observables (`libros$`, `cargando$`,
 *    `error$`, `seleccionado$`) respaldados por `BehaviorSubject`. Las vistas
 *    se suscriben con el pipe `async` y se actualizan solas cuando cambia el estado.
 *  - Singleton + Decorator: `@Injectable({ providedIn: 'root' })`, una sola
 *    instancia comparte el mismo estado entre todas las vistas.
 */
@Injectable({ providedIn: 'root' })
export class LibroFacade {
  private readonly api = inject(LibroApi);

  // Estado interno (sujetos observables). Solo el facade puede emitir valores.
  private readonly librosSubject = new BehaviorSubject<Libro[]>([]);
  private readonly cargandoSubject = new BehaviorSubject<boolean>(false);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);
  private readonly seleccionadoSubject = new BehaviorSubject<Libro | null>(null);

  // Estado público de solo lectura para las vistas.
  readonly libros$: Observable<Libro[]> = this.librosSubject.asObservable();
  readonly cargando$: Observable<boolean> = this.cargandoSubject.asObservable();
  readonly error$: Observable<string | null> = this.errorSubject.asObservable();
  readonly seleccionado$: Observable<Libro | null> = this.seleccionadoSubject.asObservable();

  /** Carga (o recarga) la lista completa de libros: GET /libros. */
  cargarLibros(): void {
    this.ejecutar(this.api.listar()).subscribe((libros) => this.librosSubject.next(libros));
  }

  /** Carga un libro para la vista de detalle: GET /libros/{id}. */
  seleccionar(id: number): void {
    this.seleccionadoSubject.next(null);
    this.obtener(id).subscribe((libro) => this.seleccionadoSubject.next(libro));
  }

  /** Obtiene un libro por su ID (por ejemplo, para editarlo): GET /libros/{id}. */
  obtener(id: number): Observable<Libro> {
    return this.ejecutar(this.api.obtenerPorId(id));
  }

  /** Agrega un libro y lo suma a la lista observada: POST /libros. */
  agregar(datos: LibroDatos): Observable<Libro> {
    return this.ejecutar(this.api.crear(datos)).pipe(
      tap((creado) => this.librosSubject.next([...this.librosSubject.value, creado])),
    );
  }

  /** Actualiza un libro y lo reemplaza en la lista observada: PUT /libros/{id}. */
  actualizar(id: number, datos: LibroDatos): Observable<Libro> {
    return this.ejecutar(this.api.actualizar(id, datos)).pipe(
      tap((actualizado) => {
        this.librosSubject.next(
          this.librosSubject.value.map((l) => (l.id === id ? actualizado : l)),
        );
        if (this.seleccionadoSubject.value?.id === id) {
          this.seleccionadoSubject.next(actualizado);
        }
      }),
    );
  }

  /** Elimina un libro y lo quita de la lista observada: DELETE /libros/{id}. */
  eliminar(id: number): Observable<void> {
    return this.ejecutar(this.api.eliminar(id)).pipe(
      tap(() => {
        this.librosSubject.next(this.librosSubject.value.filter((l) => l.id !== id));
        if (this.seleccionadoSubject.value?.id === id) {
          this.seleccionadoSubject.next(null);
        }
      }),
    );
  }

  /** Oculta el mensaje de error actual. */
  limpiarError(): void {
    this.errorSubject.next(null);
  }

  /**
   * Envuelve una llamada a la API: activa el indicador de carga, limpia el error
   * anterior y, si la llamada falla, publica un mensaje legible en `error$` y
   * termina el flujo sin propagar la excepción a la vista.
   */
  private ejecutar<T>(peticion: Observable<T>): Observable<T> {
    // defer: el estado cambia recién cuando alguien se suscribe (cuando parte la petición).
    return defer(() => {
      this.errorSubject.next(null);
      this.cargandoSubject.next(true);
      return peticion.pipe(
        catchError((err: unknown) => {
          this.errorSubject.next(this.traducirError(err));
          return EMPTY;
        }),
        finalize(() => this.cargandoSubject.next(false)),
      );
    });
  }

  /** Traduce la respuesta de error del microservicio a un mensaje para el usuario. */
  private traducirError(err: unknown): string {
    if (!(err instanceof HttpErrorResponse)) {
      return 'Ocurrió un error inesperado.';
    }
    const cuerpo = err.error as { error?: string; detalle?: Record<string, string> } | null;
    switch (err.status) {
      case 0:
      case 502:
      case 503:
      case 504:
        return 'No se pudo conectar con el microservicio de libros. Verifica que esté en ejecución.';
      case 400: {
        // El backend responde {"error": "Datos inválidos", "detalle": {campo: mensaje}}
        const detalle = cuerpo?.detalle ? Object.values(cuerpo.detalle).join('. ') : '';
        return detalle ? `Datos inválidos: ${detalle}.` : 'Datos inválidos.';
      }
      case 404:
        // El backend responde {"error": "No existe un libro con ID X"}
        return cuerpo?.error ?? 'El libro solicitado no existe.';
      default:
        return `Error del servidor (${err.status}). Intenta nuevamente.`;
    }
  }
}
