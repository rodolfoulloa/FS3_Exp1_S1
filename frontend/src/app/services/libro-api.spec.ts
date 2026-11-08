import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Libro, LibroDatos } from '../models/libro';
import { LibroApi } from './libro-api';

describe('LibroApi', () => {
  let api: LibroApi;
  let http: HttpTestingController;

  const datos: LibroDatos = {
    titulo: 'Rayuela',
    autor: 'Julio Cortázar',
    anioPublicacion: 1963,
    genero: 'Novela',
  };
  const libro: Libro = { id: 1, ...datos };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(LibroApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('es un singleton (misma instancia en toda la aplicación)', () => {
    expect(TestBed.inject(LibroApi)).toBe(api);
  });

  it('listar() hace GET /api/libros', () => {
    let resultado: Libro[] | undefined;
    api.listar().subscribe((r) => (resultado = r));

    const req = http.expectOne('/api/libros');
    expect(req.request.method).toBe('GET');
    req.flush([libro]);

    expect(resultado).toEqual([libro]);
  });

  it('obtenerPorId() hace GET /api/libros/{id}', () => {
    let resultado: Libro | undefined;
    api.obtenerPorId(1).subscribe((r) => (resultado = r));

    const req = http.expectOne('/api/libros/1');
    expect(req.request.method).toBe('GET');
    req.flush(libro);

    expect(resultado).toEqual(libro);
  });

  it('crear() hace POST /api/libros con el libro en el body', () => {
    let resultado: Libro | undefined;
    api.crear(datos).subscribe((r) => (resultado = r));

    const req = http.expectOne('/api/libros');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(datos);
    req.flush(libro, { status: 201, statusText: 'Created' });

    expect(resultado).toEqual(libro);
  });

  it('actualizar() hace PUT /api/libros/{id} con el libro en el body', () => {
    const cambios = { ...datos, genero: 'Novela experimental' };
    let resultado: Libro | undefined;
    api.actualizar(1, cambios).subscribe((r) => (resultado = r));

    const req = http.expectOne('/api/libros/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(cambios);
    req.flush({ id: 1, ...cambios });

    expect(resultado).toEqual({ id: 1, ...cambios });
  });

  it('eliminar() hace DELETE /api/libros/{id}', () => {
    let completado = false;
    api.eliminar(1).subscribe({ complete: () => (completado = true) });

    const req = http.expectOne('/api/libros/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null, { status: 204, statusText: 'No Content' });

    expect(completado).toBe(true);
  });
});
