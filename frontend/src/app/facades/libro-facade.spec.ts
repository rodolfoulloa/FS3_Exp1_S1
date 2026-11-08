import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom, of, throwError } from 'rxjs';
import { Libro, LibroDatos } from '../models/libro';
import { LibroApi } from '../services/libro-api';
import { LibroFacade } from './libro-facade';

describe('LibroFacade', () => {
  let facade: LibroFacade;
  let api: {
    listar: ReturnType<typeof vi.fn>;
    obtenerPorId: ReturnType<typeof vi.fn>;
    crear: ReturnType<typeof vi.fn>;
    actualizar: ReturnType<typeof vi.fn>;
    eliminar: ReturnType<typeof vi.fn>;
  };

  const rayuela: Libro = {
    id: 1,
    titulo: 'Rayuela',
    autor: 'Julio Cortázar',
    anioPublicacion: 1963,
    genero: 'Novela',
  };
  const principito: Libro = {
    id: 2,
    titulo: 'El Principito',
    autor: 'Antoine de Saint-Exupéry',
    anioPublicacion: 1943,
    genero: 'Fábula',
  };

  /** Valor actual de la lista observada por las vistas. */
  const librosActuales = () => firstValueFrom(facade.libros$);
  const errorActual = () => firstValueFrom(facade.error$);

  beforeEach(() => {
    api = {
      listar: vi.fn(() => of([rayuela, principito])),
      obtenerPorId: vi.fn(),
      crear: vi.fn(),
      actualizar: vi.fn(),
      eliminar: vi.fn(),
    };
    TestBed.configureTestingModule({
      providers: [{ provide: LibroApi, useValue: api }],
    });
    facade = TestBed.inject(LibroFacade);
  });

  it('cargarLibros() publica la lista recibida del microservicio', async () => {
    facade.cargarLibros();

    expect(api.listar).toHaveBeenCalledTimes(1);
    expect(await librosActuales()).toEqual([rayuela, principito]);
    expect(await firstValueFrom(facade.cargando$)).toBe(false);
  });

  it('notifica a los observadores cada vez que cambia la lista', () => {
    const emisiones: Libro[][] = [];
    facade.libros$.subscribe((l) => emisiones.push(l));

    facade.cargarLibros();

    expect(emisiones).toEqual([[], [rayuela, principito]]);
  });

  it('agregar() suma el libro creado a la lista', async () => {
    facade.cargarLibros();
    const datos: LibroDatos = {
      titulo: 'Ficciones',
      autor: 'Jorge Luis Borges',
      anioPublicacion: 1944,
      genero: 'Cuento',
    };
    const creado: Libro = { id: 3, ...datos };
    api.crear.mockReturnValue(of(creado));

    const resultado = await firstValueFrom(facade.agregar(datos));

    expect(api.crear).toHaveBeenCalledWith(datos);
    expect(resultado).toEqual(creado);
    expect(await librosActuales()).toEqual([rayuela, principito, creado]);
  });

  it('actualizar() reemplaza el libro en la lista', async () => {
    facade.cargarLibros();
    const cambios: LibroDatos = { ...rayuela, genero: 'Novela experimental' };
    const actualizado: Libro = { ...rayuela, genero: 'Novela experimental' };
    api.actualizar.mockReturnValue(of(actualizado));

    await firstValueFrom(facade.actualizar(1, cambios));

    expect(api.actualizar).toHaveBeenCalledWith(1, cambios);
    expect(await librosActuales()).toEqual([actualizado, principito]);
  });

  it('eliminar() quita el libro de la lista', async () => {
    facade.cargarLibros();
    api.eliminar.mockReturnValue(of(undefined));

    facade.eliminar(1).subscribe();

    expect(api.eliminar).toHaveBeenCalledWith(1);
    expect(await librosActuales()).toEqual([principito]);
  });

  it('seleccionar() publica el libro para la vista de detalle', async () => {
    api.obtenerPorId.mockReturnValue(of(principito));

    facade.seleccionar(2);

    expect(api.obtenerPorId).toHaveBeenCalledWith(2);
    expect(await firstValueFrom(facade.seleccionado$)).toEqual(principito);
  });

  it('traduce el 404 del backend al mensaje del microservicio', async () => {
    api.obtenerPorId.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
            error: { status: 404, error: 'No existe un libro con ID 99' },
          }),
      ),
    );

    facade.seleccionar(99);

    expect(await firstValueFrom(facade.seleccionado$)).toBeNull();
    expect(await errorActual()).toBe('No existe un libro con ID 99');
    expect(await firstValueFrom(facade.cargando$)).toBe(false);
  });

  it('no modifica la lista si eliminar() falla con 404', async () => {
    facade.cargarLibros();
    api.eliminar.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404, error: { error: 'No existe un libro con ID 1' } })),
    );

    facade.eliminar(1).subscribe();

    expect(await librosActuales()).toEqual([rayuela, principito]);
    expect(await errorActual()).toBe('No existe un libro con ID 1');
  });

  it('traduce el 400 con el detalle de las validaciones', async () => {
    api.crear.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
            error: { error: 'Datos inválidos', detalle: { titulo: 'El título es obligatorio' } },
          }),
      ),
    );

    facade.agregar({ ...rayuela, titulo: '' }).subscribe();

    expect(await errorActual()).toBe('Datos inválidos: El título es obligatorio.');
  });

  it('informa cuando el microservicio no está disponible', async () => {
    api.listar.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 0 })));

    facade.cargarLibros();

    expect(await errorActual()).toContain('No se pudo conectar con el microservicio');
  });

  it('limpiarError() borra el mensaje de error', async () => {
    api.listar.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    facade.cargarLibros();

    facade.limpiarError();

    expect(await errorActual()).toBeNull();
  });
});
