package usonsonate.com.tukybirth;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import usonsonate.com.tukybirth.Calendar.MyEventDay;
import usonsonate.com.tukybirth.SQLite.Ciclo;
import usonsonate.com.tukybirth.SQLite.DB;
import usonsonate.com.tukybirth.SQLite.DetalleCiclo;
import usonsonate.com.tukybirth.SQLite.Notas;
import usonsonate.com.tukybirth.SQLite.Personas;
import usonsonate.com.tukybirth.SQLite.PromedioCiclos;

public class CalendarLogin extends AppCompatActivity {

    private Personas persona;
    private DB db;
    private Calendar c;
    private Date currentDate;
    private CalendarView CalendarPeriodo;
    private FloatingActionButton btnAgregar;
    private String FinCiclo;
    private String InicioPeriodo = "";
    private CustomDateParse customDateParse;
    private List<EventDay> mRegisteredDaysCiclo = new ArrayList<>();
    private List<Ciclo> lstCiclos;
    private List<DetalleCiclo> lstDetalleCiclo;
    private PromedioCiclos promedioCiclos;
    private Ciclo UltimoCiclo;
    public static final int INICIALIZAR = 44;
    public static final int INSERTAR = 45;
    public static final int MODIFICAR = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_login);

        persona = (Personas) getIntent().getExtras().getSerializable("PERSONA");
        CalendarPeriodo = findViewById(R.id.clnPeriodo);
        btnAgregar = findViewById(R.id.fabtnPeriodo);
        customDateParse = new CustomDateParse();
        db = new DB(CalendarLogin.this);
        setTitle("Datos del ciclo");
        //Para activar y asignar que necesitaremos un botón para regresar a la activity anterior
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        //region Inicializar_Calendario
        //Inicializamos el calendario del sistema para poder asignar una fecha
        c = Calendar.getInstance();
        currentDate = new Date();
        c.setTime(currentDate);

        //Establecemos la fecha de inicio del calendario principal (widget)
        try {
            CalendarPeriodo.setDate(c);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        //endregion

        Calcular_Ciclo_con_Datos_Iniciales();

        //region Evento Dia Actual

        CalendarPeriodo.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Date date = customDateParse.convertirStringToDate(customDateParse.convertirDateToString(new Date()));
                Date pressDate = customDateParse.convertirStringToDate(customDateParse.convertirDateToString(eventDay.getCalendar().getTime()));

                if (customDateParse.convertirStringToDate(persona.getUltimo_periodo()).after(eventDay.getCalendar().getTime())){

                    Toast.makeText(CalendarLogin.this, "No puede introducir registros antes de la fecha en que iniciará su periodo actual según el registro: " + persona.getUltimo_periodo(), Toast.LENGTH_SHORT).show();

                }else{
                    if (date.after(eventDay.getCalendar().getTime()) || date.equals(pressDate)){

                        //Validamos si hay ciclos introducidos
                        if (Integer.parseInt(promedioCiclos.getCOUNT()) >0){

                            if(lstDetalleCiclo.size() == 0){
                                //Si NO hay detalle ciclo enviamos parametros basicos ya que será una inserción
                                Ciclo ciclo_seleccionado = new Ciclo();

                                for(Ciclo c: lstCiclos){
                                    if (customDateParse.ValidarUnaFechaEntreDos(c.getFecha_inicio(),c.getFecha_fin(), customDateParse.convertirDateToString(eventDay.getCalendar().getTime())) &&
                                            c.getEstado().equals("TERMINO")){
                                        ciclo_seleccionado = c;
                                        break;
                                    }else{
                                        if(c.getEstado().equals("EN PROCESO")){
                                            ciclo_seleccionado = c;
                                            break;
                                        }
                                    }
                                }

                                Intent intent = new Intent(getApplicationContext(), DetalleDiaPeriodo.class);
                                intent.putExtra("DATE_CALENDAR", customDateParse.convertirDateToString(eventDay.getCalendar().getTime()));
                                intent.putExtra("INICIALIZAR", "MEDIO");
                                intent.putExtra("CICLO", ciclo_seleccionado);
                                intent.putExtra("PERSONA", persona);
                                startActivityForResult(intent, INSERTAR);

                            }else{
                                //Si hay detalle ciclo debemos buscar el del día seleccionado ya que sera una actualización
                                DetalleCiclo dia_seleccionado = new DetalleCiclo();
                                Ciclo ciclo_seleccionado = new Ciclo();

                                for (DetalleCiclo d: lstDetalleCiclo){

                                    if(d.getFecha_introduccion().equals(customDateParse.convertirDateToString(eventDay.getCalendar().getTime()))){
                                        dia_seleccionado = d;
                                    }
                                }

                                if(dia_seleccionado.getId_detalle().isEmpty()){
                                    //NO HAY DÍA REGISTRADO
                                    //Si NO hay detalle ciclo enviamos parametros basicos ya que será una inserción
                                    ciclo_seleccionado = new Ciclo();

                                    for(Ciclo c: lstCiclos){

                                        if (customDateParse.ValidarUnaFechaEntreDos(c.getFecha_inicio(),c.getFecha_fin(), customDateParse.convertirDateToString(eventDay.getCalendar().getTime())) &&
                                                c.getEstado().equals("TERMINO")){
                                            ciclo_seleccionado = c;
                                            break;
                                        }else{
                                            if(c.getEstado().equals("EN PROCESO")){
                                                ciclo_seleccionado = c;
                                                break;
                                            }
                                        }
                                    }

                                    //Enviar validacion para que no pueda agregar detalles en un ciclo que ya termino



                                    Intent intent = new Intent(getApplicationContext(), DetalleDiaPeriodo.class);
                                    intent.putExtra("DATE_CALENDAR", customDateParse.convertirDateToString(eventDay.getCalendar().getTime()));
                                    intent.putExtra("INICIALIZAR", "MEDIO");
                                    intent.putExtra("CICLO", ciclo_seleccionado);
                                    intent.putExtra("PERSONA", persona);
                                    startActivityForResult(intent, INSERTAR);

                                }else{

                                    for(Ciclo c: lstCiclos){
                                        if(dia_seleccionado.getId_ciclo().equals(c.getId_ciclo())){
                                            ciclo_seleccionado = c;
                                        }
                                    }

                                    Intent intent = new Intent(getApplicationContext(), DetalleDiaPeriodo.class);
                                    intent.putExtra("DATE_CALENDAR", customDateParse.convertirDateToString(eventDay.getCalendar().getTime()));
                                    intent.putExtra("INICIALIZAR", "NO");
                                    //Enviar el detalle ciclo del día seleccionado
                                    intent.putExtra("DETALLE_CICLO", dia_seleccionado);
                                    //Enviar el ciclo del día seleccionado
                                    intent.putExtra("CICLO", ciclo_seleccionado);
                                    startActivityForResult(intent, MODIFICAR);
                                }

                            }
                        }else{
                            //Es necesario inicializar
                            //Si NO hay ciclo
                            Intent intent = new Intent(getApplicationContext(), DetalleDiaPeriodo.class);
                            intent.putExtra("DATE_CALENDAR", customDateParse.convertirDateToString(eventDay.getCalendar().getTime()));
                            intent.putExtra("INICIALIZAR", "SI");
                            intent.putExtra("PERSONA", persona);
                            startActivityForResult(intent, INICIALIZAR);

                        }



                    }else{

                        Toast.makeText(CalendarLogin.this, "No se pueden agregar detalles a dias posteriores de la fecha actual.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //endregion

        CalendarPeriodo.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                Calcular_Ciclo_con_Datos_Iniciales();
            }
        });

        CalendarPeriodo.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                Calcular_Ciclo_con_Datos_Iniciales();
            }
        });

    }

    public void DetallesIconos(View v){
        Intent intent = new Intent(getApplicationContext(), ListDetalleActivity.class);
        startActivity(intent);
    }

    private void Calcular_Ciclo_con_Datos_Iniciales(){

        //Obtenemos los promedios para conocer si calculamos desde ellos o desde parametros iniciales
        CalculatePromedios();
        if (Integer.parseInt(promedioCiclos.getCOUNT()) >0){

            //Consultamos el ultimo periodo almacenado
            ConsultarUltimoPeriodo();

            //Si poseemos registros almacenados consultamos los de este mes
            if(ConsultarCiclos()){

                //Si tiene ciclos calculamos los detalles de esos ciclos en este mes
                ConsultarDetalleCiclos();

                //Mostramos todos los detalles de los ciclos almacenados
                for (DetalleCiclo d:lstDetalleCiclo) {
                    DetallesCicloAlmacenados(d);
                }

            }

            //region PrediccionProximoCiclo

            String valuefinal = customDateParse.convertirDateToStringMonth_Year(customDateParse.convertirStringToDate(UltimoCiclo.getFecha_fin()));
            String valuecalendario = customDateParse.convertirDateToStringMonth_Year(CalendarPeriodo.getCurrentPageDate().getTime());
            String valueinicial = customDateParse.convertirDateToStringMonth_Year(customDateParse.convertirStringToDate(UltimoCiclo.getFecha_inicio()));
            String valueCambioMesFin = customDateParse.convertirDateToStringMonth_Year(customDateParse.cambiar_mes(customDateParse.convertirStringToDate(UltimoCiclo.getFecha_fin()),1));

            InicioPeriodo = customDateParse.convertirDateToString(customDateParse.cambiar_dia(
                    customDateParse.convertirStringToDate(UltimoCiclo.getFecha_inicio()),
                    Integer.parseInt(promedioCiclos.getDURACION_CICLO()) + 1));

            PredecirProximoCiclo(InicioPeriodo, Integer.parseInt(promedioCiclos.getDURACION_PERIODO()));
            //endregion

        }else{
            //Si no poseemos registros calculamos desde los parametros ingresados
            // desde el login
            //FinCiclo =  calculateFinCiclo(persona.getUltimo_periodo(), persona.getCiclo());
            InicioPeriodo = customDateParse.convertirDateToString(customDateParse.cambiar_dia(
                    customDateParse.convertirStringToDate(persona.getUltimo_periodo()), Integer.parseInt(persona.getCiclo())));

            PredecirProximoCiclo(InicioPeriodo, Integer.parseInt(persona.getPeriodo()));
        }

    }

    private void PredecirProximoCiclo(String InicioPeriodo, int duracion){

        String fecha_temporal = InicioPeriodo;

        for (int i = 0; i < duracion; i++){

            MyEventDay myEventDay = new MyEventDay(customDateParse.convertirACalendar(fecha_temporal),
                    R.drawable.predict,"predicción");

            try {
                addPredictDaysInCalendar(myEventDay);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }


            String d = fecha_temporal;

            fecha_temporal = customDateParse.convertirDateToString(customDateParse.cambiar_dia(customDateParse.convertirStringToDate(d), 1));

        }
    }

    private void DetallesCicloAlmacenados(DetalleCiclo d){

        String fecha_temporal = d.getFecha_introduccion();
        int icon = 0;

        switch (d.getSeveridad()){
            case"Dolores":
                icon = R.drawable.sick_girl;
                break;
            case "Perdidas":
                icon = R.drawable.perdidas;
                break;

            case "Ligero":
                icon = R.drawable.ligero;
                break;

            case "Medio":
                icon = R.drawable.medio;
                break;

            case "Fuerte":
                icon = R.drawable.desangramiento;
                break;

            case "TERMINO":
                icon = R.drawable.finish_period;
                break;

                default:
                    icon = R.drawable.period_blood;
                    break;

        }

        MyEventDay myEventDay = new MyEventDay(customDateParse.convertirACalendar(fecha_temporal),
                icon,d.getDetalle());

        try {
            addDaysInCalendar(myEventDay);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }

    private String calculateInicioPeriodo(int periodo){

        int periododuracion = periodo * - 1;

        return customDateParse.convertirDateToString(customDateParse.cambiar_dia(customDateParse.convertirStringToDate(
                FinCiclo), periododuracion));
    }

    private String calculateFinCiclo(String UltimoPeriodo, String Cambio){

        return customDateParse.convertirDateToString(customDateParse.cambiar_dia(
                customDateParse.convertirStringToDate(UltimoPeriodo),Integer.parseInt(Cambio)));
    }

    private void addPredictDaysInCalendar(MyEventDay myEventDay ) throws OutOfDateRangeException {
        //Formato que debe poseer la nota al enviarse para agregar al calendario
        //MyEventDay(Calendar day, int imageResource, String note)

        //CalendarPeriodo.setDate(myEventDay.getCalendar());
        mRegisteredDaysCiclo.add(myEventDay);
        CalendarPeriodo.setEvents(mRegisteredDaysCiclo);
    }

    private void addDaysInCalendar(MyEventDay myEventDay ) throws OutOfDateRangeException {
        //Formato que debe poseer la nota al enviarse para agregar al calendario
        //MyEventDay(Calendar day, int imageResource, String note)

        //CalendarPeriodo.setDate(myEventDay.getCalendar());
        mRegisteredDaysCiclo.add(myEventDay);
        CalendarPeriodo.setEvents(mRegisteredDaysCiclo);
    }

    private boolean ConsultarCiclos() {

        boolean insertado = false;

        lstCiclos = null;

        lstCiclos = db.getArrayCiclos(
                db.getCursorCiclo(customDateParse.convertirDateToStringMonth_Year(
                        CalendarPeriodo.getCurrentPageDate().getTime()
                ))
        );

        if (lstCiclos == null) {

            lstCiclos = new ArrayList<>();//si no hay datos

        }else{
            insertado = true;
        }

        return insertado;
    }

    private boolean ConsultarDetalleCiclos() {

        boolean insertado = false;

        lstDetalleCiclo = null;

        lstDetalleCiclo = db.getArrayDetalleCiclos(
                db.getCursorDetalleCiclo(customDateParse.convertirDateToStringMonth_Year(
                        CalendarPeriodo.getCurrentPageDate().getTime()
                ))
        );

        if (lstDetalleCiclo == null) {

            lstDetalleCiclo = new ArrayList<>();//si no hay datos

        }else{

            insertado = true;
        }

        return insertado;
    }

    private void CalculatePromedios(){

        promedioCiclos = null;

        promedioCiclos = db.getArrayPromediosCiclos(
                db.getPromediosCiclo()
        ).get(0);

        if (promedioCiclos == null) {

            promedioCiclos = new PromedioCiclos();//si no hay datos

        }

    }

    private void ConsultarUltimoPeriodo(){

        UltimoCiclo = null;

        UltimoCiclo = db.getArrayLastCiclo(
                db.getLastCiclo()
        );

        if (UltimoCiclo == null) {

            UltimoCiclo = new Ciclo();//si no hay datos

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INICIALIZAR ||requestCode == MODIFICAR || requestCode == INSERTAR  ) {
            mRegisteredDaysCiclo.clear();

            try {
                CalendarPeriodo.setDate(CalendarPeriodo.getCurrentPageDate().getTime());
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }

            Calcular_Ciclo_con_Datos_Iniciales();
        }
    }
}
