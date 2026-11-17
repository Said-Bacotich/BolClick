# HU-01: seccion de recordatorios de vencimiento

## Comportamiento

En el menu lateral, **Ventas y Operaciones > Recordatorios de vencimiento**
abre la nueva pantalla. La entrada utiliza el permiso existente `sourcing.ver`.
La pantalla se actualiza al abrirla, al volver a primer plano y con **Actualizar**.

- Separa **Proximos a vencer** (hoy a hoy + 14 dias, inclusive) de **Ya vencidos**.
- Cada registro muestra producto, sucursal, fecha de vencimiento y estado.
- Ordena cada grupo por fecha, de la mas antigua a la mas reciente.
- Excluye fechas ausentes, invalidas y posteriores a la ventana.
- Muestra carga, mensaje sin resultados y error con **Reintentar**.
- Cuando solo un grupo esta vacio, muestra un mensaje propio dentro de ese grupo.
- No calcula ni presenta cantidad restante. Los numeros de los encabezados
  representan el numero de lotes del grupo.
- Cancela la peticion al destruir la vista e ignora respuestas tardias.

## Acceso y contrato

La app consulta `GET /api/sourcing/vencimientos`, con el token y tenant habituales.
Es necesario desplegar los cambios de backend junto con la app.

El servidor aplica el tenant autenticado y la sucursal del usuario antes de
devolver los datos. `OWNER` y `SUPER_ADMIN` pueden consultar todas las sucursales
de su tenant; los demas roles solo su sucursal asignada. Sin asignacion se
devuelve una lista vacia. El guard conserva el permiso `sourcing.ver`.

La estrategia JWT expone la sucursal actual y el rol actual de la base de datos.
La app vuelve a filtrar por sucursal como defensa adicional. No necesita el
permiso `sucursales.ver` para resolver el nombre de la sucursal.

Ejemplo de respuesta:

```json
[
  {
    "id": "id-del-lote",
    "fechaVencimiento": "2026-11-17",
    "sucursal_id": "id-de-sucursal",
    "producto": { "name": "Leche" },
    "sucursal": { "name": "Centro" }
  }
]
```

La respuesta omite stock y cantidades. La clasificacion por dia se realiza en
la app, con la fecha local del dispositivo, usando la logica del issue #1.

## Verificacion

Backend, desde `backend`:

```powershell
npm ci --ignore-scripts
npm test -- --runInBand
npm run build
```

Android, desde `movil`, con SDK configurado en `local.properties`, JDK 21 de
JetBrains y Android Build Tools 36.0.0:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
# Requiere un emulador o dispositivo de pruebas conectado:
.\gradlew.bat :app:connectedDebugAndroidTest
```

Las pruebas nuevas cubren agrupacion y aislamiento por sucursal, respuesta sin
cantidades, rol actual del usuario, campos de cada fila, grupos vacios, carga,
error de red, reintento, error de permisos y cancelacion al salir.

Validacion realizada el 17 de noviembre de 2026:

- Backend: 40 pruebas aprobadas en 6 suites; compilacion completada.
- Android: 17 pruebas unitarias aprobadas; APK debug y APK de pruebas generados.
- Interfaz: 6 pruebas aprobadas en el emulador Pixel 10 Pro con Android API 37.

Para esta verificacion se uso temporalmente el SDK instalado en `C:/Sdk`.
Se restauro `local.properties` a su contenido anterior para no incluir una
ruta especifica de esta maquina entre los cambios del issue.

El contenedor `RecordatoriosTestActivity` y su manifest se incluyen solo en la
variante debug. Las pruebas de pantalla usan una API controlada, sin acceder a
un servidor real ni modificar la sesion del usuario.

Para permitir compilar el backend se anadio `@types/pg` y se trataron como cero
los `rowCount` nulos en las comprobaciones existentes de pre-migracion. No se
ejecutaron migraciones ni se conecto a una base de datos para verificar este issue.
