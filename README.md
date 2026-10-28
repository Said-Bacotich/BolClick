# BolClick

Sistema web de inventario y ventas con varios comercios.

## Requisitos

- Node.js y npm.
- PostgreSQL.

## Configuración local

1. Crea una base de datos PostgreSQL llamada `mall_db`.
2. En una terminal de PowerShell, configura las variables de la base de datos y ejecuta el backend:

```powershell
Set-Location -LiteralPath .\backend
$env:DB_HOST = 'localhost'
$env:DB_PORT = '5432'
$env:DB_USERNAME = 'postgres'
$env:DB_PASSWORD = 'tu contraseña local de PostgreSQL'
$env:DB_DATABASE = 'mall_db'
$env:JWT_SECRET = 'elige-un-secreto-local'
npm ci
npm run start:dev
```

3. En otra terminal, ejecuta el frontend:

```powershell
Set-Location -LiteralPath .\frontend
npm ci
npm run dev
```

Abre la dirección local que Vite muestre en la terminal, normalmente `http://localhost:5173`.

El backend ofrece la documentación de la API en `http://localhost:3000/api/docs`. En desarrollo, TypeORM crea el esquema cuando se inicia el backend.

## Cuenta superadministradora opcional

El registro de un comercio crea una cuenta propietaria que queda pendiente de aprobación. Para crear una cuenta superadministradora local, configura `DB_PASSWORD`, `SUPERADMIN_EMAIL` y `SUPERADMIN_PASSWORD` en la terminal y ejecuta:

```powershell
node .\seed_superadmin.js
```

No guardes contraseñas reales ni datos locales en el repositorio.