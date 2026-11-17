# HU-01: pruebas automatizadas de recordatorios

La suite `AceptacionRecordatoriosVencimientosTest` comprueba los criterios de
HU-01 en dos niveles: la clasificacion de cada fecha y su inclusion o exclusion
en los grupos que consume la pantalla. Cada caso verifica ambos grupos y el
estado vacio, tanto para `OWNER` como para `VENDEDOR` con sucursal autorizada.

La referencia es **17/11/2026, 23:59:59, America/La_Paz**, fijada en el test.
Los resultados no dependen de la fecha real del equipo ni de un servidor.
Las expectativas se declaran de forma explicita, sin obtenerlas de la logica
de produccion.

| Criterio | Casos | Clasificacion | Grupo |
| --- | --- | --- | --- |
| Fechas anteriores a hoy | Hace un anio, ayer | Vencido | Solo vencidos |
| Vencimiento hoy | Dia 0 | Proximo a vencer | Solo proximos |
| Dentro de la ventana | Dias 1, 7 y 13 | Proximo a vencer | Solo proximos |
| Limite superior incluido | Dia 14 (01/12/2026) | Proximo a vencer | Solo proximos |
| Despues del limite | Dia 15, proximo anio | Fuera de ventana | Ninguno |
| Sin fecha | `null`, vacia, espacios | Sin fecha | Ninguno |
| Fecha inexistente | 31/11/2026 | Fecha invalida | Ninguno |

La matriz contiene **13 casos parametrizados**. Complementa las 11 pruebas de
`ClasificadorVencimientosTest` y las 5 de `RecordatoriosVencimientosTest`, que
cubren cambios de mes y anio, anios bisiestos, horario de verano, ordenamiento y
acceso por sucursal.

## Ejecucion

Desde `movil`, con el SDK local configurado y el JDK requerido por el proyecto:

```powershell
.\gradlew.bat :app:testDebugUnitTest --no-daemon
```

No requiere emulador. El informe HTML se genera en
`app/build/reports/tests/testDebugUnitTest/index.html`.

Las pruebas relacionadas de aislamiento en el servidor se ejecutan desde
`backend`:

```powershell
npm test -- --runInBand sourcing.vencimientos.spec.ts jwt.strategy.spec.ts
```

Las pruebas de interfaz y sus instrucciones estan descritas en
[Seccion de recordatorios](HU-01-seccion-recordatorios.md).

## Resultado de esta verificacion

El 17 de noviembre de 2026:

- `:app:testDebugUnitTest`: **30 pruebas aprobadas**, incluidas las 13 de la
  matriz de aceptacion, 16 previas de HU-01 y la prueba de ejemplo del proyecto.
- Suites relacionadas del backend: **10 pruebas aprobadas** en 2 suites.

Este issue anade pruebas y documentacion; no modifica la logica de produccion.
