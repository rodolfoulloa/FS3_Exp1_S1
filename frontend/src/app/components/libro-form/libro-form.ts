import { Component, DestroyRef, computed, effect, inject, input, untracked } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AsyncPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LibroFacade } from '../../facades/libro-facade';
import { Libro, LibroDatos } from '../../models/libro';

/**
 * VISTA: formulario para crear o editar un libro (arquetipo MVC)
 *
 *  - /libros/nuevo       -> crea un libro (POST /libros)
 *  - /libros/:id/editar  -> carga el libro (GET /libros/{id}) y lo actualiza (PUT /libros/{id})
 *
 * Usa formularios reactivos con las mismas validaciones del microservicio:
 * título, autor, año y género obligatorios, y año entre 0 y 2100.
 * Toda la comunicación con el backend pasa por el facade.
 */
@Component({
  selector: 'app-libro-form',
  imports: [AsyncPipe, ReactiveFormsModule, RouterLink],
  templateUrl: './libro-form.html',
})
export class LibroForm {
  protected readonly facade = inject(LibroFacade);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  /** Parámetro :id de la ruta (no viene al crear un libro). */
  readonly id = input<string>();

  /** true cuando el formulario está editando un libro existente. */
  protected readonly esEdicion = computed(() => this.id() != null);

  protected readonly form = inject(FormBuilder).nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(200)]],
    autor: ['', [Validators.required, Validators.maxLength(150)]],
    anioPublicacion: [
      null as number | null,
      [Validators.required, Validators.min(0), Validators.max(2100)],
    ],
    genero: ['', [Validators.required, Validators.maxLength(80)]],
  });

  constructor() {
    // En modo edición se carga el libro y se rellena el formulario.
    effect(() => {
      const id = this.id();
      if (id == null) {
        return;
      }
      untracked(() =>
        this.facade
          .obtener(Number(id))
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe((libro) => this.form.patchValue(libro)),
      );
    });
  }

  /** Indica si un campo debe mostrar su mensaje de error. */
  protected invalido(campo: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[campo];
    return control.invalid && (control.touched || control.dirty);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valores = this.form.getRawValue();
    const datos: LibroDatos = {
      titulo: valores.titulo.trim(),
      autor: valores.autor.trim(),
      anioPublicacion: Number(valores.anioPublicacion),
      genero: valores.genero.trim(),
    };

    const id = this.id();
    const operacion = id != null ? this.facade.actualizar(Number(id), datos) : this.facade.agregar(datos);

    // Si la operación falla, el facade publica el error y no se navega.
    operacion
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((libro: Libro) => this.router.navigate(['/libros', libro.id]));
  }
}
