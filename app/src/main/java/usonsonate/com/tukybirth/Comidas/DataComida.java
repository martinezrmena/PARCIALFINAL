package usonsonate.com.tukybirth.Comidas;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.ArrayList;

import usonsonate.com.tukybirth.Ejercicios.InformationExercise;
import usonsonate.com.tukybirth.R;

public class DataComida {


    private static String posicion;

    public  DataComida(String posicion){
        this.posicion = posicion;
    }

    public static ArrayList<InformacionComidas> getData(String [] datos, String detils []) {

        ArrayList<InformacionComidas> data = new ArrayList<>();

        int[] images = {
                /*Trimestre 1*/
                R.drawable.trim1_0,
                R.drawable.trim1_1,
                R.drawable.trim1_2,

                /*Trimestre 2*/
                R.drawable.trim2_0,
                R.drawable.trim2_1,
                R.drawable.trim2_2,

                /*Trimestre 3*/
                R.drawable.trim3_0,
                R.drawable.trim3_1,
                R.drawable.trim3_2,

        };

        String[] Semanas = datos;

        /*********************************************************************************/
        if(posicion.equals("0")){

            for (int i = 0; i < 3; i++) {
                InformacionComidas current = new InformacionComidas();
                current.title = Semanas[i];
                current.imageId = images[i];
                // current.color = new ColorDrawable(Color.parseColor("#022D51"));
                current.color = new ColorDrawable(Color.parseColor("#E8F8F8"));
                current.Details = detils[i];
                data.add(current);
            }


        }
        /*********************************************************************************/
        if(posicion.equals("1")){


            for (int i = 3; i < 6; i++) {

                InformacionComidas current = new InformacionComidas();
                current.title = Semanas[i];
                current.imageId = images[i];
                current.color = new ColorDrawable(Color.parseColor("#1b5e20"));
                current.Details = detils[i];
                data.add(current);
            }


        }
        /*********************************************************************************/
        if(posicion.equals("2")){


            for (int i = 6; i < 9; i++) {

                InformacionComidas current = new InformacionComidas();
                current.title = Semanas[i];
                current.imageId = images[i];
                current.color = new ColorDrawable(Color.parseColor("#A00E21"));
                current.Details = detils[i];
                data.add(current);
            }


        }
        /*********************************************************************************/


        return data;
    }
}
