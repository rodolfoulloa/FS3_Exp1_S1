import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LibroFacade } from './facades/libro-facade';

/**
 * Componente raíz: encabezado, navegación, aviso de errores y el
 * `router-outlet` donde se muestran las vistas de libros.
 */
@Component({
  selector: 'app-root',
  imports: [AsyncPipe, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App {
  protected readonly facade = inject(LibroFacade);
}
