package com.example.template.testing;

import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.fragment.app.FragmentActivity;
import com.example.template.R;

/** Contenedor sin red ni sesion, disponible solamente en la variante debug. */
public class RecordatoriosTestActivity extends FragmentActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contenido = new FrameLayout(this);
        contenido.setId(R.id.fragment_container);
        setContentView(contenido);
    }
}
