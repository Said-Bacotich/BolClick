package com.example.template.domain.vencimientos;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/** Clasifica fechas de lotes sin consultar stock, servicios ni componentes de Android. */
public final class ClasificadorVencimientos {
    public static final int DIAS_ANTICIPACION = 14;

    public enum Estado {
        VENCIDO,
        PROXIMO_A_VENCER,
        FUERA_DE_VENTANA,
        SIN_FECHA,
        FECHA_INVALIDA
    }

    private ClasificadorVencimientos() {
    }

    /** Usa el dia actual de la zona horaria del dispositivo. */
    public static Estado clasificar(String fechaVencimiento) {
        return clasificar(fechaVencimiento, Calendar.getInstance());
    }

    /**
     * Recibe la fecha del backend en formato yyyy-MM-dd. Hoy y hoy + 14 dias
     * pertenecen a PROXIMO_A_VENCER; solamente las fechas anteriores son VENCIDO.
     * La hora de hoy se ignora y el calendario recibido no se modifica.
     * SIN_FECHA y FECHA_INVALIDA deben excluirse de los recordatorios.
     */
    public static Estado clasificar(String fechaVencimiento, Calendar hoy) {
        Objects.requireNonNull(hoy, "hoy no puede ser null");
        if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
            return Estado.SIN_FECHA;
        }
        if (!fechaVencimiento.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return Estado.FECHA_INVALIDA;
        }

        // UTC sirve como eje de comparacion de dias, sin conversion de la fecha
        // del backend ni sumas de milisegundos afectadas por horario de verano.
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        formato.setTimeZone(utc);
        formato.setLenient(false);
        ParsePosition posicion = new ParsePosition(0);
        Date vencimiento = formato.parse(fechaVencimiento, posicion);
        if (vencimiento == null || posicion.getIndex() != fechaVencimiento.length()) {
            return Estado.FECHA_INVALIDA;
        }

        Calendar inicio = new GregorianCalendar(utc, Locale.ROOT);
        inicio.clear();
        inicio.set(hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
        if (vencimiento.before(inicio.getTime())) {
            return Estado.VENCIDO;
        }
        inicio.add(Calendar.DAY_OF_MONTH, DIAS_ANTICIPACION);
        return vencimiento.after(inicio.getTime())
                ? Estado.FUERA_DE_VENTANA : Estado.PROXIMO_A_VENCER;
    }
}
