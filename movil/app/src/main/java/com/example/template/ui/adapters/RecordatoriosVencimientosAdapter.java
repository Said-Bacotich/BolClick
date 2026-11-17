package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.template.R;
import com.example.template.domain.vencimientos.RecordatoriosVencimientos;
import com.example.template.network.models.LoteIngreso;
import java.util.ArrayList;
import java.util.List;

public final class RecordatoriosVencimientosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CABECERA = 0, VACIO = 1, LOTE = 2;
    private final List<Fila> filas = new ArrayList<>();

    public void mostrar(RecordatoriosVencimientos datos) {
        filas.clear();
        agregarGrupo(datos.getProximos(), false, R.string.vencimientos_proximos, R.string.vencimientos_sin_proximos);
        agregarGrupo(datos.getVencidos(), true, R.string.vencimientos_vencidos, R.string.vencimientos_sin_vencidos);
        notifyDataSetChanged();
    }

    private void agregarGrupo(List<LoteIngreso> lotes, boolean vencido, int titulo, int vacio) {
        filas.add(new Fila(CABECERA, titulo, lotes.size(), null, vencido));
        if (lotes.isEmpty()) filas.add(new Fila(VACIO, vacio, 0, null, vencido));
        for (LoteIngreso lote : lotes) filas.add(new Fila(LOTE, 0, 0, lote, vencido));
    }

    @Override public int getItemViewType(int position) { return filas.get(position).tipo; }
    @Override public int getItemCount() { return filas.size(); }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int tipo) {
        if (tipo == LOTE) {
            return new LoteHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recordatorio_vencimiento, parent, false));
        }
        TextView texto = new TextView(parent.getContext());
        texto.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        int espacio = (int) (16 * parent.getResources().getDisplayMetrics().density);
        texto.setPadding(0, espacio, 0, espacio);
        texto.setTextSize(tipo == CABECERA ? 20 : 16);
        texto.setTextColor(parent.getContext().getColor(R.color.text_primary));
        if (tipo == CABECERA) texto.setTypeface(null, android.graphics.Typeface.BOLD);
        ViewCompat.setAccessibilityHeading(texto, tipo == CABECERA);
        return new RecyclerView.ViewHolder(texto) {};
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Fila fila = filas.get(position);
        android.content.Context context = holder.itemView.getContext();
        if (fila.tipo != LOTE) {
            ((TextView) holder.itemView).setText(fila.tipo == CABECERA
                    ? context.getString(fila.texto, fila.total) : context.getString(fila.texto));
            return;
        }
        LoteHolder vista = (LoteHolder) holder;
        LoteIngreso lote = fila.lote;
        String producto = lote.getProducto() != null ? lote.getProducto().getName() : null;
        String sucursal = lote.getSucursal() != null ? lote.getSucursal().getName() : null;
        vista.producto.setText(context.getString(R.string.vencimientos_producto,
                producto == null || producto.trim().isEmpty()
                        ? context.getString(R.string.vencimientos_producto_desconocido) : producto));
        vista.sucursal.setText(context.getString(R.string.vencimientos_sucursal,
                sucursal == null || sucursal.trim().isEmpty()
                        ? context.getString(R.string.vencimientos_sucursal_desconocida) : sucursal));
        String fecha = lote.getFechaVencimiento();
        vista.fecha.setText(context.getString(R.string.vencimientos_fecha,
                fecha.substring(8, 10) + "/" + fecha.substring(5, 7) + "/" + fecha.substring(0, 4)));
        vista.estado.setText(fila.vencido ? R.string.vencimientos_estado_vencido : R.string.vencimientos_estado_proximo);
        vista.estado.setTextColor(android.graphics.Color.parseColor(fila.vencido ? "#B91C1C" : "#92400E"));
    }

    private static final class Fila {
        final int tipo, texto, total;
        final LoteIngreso lote;
        final boolean vencido;
        Fila(int tipo, int texto, int total, LoteIngreso lote, boolean vencido) {
            this.tipo = tipo; this.texto = texto; this.total = total;
            this.lote = lote; this.vencido = vencido;
        }
    }

    private static final class LoteHolder extends RecyclerView.ViewHolder {
        final TextView producto, sucursal, fecha, estado;
        LoteHolder(View view) {
            super(view);
            producto = view.findViewById(R.id.recordatorioProducto);
            sucursal = view.findViewById(R.id.recordatorioSucursal);
            fecha = view.findViewById(R.id.recordatorioFecha);
            estado = view.findViewById(R.id.recordatorioEstado);
        }
    }
}
