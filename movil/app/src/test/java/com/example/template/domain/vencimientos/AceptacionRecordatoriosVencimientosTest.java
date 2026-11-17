package com.example.template.domain.vencimientos;

import com.example.template.network.models.LoteIngreso;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.example.template.domain.vencimientos.ClasificadorVencimientos.Estado;
import static com.example.template.domain.vencimientos.ClasificadorVencimientos.Estado.*;
import static org.junit.Assert.assertEquals;

/** Criterios de HU-01 verificados desde la fecha hasta los grupos de la pantalla. */
@RunWith(Parameterized.class)
public class AceptacionRecordatoriosVencimientosTest {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> criteriosHU01() {
        // Hoy se fija en 2026-11-17. Las expectativas son los criterios de la HU,
        // no se calculan utilizando la logica de produccion.
        return Arrays.asList(new Object[][] {
                {"Lote vencido hace un anio", "2025-11-17", VENCIDO, false, true},
                {"Lote vencido ayer", "2026-11-16", VENCIDO, false, true},
                {"Vence hoy: limite inferior incluido", "2026-11-17", PROXIMO_A_VENCER, true, false},
                {"Vence manana", "2026-11-18", PROXIMO_A_VENCER, true, false},
                {"Vence dentro de 7 dias", "2026-11-24", PROXIMO_A_VENCER, true, false},
                {"Vence dentro de 13 dias", "2026-11-30", PROXIMO_A_VENCER, true, false},
                {"Vence dentro de 14 dias: limite superior incluido", "2026-12-01", PROXIMO_A_VENCER, true, false},
                {"Vence dentro de 15 dias: excluido", "2026-12-02", FUERA_DE_VENTANA, false, false},
                {"Vence el proximo anio: excluido", "2027-11-17", FUERA_DE_VENTANA, false, false},
                {"Sin fecha: null", null, SIN_FECHA, false, false},
                {"Sin fecha: cadena vacia", "", SIN_FECHA, false, false},
                {"Sin fecha: solo espacios", "   ", SIN_FECHA, false, false},
                {"Fecha inexistente: excluida", "2026-11-31", FECHA_INVALIDA, false, false},
        });
    }

    private final String fechaVencimiento;
    private final Estado estadoEsperado;
    private final boolean debeEstarEnProximos;
    private final boolean debeEstarEnVencidos;

    public AceptacionRecordatoriosVencimientosTest(String criterio, String fechaVencimiento,
                                                  Estado estadoEsperado, boolean enProximos,
                                                  boolean enVencidos) {
        this.fechaVencimiento = fechaVencimiento;
        this.estadoEsperado = estadoEsperado;
        this.debeEstarEnProximos = enProximos;
        this.debeEstarEnVencidos = enVencidos;
    }

    @Test public void clasificacionYRecordatoriosCumplenElCriterioHU01() {
        Calendar hoy = new GregorianCalendar(TimeZone.getTimeZone("America/La_Paz"));
        hoy.clear();
        hoy.set(2026, Calendar.NOVEMBER, 17, 23, 59, 59);
        LoteIngreso lote = new LoteIngreso("sucursal-a", "producto-a", null,
                100, fechaVencimiento, null);

        assertEquals("Clasificacion de la fecha", estadoEsperado,
                ClasificadorVencimientos.clasificar(fechaVencimiento, hoy));

        // El mismo contrato se exige al administrador y al empleado con acceso.
        for (String rol : new String[]{"OWNER", "VENDEDOR"}) {
            RecordatoriosVencimientos recordatorios = RecordatoriosVencimientos.agrupar(
                    Collections.singletonList(lote), hoy, rol, "sucursal-a");
            assertEquals(rol + ": grupo de proximos", debeEstarEnProximos
                    ? Collections.singletonList(lote) : Collections.emptyList(), recordatorios.getProximos());
            assertEquals(rol + ": grupo de vencidos", debeEstarEnVencidos
                    ? Collections.singletonList(lote) : Collections.emptyList(), recordatorios.getVencidos());
            assertEquals(rol + ": estado vacio", !debeEstarEnProximos && !debeEstarEnVencidos,
                    recordatorios.isEmpty());
        }
    }
}
