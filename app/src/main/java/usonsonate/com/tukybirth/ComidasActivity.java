package usonsonate.com.tukybirth;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.animation.DecelerateInterpolator;

import usonsonate.com.tukybirth.Slides.SlideAdapterComidas;
import usonsonate.com.tukybirth.Slides.SlideAdapterEjercicios;

public class ComidasActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SlideAdapterComidas myadapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);


        setTitle("Comidas");

        //CREAMOS LA TRANSICION DE ENTRADA
        Fade fadeIn = new Fade(Fade.IN);
        fadeIn.setDuration(MainActivity.DURATION_TRANSITION);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        //CREAMOS LA TRANSICION DE SALIDA
        Fade fadeOut = new Fade(Fade.OUT);
        fadeIn.setDuration(MainActivity.DURATION_TRANSITION);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        //OBTENEMOS LA VENTANA ANTERIOR Y ESTABLECEMOS LA TRASICION EN SU LLEGADA
        getWindow().setExitTransition(fadeOut);
        getWindow().setEnterTransition(fadeIn);
        getWindow().setAllowEnterTransitionOverlap(false);

        viewPager = findViewById(R.id.viewpagerComidas);
        myadapter = new SlideAdapterComidas(this);
        viewPager.setAdapter(myadapter);

    }
}
