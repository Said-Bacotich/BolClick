# HU-01: clasificacion de vencimientos (issue #1)

`ClasificadorVencimientos` es una clase Java independiente de Android, la red y
el almacenamiento. Recibe `fechaVencimiento` en formato `yyyy-MM-dd`, tal como
el campo `date` del backend. No recibe ni calcula cantidades de lotes.

| Fecha de vencimiento | Estado |
| --- | --- |
| Anterior a hoy | `VENCIDO` |
| Desde hoy hasta hoy + 14 dias, inclusive | `PROXIMO_A_VENCER` |
| Posterior a hoy + 14 dias | `FUERA_DE_VENTANA` |
| Nula, vacia o solo espacios | `SIN_FECHA` |
| Formato incorrecto o fecha inexistente | `FECHA_INVALIDA` |

Los futuros recordatorios deben seleccionar unicamente `VENCIDO` y
`PROXIMO_A_VENCER`, en grupos separados. Los demas estados se excluyen.

## Uso desde la aplicacion

```java
import com.example.template.domain.vencimientos.ClasificadorVencimientos;
import com.example.template.domain.vencimientos.ClasificadorVencimientos.Estado;
import java.util.Calendar;

// Capturar hoy una vez antes de recorrer los lotes mantiene la misma referencia.
Calendar hoy = Calendar.getInstance();
Estado estado = ClasificadorVencimientos.clasificar(lote.getFechaVencimiento(), hoy);
```

Tambien existe `clasificar(fechaVencimiento)`, que obtiene el dia actual de la
zona horaria del dispositivo. La variante con `Calendar` permite fijar la fecha
en las pruebas. Se ignora la hora y no se modifica el calendario recibido.

## Pruebas automatizadas

Desde la carpeta `movil`, en PowerShell:

```powershell
.\gradlew.bat :app:testDebugUnitTest --no-daemon
```

`ClasificadorVencimientosTest` cubre fechas anteriores, hoy al final del dia,
fechas intermedias, limites de 14 y 15 dias, fechas ausentes e invalidas,
cambios de mes y anio, anio bisiesto y horario de verano. Tambien comprueba
que la clasificacion no modifica el calendario recibido.

Validacion realizada: compilacion con `javac --release 11` y ejecucion directa
con JUnit 4.13.2: **11 pruebas aprobadas**. El intento con Gradle no llego a
ejecutar las pruebas: quedo sin avanzar al iniciar su proceso daemon.

Este issue aporta la logica y sus pruebas. La pantalla y la presentacion de los
recordatorios se integraran en las tareas correspondientes de HU-01.
