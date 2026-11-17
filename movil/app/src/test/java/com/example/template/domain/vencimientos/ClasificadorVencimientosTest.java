package com.example.template.domain.vencimientos;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.example.template.domain.vencimientos.ClasificadorVencimientos.Estado.*;
import static com.example.template.domain.vencimientos.ClasificadorVencimientos.clasificar;
import static org.junit.Assert.assertEquals;

public class ClasificadorVencimientosTest {
    private Calendar fecha(int year, int month, int day) {
        Calendar hoy = new GregorianCalendar(TimeZone.getTimeZone("America/La_Paz"));
        hoy.clear();
        hoy.set(year, month - 1, day, 23, 59, 59);
        return hoy;
    }

    @Test
    public void fechasAnterioresEstanVencidas() {
        Calendar hoy = fecha(2026, 11, 17);
        assertEquals(VENCIDO, clasificar("2026-11-16", hoy));
        assertEquals(VENCIDO, clasificar("2025-01-01", hoy));
    }

    @Test
    public void hoyEstaIncluidoAunqueSeaElFinalDelDia() {
        assertEquals(PROXIMO_A_VENCER, clasificar("2026-11-17", fecha(2026, 11, 17)));
    }

    @Test
    public void diasIntermediosEstanIncluidos() {
        assertEquals(PROXIMO_A_VENCER, clasificar("2026-11-24", fecha(2026, 11, 17)));
    }

    @Test
    public void diaCatorceEstaIncluidoAlCambiarDeMes() {
        assertEquals(PROXIMO_A_VENCER, clasificar("2026-12-01", fecha(2026, 11, 17)));
    }

    @Test
    public void diaQuinceEstaFueraDeVentana() {
        assertEquals(FUERA_DE_VENTANA, clasificar("2026-12-02", fecha(2026, 11, 17)));
    }

    @Test
    public void lotesSinFechaSeExcluyen() {
        Calendar hoy = fecha(2026, 11, 17);
        assertEquals(SIN_FECHA, clasificar(null, hoy));
        assertEquals(SIN_FECHA, clasificar("", hoy));
        assertEquals(SIN_FECHA, clasificar("   ", hoy));
    }

    @Test
    public void fechasInvalidasNoSeNormalizanNiGeneranAlertas() {
        Calendar hoy = fecha(2026, 11, 17);
        for (String fecha : new String[]{"2026-02-29", "2026-11-31", "2026-13-01",
                "17/11/2026", "2026-1-01", "2026-11-17extra", "2026-11-17T00:00:00Z"}) {
            assertEquals(fecha, FECHA_INVALIDA, clasificar(fecha, hoy));
        }
    }

    @Test
    public void ventanaCruzaElCambioDeAnio() {
        Calendar hoy = fecha(2026, 12, 25);
        assertEquals(PROXIMO_A_VENCER, clasificar("2027-01-08", hoy));
        assertEquals(FUERA_DE_VENTANA, clasificar("2027-01-09", hoy));
    }

    @Test
    public void ventanaRespetaElAnioBisiesto() {
        Calendar hoy = fecha(2028, 2, 15);
        assertEquals(PROXIMO_A_VENCER, clasificar("2028-02-29", hoy));
        assertEquals(FUERA_DE_VENTANA, clasificar("2028-03-01", hoy));
    }

    @Test
    public void usaElDiaLocalYNoModificaElCalendarioRecibido() {
        Calendar hoy = fecha(2026, 11, 17);
        Calendar original = (Calendar) hoy.clone();
        assertEquals(PROXIMO_A_VENCER, clasificar("2026-11-17", hoy));
        assertEquals(original, hoy);
    }

    @Test
    public void ventanaRespetaCambiosDeHorarioDeVerano() {
        Calendar hoy = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
        hoy.clear();
        hoy.set(2026, Calendar.MARCH, 1, 23, 59);
        assertEquals(PROXIMO_A_VENCER, clasificar("2026-03-15", hoy));
        assertEquals(FUERA_DE_VENTANA, clasificar("2026-03-16", hoy));
    }
}
