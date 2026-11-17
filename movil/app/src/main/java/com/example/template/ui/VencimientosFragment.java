package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.template.R;
import com.example.template.domain.vencimientos.RecordatoriosVencimientos;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.LoteIngreso;
import com.example.template.ui.adapters.RecordatoriosVencimientosAdapter;
import com.example.template.utils.SessionManager;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VencimientosFragment extends Fragment {
    private View progreso;
    private TextView mensaje;
    private Button actualizar;
    private RecyclerView lista;
    private RecordatoriosVencimientosAdapter adapter;
    private ApiService api;
    private SessionManager sesion;
    private Call<List<LoteIngreso>> peticion;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vencimientos, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progreso = view.findViewById(R.id.progresoVencimientos);
        mensaje = view.findViewById(R.id.mensajeVencimientos);
        actualizar = view.findViewById(R.id.btnActualizarVencimientos);
        lista = view.findViewById(R.id.listaVencimientos);
        adapter = new RecordatoriosVencimientosAdapter();
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));
        lista.setAdapter(adapter);
        sesion = crearSesion();
        api = crearApi();
        actualizar.setOnClickListener(v -> cargar());
    }

    protected SessionManager crearSesion() { return new SessionManager(requireContext()); }
    protected ApiService crearApi() { return ApiClient.getClient(requireContext()).create(ApiService.class); }

    @Override public void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        if (peticion != null) peticion.cancel();
        peticion = null;
        String rol = sesion.getRole();
        String sucursal = sesion.getSucursalId();
        if (!"OWNER".equalsIgnoreCase(rol) && !"SUPER_ADMIN".equalsIgnoreCase(rol)
                && (sucursal == null || sucursal.trim().isEmpty())) {
            estado(false, R.string.vencimientos_sin_sucursal, false);
            return;
        }
        estado(true, R.string.vencimientos_cargando, false);
        peticion = api.getVencimientos();
        peticion.enqueue(new Callback<List<LoteIngreso>>() {
            @Override public void onResponse(Call<List<LoteIngreso>> call, Response<List<LoteIngreso>> response) {
                if (call != peticion || getView() == null) return;
                peticion = null;
                if (!response.isSuccessful() || response.body() == null) {
                    estado(false, response.code() == 401 || response.code() == 403
                            ? R.string.vencimientos_sin_permiso : R.string.vencimientos_error, true);
                    return;
                }
                RecordatoriosVencimientos datos = RecordatoriosVencimientos.agrupar(
                        response.body(), Calendar.getInstance(), sesion.getRole(), sesion.getSucursalId());
                adapter.mostrar(datos);
                estado(false, datos.isEmpty() ? R.string.vencimientos_vacio : 0, false);
                lista.setVisibility(datos.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override public void onFailure(Call<List<LoteIngreso>> call, Throwable t) {
                if (call != peticion || getView() == null || call.isCanceled()) return;
                peticion = null;
                estado(false, R.string.vencimientos_error, true);
            }
        });
    }

    private void estado(boolean cargando, int texto, boolean error) {
        progreso.setVisibility(cargando ? View.VISIBLE : View.GONE);
        lista.setVisibility(View.GONE);
        mensaje.setVisibility(texto == 0 ? View.GONE : View.VISIBLE);
        if (texto != 0) mensaje.setText(texto);
        actualizar.setEnabled(!cargando);
        actualizar.setText(error ? R.string.vencimientos_reintentar : R.string.vencimientos_actualizar);
    }

    @Override public void onDestroyView() {
        if (peticion != null) peticion.cancel();
        peticion = null;
        lista.setAdapter(null);
        lista = null;
        adapter = null;
        progreso = null;
        mensaje = null;
        actualizar = null;
        super.onDestroyView();
    }
}
