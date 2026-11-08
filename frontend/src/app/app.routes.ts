import { Routes } from '@angular/router';
import { LibroDetalle } from './components/libro-detalle/libro-detalle';
import { LibroForm } from './components/libro-form/libro-form';
import { LibroLista } from './components/libro-lista/libro-lista';

/**
 * Rutas de la aplicación. El enrutamiento es parte de la capa de control del
 * arquetipo MVC: decide qué vista se muestra según la URL.
 * El parámetro `:id` llega a los componentes como input (withComponentInputBinding).
 */
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'libros' },
  { path: 'libros', component: LibroLista, title: 'Libros | Biblioteca' },
  { path: 'libros/nuevo', component: LibroForm, title: 'Nuevo libro | Biblioteca' },
  { path: 'libros/:id', component: LibroDetalle, title: 'Detalle del libro | Biblioteca' },
  { path: 'libros/:id/editar', component: LibroForm, title: 'Editar libro | Biblioteca' },
  { path: '**', redirectTo: 'libros' },
];
