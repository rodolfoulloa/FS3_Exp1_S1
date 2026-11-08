import { AsyncPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LibroFacade } from '../../facades/libro-facade';
import { Libro } from '../../models/libro';

/**
 * VISTA: listado de libros (arquetipo MVC)
 *
 * Muestra la tabla con todos los libros (GET /libros), permite buscar uno por
 * su ID y lleva a ver, editar o eliminar cada libro. No conoce HttpClient ni
 * la API: solo habla con el facade y lee su estado con el pipe `async` (Observer).
 *
 * Decorator: `@Component` agrega a la clase los metadatos que la convierten en
 * un componente de Angular (selector, plantilla, estilos e imports).
 */
@Component({
  selector: 'app-libro-lista',
  imports: [AsyncPipe, RouterLink, ReactiveFormsModule],
  templateUrl: './libro-lista.html',
})
export class LibroLista implements OnInit {
  protected readonly facade = inject(LibroFacade);
  private readonly router = inject(Router);

  /** Campo del buscador por ID. */
  protected readonly idBuscado = new FormControl<number | null>(null, [
    Validators.required,
    Validators.min(1),
  ]);

  ngOnInit(): void {
    this.facade.cargarLibros();
  }

  /** Lleva a la vista de detalle, que consulta GET /libros/{id}. */
  buscar(): void {
    if (this.idBuscado.invalid) {
      this.idBuscado.markAsTouched();
      return;
    }
    this.router.navigate(['/libros', this.idBuscado.value]);
  }

  /** Pide confirmación y elimina el libro (DELETE /libros/{id}). */
  eliminar(libro: Libro): void {
    if (libro.id != null && confirm(`¿Eliminar el libro "${libro.titulo}"?`)) {
      this.facade.eliminar(libro.id).subscribe();
    }
  }
}
