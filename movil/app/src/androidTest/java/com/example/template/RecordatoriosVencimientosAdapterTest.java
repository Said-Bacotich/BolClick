package com.example.template;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.template.domain.vencimientos.RecordatoriosVencimientos;
import com.example.template.network.models.LoteIngreso;
import com.example.template.ui.adapters.RecordatoriosVencimientosAdapter;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecordatoriosVencimientosAdapterTest {
    private final Calendar hoy = new GregorianCalendar(2026, Calendar.NOVEMBER, 17);

    private Context context() {
        return new ContextThemeWrapper(InstrumentationRegistry.getInstrumentation()
                .getTargetContext(), R.style.Theme_Template);
    }

    private View fila(RecordatoriosVencimientosAdapter adapter, int posicion, Context context) {
        RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(new FrameLayout(context),
                adapter.getItemViewType(posicion));
        adapter.onBindViewHolder(holder, posicion);
        return holder.itemView;
    }

    @Test public void muestraGruposYLosCuatroCamposSinCantidad() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Context context = context();
            Gson gson = new Gson();
            LoteIngreso proximo = gson.fromJson("{\"sucursal_id\":\"a\","
                    + "\"fechaVencimiento\":\"2026-11-17\",\"cantidad\":98765,"
                    + "\"producto\":{\"name\":\"Leche\"},\"sucursal\":{\"name\":\"Centro\"}}", LoteIngreso.class);
            LoteIngreso vencido = new LoteIngreso("a", "p", null, 98765, "2026-11-16", null);
            RecordatoriosVencimientosAdapter adapter = new RecordatoriosVencimientosAdapter();
            adapter.mostrar(RecordatoriosVencimientos.agrupar(Arrays.asList(vencido, proximo), hoy, "OWNER", null));
            assertEquals(4, adapter.getItemCount());
            assertEquals(context.getString(R.string.vencimientos_proximos, 1),
                    ((TextView) fila(adapter, 0, context)).getText().toString());
            View registro = fila(adapter, 1, context);
            assertEquals("Producto: Leche", ((TextView) registro.findViewById(R.id.recordatorioProducto)).getText().toString());
            assertEquals("Sucursal: Centro", ((TextView) registro.findViewById(R.id.recordatorioSucursal)).getText().toString());
            assertEquals("Vencimiento: 17/11/2026", ((TextView) registro.findViewById(R.id.recordatorioFecha)).getText().toString());
            assertEquals("Estado: Próximo a vencer", ((TextView) registro.findViewById(R.id.recordatorioEstado)).getText().toString());
            assertEquals(4, ((android.view.ViewGroup) registro).getChildCount());
            assertEquals(context.getString(R.string.vencimientos_vencidos, 1),
                    ((TextView) fila(adapter, 2, context)).getText().toString());
            View antiguo = fila(adapter, 3, context);
            assertEquals("Estado: Vencido", ((TextView) antiguo.findViewById(R.id.recordatorioEstado)).getText().toString());
        });
    }

    @Test public void grupoSinResultadosTieneMensajePropio() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Context context = context();
            RecordatoriosVencimientosAdapter adapter = new RecordatoriosVencimientosAdapter();
            adapter.mostrar(RecordatoriosVencimientos.agrupar(Collections.singletonList(
                    new LoteIngreso("a", "p", null, 100, "2026-11-16", null)), hoy, "OWNER", null));
            assertEquals(context.getString(R.string.vencimientos_sin_proximos),
                    ((TextView) fila(adapter, 1, context)).getText().toString());
        });
    }
}
