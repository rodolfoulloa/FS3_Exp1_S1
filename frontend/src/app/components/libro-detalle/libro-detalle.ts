import { AsyncPipe } from '@angular/common';
import { Component, effect, inject, input, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LibroFacade } from '../../facades/libro-facade';

/**
 * VISTA: detalle de un libro (arquetipo MVC)
 *
 * Muestra un libro obtenido con GET /libros/{id}. El `id` llega desde la URL
 * (/libros/:id) gracias a `withComponentInputBinding()`. El componente solo
 * pide el libro al facade y lo muestra con el pipe `async` (Observer).
 */
@Component({
  selector: 'app-libro-detalle',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './libro-detalle.html',
})
export class LibroDetalle {
  protected readonly facade = inject(LibroFacade);

  /** Parámetro :id de la ruta. */
  readonly id = input.required<string>();

  constructor() {
    // Cada vez que cambia el :id de la URL se consulta el libro correspondiente.
    effect(() => {
      const id = Number(this.id());
      untracked(() => this.facade.seleccionar(id));
    });
  }
}
