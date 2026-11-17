package com.example.template;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.template.network.ApiService;
import com.example.template.network.models.LoteIngreso;
import com.example.template.testing.RecordatoriosTestActivity;
import com.example.template.ui.VencimientosFragment;
import com.example.template.utils.SessionManager;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Timeout;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class VencimientosFragmentTest {
    public static class FragmentPrueba extends VencimientosFragment {
        final List<PeticionPrueba> peticiones = new ArrayList<>();

        @Override protected SessionManager crearSesion() {
            return new SessionManager(requireContext()) {
                @Override public String getRole() { return "OWNER"; }
                @Override public String getSucursalId() { return null; }
            };
        }

        @Override protected ApiService crearApi() {
            return (ApiService) Proxy.newProxyInstance(ApiService.class.getClassLoader(),
                    new Class<?>[]{ApiService.class}, (proxy, method, args) -> {
                        if (!method.getName().equals("getVencimientos")) throw new AssertionError(method.getName());
                        PeticionPrueba peticion = new PeticionPrueba();
                        peticiones.add(peticion);
                        return peticion;
                    });
        }
    }

    private void abrir(RecordatoriosTestActivity activity, FragmentPrueba fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commitNow();
    }

    @Test public void muestraCargaYVacioAlCompletarSinResultados() {
        try (ActivityScenario<RecordatoriosTestActivity> escenario = ActivityScenario.launch(RecordatoriosTestActivity.class)) {
            escenario.onActivity(activity -> {
                FragmentPrueba fragment = new FragmentPrueba();
                abrir(activity, fragment);
                assertEquals(View.VISIBLE, activity.findViewById(R.id.progresoVencimientos).getVisibility());
                assertFalse(activity.findViewById(R.id.btnActualizarVencimientos).isEnabled());
                fragment.peticiones.get(0).responder(Response.success(Collections.emptyList()));
                assertEquals(View.GONE, activity.findViewById(R.id.progresoVencimientos).getVisibility());
                assertEquals(View.GONE, activity.findViewById(R.id.listaVencimientos).getVisibility());
                assertEquals(activity.getString(R.string.vencimientos_vacio),
                        ((TextView) activity.findViewById(R.id.mensajeVencimientos)).getText().toString());
            });
        }
    }

    @Test public void permiteReintentarYNoConservaDatosVisiblesAnteUnErrorDePermisos() {
        try (ActivityScenario<RecordatoriosTestActivity> escenario = ActivityScenario.launch(RecordatoriosTestActivity.class)) {
            escenario.onActivity(activity -> {
                FragmentPrueba fragment = new FragmentPrueba();
                abrir(activity, fragment);
                fragment.peticiones.get(0).fallar();
                Button boton = activity.findViewById(R.id.btnActualizarVencimientos);
                assertEquals(activity.getString(R.string.vencimientos_reintentar), boton.getText().toString());
                assertEquals(activity.getString(R.string.vencimientos_error),
                        ((TextView) activity.findViewById(R.id.mensajeVencimientos)).getText().toString());
                boton.performClick();
                fragment.peticiones.get(1).responder(Response.success(Collections.singletonList(
                        new LoteIngreso("a", "p", null, 200, "2000-01-01", null))));
                RecyclerView lista = activity.findViewById(R.id.listaVencimientos);
                assertEquals(View.VISIBLE, lista.getVisibility());
                assertEquals(4, lista.getAdapter().getItemCount());
                boton.performClick();
                assertEquals(View.GONE, lista.getVisibility());
                fragment.peticiones.get(2).responder(Response.error(403, ResponseBody.create(null, "")));
                assertEquals(View.GONE, lista.getVisibility());
                assertEquals(activity.getString(R.string.vencimientos_sin_permiso),
                        ((TextView) activity.findViewById(R.id.mensajeVencimientos)).getText().toString());
            });
        }
    }

    @Test public void alSalirCancelaLaPeticionEIgnoraRespuestasTardias() {
        try (ActivityScenario<RecordatoriosTestActivity> escenario = ActivityScenario.launch(RecordatoriosTestActivity.class)) {
            escenario.onActivity(activity -> {
                FragmentPrueba fragment = new FragmentPrueba();
                abrir(activity, fragment);
                PeticionPrueba pendiente = fragment.peticiones.get(0);
                activity.getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
                assertTrue(pendiente.isCanceled());
                pendiente.responder(Response.success(Collections.emptyList()));
                assertNull(fragment.getView());
            });
        }
    }

    private static class PeticionPrueba implements Call<List<LoteIngreso>> {
        Callback<List<LoteIngreso>> callback;
        boolean cancelada;
        void responder(Response<List<LoteIngreso>> respuesta) { callback.onResponse(this, respuesta); }
        void fallar() { callback.onFailure(this, new IOException("sin conexion")); }
        @Override public void enqueue(Callback<List<LoteIngreso>> callback) { this.callback = callback; }
        @Override public void cancel() { cancelada = true; }
        @Override public boolean isCanceled() { return cancelada; }
        @Override public boolean isExecuted() { return callback != null; }
        @Override public Call<List<LoteIngreso>> clone() { return new PeticionPrueba(); }
        @Override public Request request() { return new Request.Builder().url("https://example.test/").build(); }
        @Override public Timeout timeout() { return new Timeout(); }
        @Override public Response<List<LoteIngreso>> execute() { throw new UnsupportedOperationException(); }
    }
}
