package com.example.template.domain.vencimientos;

import com.example.template.network.models.LoteIngreso;
import org.junit.Test;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import static org.junit.Assert.*;

public class RecordatoriosVencimientosTest {
    private final Calendar hoy = new GregorianCalendar(2026, Calendar.NOVEMBER, 17);

    private LoteIngreso lote(String sucursal, String fecha) {
        return new LoteIngreso(sucursal, "producto", null, 100, fecha, null);
    }

    @Test public void agrupaPorEstadoYOrdenaSinModificarLaEntrada() {
        LoteIngreso catorce = lote("a", "2026-12-01");
        LoteIngreso ayer = lote("a", "2026-11-16");
        LoteIngreso actual = lote("a", "2026-11-17");
        java.util.List<LoteIngreso> entrada = Arrays.asList(catorce, ayer, actual,
                lote("a", "2026-12-02"), lote("a", null), lote("a", "fecha invalida"));
        RecordatoriosVencimientos datos = RecordatoriosVencimientos.agrupar(entrada, hoy, "OWNER", null);
        assertEquals(Arrays.asList(actual, catorce), datos.getProximos());
        assertEquals(Collections.singletonList(ayer), datos.getVencidos());
        assertSame(catorce, entrada.get(0));
        assertFalse(datos.isEmpty());
    }

    @Test public void empleadoSoloVeSuSucursalEnAmbosGrupos() {
        LoteIngreso actual = lote("a", "2026-11-17");
        LoteIngreso vencido = lote("a", "2026-11-16");
        RecordatoriosVencimientos datos = RecordatoriosVencimientos.agrupar(Arrays.asList(
                actual, vencido, lote("b", "2026-11-17"), lote("b", "2026-11-16")), hoy, "VENDEDOR", "a");
        assertEquals(Collections.singletonList(actual), datos.getProximos());
        assertEquals(Collections.singletonList(vencido), datos.getVencidos());
    }

    @Test public void sinSucursalNoSeAmpliaElAcceso() {
        for (String sucursal : new String[]{null, "", " "}) {
            assertTrue(RecordatoriosVencimientos.agrupar(Collections.singletonList(
                    lote("a", "2026-11-17")), hoy, "SUPERVISOR", sucursal).isEmpty());
        }
    }

    @Test public void administradoresVenTodasLasSucursales() {
        for (String rol : new String[]{"OWNER", "SUPER_ADMIN"}) {
            assertEquals(2, RecordatoriosVencimientos.agrupar(Arrays.asList(
                    lote("a", "2026-11-17"), lote("b", "2026-11-17")), hoy, rol, "a")
                    .getProximos().size());
        }
    }

    @Test public void soloFechasExcluidasProduceEstadoVacio() {
        assertTrue(RecordatoriosVencimientos.agrupar(Arrays.asList(
                lote("a", "2026-12-02"), lote("a", null), lote(null, "2026-11-17")),
                hoy, "OWNER", null).isEmpty());
        assertTrue(RecordatoriosVencimientos.agrupar(Collections.emptyList(), hoy, "OWNER", null).isEmpty());
    }
}
