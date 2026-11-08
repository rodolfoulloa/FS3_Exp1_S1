# Frontend — Biblioteca (Angular 22)

Aplicación web que gestiona los libros consumiendo el microservicio de `../backend`.
La documentación completa (arquetipo MVC, patrones de diseño y cómo ejecutar) está en el
[README de la raíz](../README.md).

```powershell
npm install
npm start                  # ng serve en http://localhost:4200 (proxy /api -> http://localhost:8080)
npx ng test --watch=false  # pruebas unitarias con Vitest
npm run build              # compilación de producción en dist/frontend/browser
```
