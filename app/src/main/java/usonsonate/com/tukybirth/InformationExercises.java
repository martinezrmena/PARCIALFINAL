package usonsonate.com.tukybirth;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.animation.DecelerateInterpolator;

import java.util.Objects;

import usonsonate.com.tukybirth.Ejercicios.AdapterListExercise;
import usonsonate.com.tukybirth.Ejercicios.DataExercise;

public class InformationExercises extends AppCompatActivity {
    RecyclerView recyclerView;
    String [] datos;
    String [] details;
    AdapterListExercise adapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CREAMOS LA TRANSICION DE ENTRADA
        Fade fadeIn = new Fade(Fade.IN);
        fadeIn.setDuration(MainActivity.DURATION_TRANSITION);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        //CREAMOS LA TRANSICION DE SALIDA
        Fade fadeOut = new Fade(Fade.OUT);
        fadeIn.setDuration(MainActivity.DURATION_TRANSITION);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        //OBTENEMOS LA VENTANA ANTERIOR Y ESTABLECEMOS LA TRASICION EN SU LLEGADA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(fadeOut);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fadeIn);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(false);
        }
        //para cerrar utilizamos finishAfterTransition();
        setContentView(R.layout.activity_information_exercises);


        //Inicializar variables
        datos = getResources().getStringArray(R.array.Trimestres);
        details = getResources().getStringArray(R.array.detalle_ejercicios);

        recyclerView = findViewById(R.id.recycleViewExercises);
        adapter = new AdapterListExercise(InformationExercises.this, DataExercise.getData(datos, details));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Vertical Orientation By Default
    }
}
