package usonsonate.com.tukybirth;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import usonsonate.com.tukybirth.Calendar.MyEventDay;
import usonsonate.com.tukybirth.SQLite.DB;
import usonsonate.com.tukybirth.SQLite.Notas;

public class MainNotes extends AppCompatActivity {

    private DB db;
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    public static final int LIST_NOTES = 45;
    private CalendarView mCalendarView;
    private List<EventDay> mEventDays = new ArrayList<>();
    //lista de datos (nota)
    private ArrayList<Notas> lstNotas;
    private ArrayList<Notas> lstNotasDia;
    Calendar c;
    Date lastdate;
    private FloatingActionButton floatingActionButton;

    //sirve para manejar la eliminacion
    private Notas nota_temp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notes);

        mCalendarView = findViewById(R.id.calendarView);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        setTitle("Agregar notas");

        //Para activar y asignar que necesitaremos un botón para regresar a la activity anterior
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //transicion inversa a //
        //CREAMOS LA TRANSICION
        Fade fadeIn = new Fade(Fade.IN);
        fadeIn.setDuration(MainActivity.DURATION_TRANSITION);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        //ESTABLECEMOS LA TRANSICION DE REGRESO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setReenterTransition(fadeIn);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(false);
        }


        //Notas por día
        lstNotasDia = new ArrayList<>();

        //Inicializamos el calendario del sistema para poder asignar una fecha
        c = Calendar.getInstance();
        lastdate = new Date();
        c.setTime(lastdate);

        //Establecemos la fecha de inicio del calendario principal (widget)
        try {
            mCalendarView.setDate(c);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        //inicializando lista y db
        db = new DB(MainNotes.this);

        //Consultamos las notas del mes del calendario con el mes que iniciara
        ConsultarNotasMes(mCalendarView.getCurrentPageDate().getTime());


        //Evento para el boton flotante
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        //Evento para visualizar la nota sobre el boton que pulsemos
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                //previewNote(eventDay);
                BuscarNotasDia(eventDay.getCalendar().getTime());
                ListaNotasporDia();
            }
        });

        mCalendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                ConsultarNotasMes(mCalendarView.getCurrentPageDate().getTime());
            }
        });

        mCalendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                ConsultarNotasMes(mCalendarView.getCurrentPageDate().getTime());
            }
        });

    }

    private void ListaNotasporDia(){

        if (lstNotasDia.size() != 0){
            Intent intento = new Intent(MainNotes.this, ListaNotasDia.class);
            Bundle contenedor = new Bundle();
            contenedor.putSerializable("array", lstNotasDia);
            intento.putExtras(contenedor);
            startActivityForResult(intento, LIST_NOTES);
        }else{
            Toast.makeText(this, "No ha agregado ninguna nota para este día.", Toast.LENGTH_SHORT).show();
        }
    }

    private void BuscarNotasDia(Date fechanotas){

        lstNotasDia = new ArrayList<>();

        for (final Notas nota:lstNotas){

            if(convertirDateToString(fechanotas).equals(nota.getFechanota())){
                lstNotasDia.add(nota);
            }
        }

    }

    private void ConsultarNotasMes(Date currentdate){

        lstNotas = null;

        lstNotas = db.getArrayNotas(
                db.getCursorNota(String.valueOf(convertirDateToStringMonth(currentdate)))
        );

        if(lstNotas==null){
            lstNotas = new ArrayList<>();//si no hay datos
        }else{

            for (final Notas nota:lstNotas){

                MyEventDay myEventDay = new MyEventDay(convertirACalendar(nota.getFechanota()),R.drawable.note,nota.getNota());

                try {
                    addNoteinCalendar(myEventDay);
                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void guardar(MyEventDay myEventDay){

        Date newDate = myEventDay.getCalendar().getTime();

        String fechaconvert = convertirDateToString(newDate);

        Notas nota = new Notas("", fechaconvert , myEventDay.getNote());
        nota_temp=null;

        if(db.guardar_O_ActualizarNotas(nota)){
            Toast.makeText(this,"Se guardo la nota",Toast.LENGTH_SHORT).show();
            //TODO limpiar los que existen y agregar los nuevos
            lstNotas.clear();
            lstNotas = db.getArrayNotas(
                    db.getCursorNota(String.valueOf(c.get(Calendar.MONTH)))
            );
        }else{
            Toast.makeText(this,"Ocurrio un error al guardar",Toast.LENGTH_SHORT).show();
        }
    }

    //region DataParse
    private String convertirDateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaComoCadena = sdf.format(date);

        return fechaComoCadena;
    }

    private String convertirDateToStringMonth(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String fechaComoCadena = sdf.format(date);

        return fechaComoCadena;
    }

    private Date cambiar_mes(Date fecha, int cambio){
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);

        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(calendar.MONTH, cambio);  // numero de meses a añadir, o restar en caso de días<0

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.format(calendar.getTime());//Le da formato a la fecha

        fecha = calendar.getTime();

        Toast.makeText(MainNotes.this, fecha.toString() + "" ,Toast.LENGTH_SHORT).show();
        return fecha;

    }

    private Calendar convertirACalendar(String fecha){
        String[] fechArray = fecha.split("-");


        int anio = Integer.parseInt(fechArray[0]);
        int mes =  Integer.parseInt(fechArray[1]) - 1;
        int dia =  Integer.parseInt(fechArray[2]);

        /*
         *
         * Al mes lo resto 1 (-1) ya que el formato de Calendar el mes empieza en 0
         * Enero=0, Febrero=1, Marzo=2, ... , Diciembre=11
         * De lo contrario Diciembre (12) no funcionaria
         *
         * */

        Calendar c1 = new GregorianCalendar(anio, mes, dia);

        return c1;
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
            MyEventDay myEventDay = data.getParcelableExtra(RESULT);
            try {
                //Agregamos la nota en el calendario
                addNoteinCalendar(myEventDay);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }

            guardar(myEventDay);
        }

        if(requestCode == LIST_NOTES){
            mEventDays.clear();
            try {
                mCalendarView.setDate(mCalendarView.getCurrentPageDate().getTime());
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }
        }

        ConsultarNotasMes(mCalendarView.getCurrentPageDate().getTime());
    }

    private void addNoteinCalendar(MyEventDay myEventDay ) throws OutOfDateRangeException {
        //Formato que debe poseer la nota al enviarse para agregar al calendario
        //MyEventDay(Calendar day, int imageResource, String note)

        //mCalendarView.setDate(myEventDay.getCalendar());
        mEventDays.add(myEventDay);
        mCalendarView.setEvents(mEventDays);
    }

    private void addNote() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivityForResult(intent, ADD_NOTE);
    }

}
