/**
 * MODELO (arquetipo MVC)
 *
 * Representa un libro tal como lo expone el microservicio de la semana 1
 * (entidad JPA `Libro`, tabla LIBROS en Oracle). Tiene exactamente los mismos
 * 5 atributos: id, titulo, autor, anioPublicacion y genero.
 */
export interface Libro {
  /** ID autogenerado por la base de datos (no viene al crear un libro). */
  id?: number;
  titulo: string;
  autor: string;
  anioPublicacion: number;
  genero: string;
}

/** Datos que se envían en el body de POST y PUT (el ID va en la URL o lo genera la BD). */
export type LibroDatos = Omit<Libro, 'id'>;
