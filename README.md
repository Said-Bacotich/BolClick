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
## Plan SQA y pruebas

La carpeta `docs/sqa` contiene el Plan de Pruebas Ágil, la auditoría interna y el registro de casos. Las pruebas unitarias backend usan Jest:

```powershell
Set-Location -LiteralPath .\backend
npm ci
npm test -- --runInBand
npm run test:cov -- --runInBand
npm run build
```

Las pruebas de navegador usan Cypress y cubren la página pública, el acceso y los campos obligatorios del registro:

```powershell
Set-Location -LiteralPath .\frontend
npm ci
npm run dev -- --host 127.0.0.1 --port 5174 --strictPort
```

En otra terminal de `frontend`, ejecuta `npm run test:e2e`.

Las pruebas de integración backend requieren una base PostgreSQL desechable llamada `bolclick_test`; el script de seguridad rechaza `mall_db`. Configura `NODE_ENV=test`, `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DATABASE=bolclick_test` y `DB_SYNCHRONIZE=true` antes de `npm run test:e2e`. No ejecutes esas pruebas sobre datos reales. GitHub Actions ejecuta las suites en cada push y pull request.
