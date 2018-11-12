package usonsonate.com.tukybirth.Comidas;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import usonsonate.com.tukybirth.Ejercicios.AdapterListExercise;
import usonsonate.com.tukybirth.Ejercicios.InformationExercise;
import usonsonate.com.tukybirth.R;
import usonsonate.com.tukybirth.Semanas.AnimationUtil;

public class AdapterComida extends  RecyclerView.Adapter<AdapterComida.MyViewHolder> {
    private Context context;

    private ArrayList<InformationExercise> data;

    private LayoutInflater inflater;

    private int previousPosition = 0;

    Dialog myDialog;

    public AdapterComida(Context context, ArrayList<InformationExercise> data) {

        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    /*aqui ahy que hacer un layout personalizado*/
    /********************************************************************************************/
    @Override
    public AdapterComida.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.list_item_row, parent, false);

        AdapterComida.MyViewHolder holder = new AdapterComida.MyViewHolder(view);

        return holder;
    }
    /********************************************************************************************/
    @Override
    public void onBindViewHolder(AdapterComida.MyViewHolder myViewHolder, final int position) {

        myViewHolder.textview.setText(data.get(position).title);
        myViewHolder.cardView.setBackground(data.get(position).color);
        myViewHolder.imageView.setImageResource(data.get(position).imageId);

        if(position > previousPosition){ // We are scrolling DOWN

            AnimationUtil.animate(myViewHolder, true);

        }else{ // We are scrolling UP

            AnimationUtil.animate(myViewHolder, false);


        }

        previousPosition = position;


        final int currentPosition = position;
        myDialog = new Dialog(context);
        final InformationExercise infoData = data.get(position);


        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "OnClick Called at position " + position, Toast.LENGTH_SHORT).show();

                TextView txtclose, txbnumero, txbInformacion;
                ImageView imagenPopUp;
                //inicializamos las variables
                myDialog.setContentView(R.layout.custompopup);
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                txbnumero = myDialog.findViewById(R.id.txtNumeroSelected);
                txbInformacion = myDialog.findViewById(R.id.txtinformacion);
                imagenPopUp = myDialog.findViewById(R.id.ImagePopup);
                imagenPopUp.setScaleType(ImageView.ScaleType.FIT_XY);

                //establecemos los valores del pop up
                txbnumero.setText(String.valueOf(position +1));
                imagenPopUp.setImageResource(infoData.imageId);
                txbInformacion.setText(infoData.Details);
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                //addItem(currentPosition, infoData);
            }
        });

        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(context, "Semana " + (position + 1) + " removida.", Toast.LENGTH_SHORT).show();

                removeItem(infoData);

                return true;
            }


        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textview;
        ImageView imageView;
        LinearLayout cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.txv_row);
            imageView = itemView.findViewById(R.id.img_row);
            cardView = itemView.findViewById(R.id.MainCard);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
    }

    // This removes the data from our Dataset and Updates the Recycler View.
    private void removeItem(InformationExercise infoData) {

        int currPosition = data.indexOf(infoData);
        data.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, InformationExercise infoData) {

        data.add(position, infoData);
        notifyItemInserted(position);
    }


}
