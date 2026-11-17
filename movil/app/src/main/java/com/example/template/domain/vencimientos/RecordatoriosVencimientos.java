package com.example.template.domain.vencimientos;

import com.example.template.network.models.LoteIngreso;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Selecciona exclusivamente recordatorios de las sucursales permitidas. */
public final class RecordatoriosVencimientos {
    private final List<LoteIngreso> proximos;
    private final List<LoteIngreso> vencidos;

    private RecordatoriosVencimientos(List<LoteIngreso> proximos, List<LoteIngreso> vencidos) {
        this.proximos = Collections.unmodifiableList(proximos);
        this.vencidos = Collections.unmodifiableList(vencidos);
    }

    public static RecordatoriosVencimientos agrupar(List<LoteIngreso> lotes, Calendar hoy,
                                                    String rol, String sucursalId) {
        List<LoteIngreso> proximos = new ArrayList<>();
        List<LoteIngreso> vencidos = new ArrayList<>();
        boolean todas = "OWNER".equalsIgnoreCase(rol) || "SUPER_ADMIN".equalsIgnoreCase(rol);
        for (LoteIngreso lote : lotes) {
            if (lote == null || lote.getSucursalId() == null) continue;
            if (!todas && (sucursalId == null || sucursalId.trim().isEmpty()
                    || !sucursalId.equals(lote.getSucursalId()))) continue;
            ClasificadorVencimientos.Estado estado =
                    ClasificadorVencimientos.clasificar(lote.getFechaVencimiento(), hoy);
            if (estado == ClasificadorVencimientos.Estado.VENCIDO) vencidos.add(lote);
            if (estado == ClasificadorVencimientos.Estado.PROXIMO_A_VENCER) proximos.add(lote);
        }
        Comparator<LoteIngreso> porFecha = Comparator.comparing(LoteIngreso::getFechaVencimiento);
        proximos.sort(porFecha);
        vencidos.sort(porFecha);
        return new RecordatoriosVencimientos(proximos, vencidos);
    }

    public List<LoteIngreso> getProximos() { return proximos; }
    public List<LoteIngreso> getVencidos() { return vencidos; }
    public boolean isEmpty() { return proximos.isEmpty() && vencidos.isEmpty(); }
}
