package ml.p_seminar.apoutdoortools;


/**
 * Diese Klasse ermöglicht die Positionsbestimmung und den Höhenmesser sowie dessen graphische Darstellung.
 *
 * Geschrieben von Tobias
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;


public class GPSFragment extends Fragment{

    private Button button;                      //Knopf der GPS startet
    private TextView textView;                  //TextView Rechts
    private TextView Ergebnisse;                //TextView am linken Rand, die die ermittelten Werte anzeigt
    private TextView info;                      //"Genauigkeit in Meter"
    private TextView tv_Genauigkeit;            //TextView, die die Genauigkeit der Messdaten angibt

    private Button erweitert;                   //Knopf durch den sich die Koordinatenangaben entweder runden oder exakt ausgeben lassen können

    private LocationManager locationManager;    //ermöglicht den Zugriff auf die GPS Ortung des Smartphones
    private LocationListener locationListener;
    private int zustand;                        //Bei einer Laufenden Höhenmessung: zustand = 1

    private View view = null;
    private SeekBar seekBar;                    //liefert ganze Zahlen zwischen 0 und 8 um die Größe des Benachrichtigungsintervalles festzulegen

    private Vibrator vibrator;                  //ermöglicht das Gerät auf Kommando vibrieren zu lassen

    private int benachrichtigungsintervall;     //legt Höhe fest die ausgehend von der Starthöhe der Messung, zurückgelegt werden muss, bevor das Smartphone vibriert
    private double laenge;                      //Längengrad
    private double breite;                      //Breitengrad
    private double hoehe;                       //Höhe über normal Null
    private float genauigkeit;                  //Mögliche Abweichung der Messung von der atsächlichen Position in Metern
    private int zustandDaten;
    private String provider;                    //Medium durch, das das Smartphone die Position bestimmen kann
    private int fixierungStart;                 //Integer, der es ermöglicht die ersten 20 Messungen zu Überspringen und nicht die erste Messung als Startwert zu nehmen, da die ersten Messungen sehr ungenau sein können
    private int zaehlerMessungen;               //Verwendet um aus zehn Messungen den Durchschnittswert zu bilden, der dann im Diagramm angezeigt wird.
    private LineGraphSeries<DataPoint> series;  //ermöglicht es Koordinatenpunkte(Datapoints) in sich zu speichern; kann als Graph angezeigt werden
    private double[] datenGraph;                //enthält zehn Messugnen, aus denenein Durchschnitt berechnet wird, dieser wird im Koordinatensystem eingetragen
    private double datenGraphDurchschnitt;      //Eben erwähnter Durchschnittswert, der in das Koordinatensystem eingetragen wird.
    private int datenGraphZaehler;              //zählt von 0-9, damit erkannt werden kann, ob zehn Messungen gesammelt wurden
    private int xPosGraph;                      //Position der Punkte auf dem Graphen; Wird in Einerschritten erhöht
    private GraphView graph;                    //Koordinatensystem

    private double starthoehe;                  //Höhe, von der aus die Messung beginnt
    private double zwischenhoehe;               //nachdem die Distanz des Benachrichtigungsintervall erreicht ist wird das nächste von diesem Wert abhängig gemacht, da starthöhe für den Graphen benötigt wird


    /**
     *Ruft nacheinander die Methoden zur initialisierung auf. DIse sind zur übersichtlichkeit auf drei Einzelne Methoden aufgeteilt.
     */
    @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container,savedInstanceState);
        view=inflater.inflate(R.layout.gps,container,false);

        initAnzeige();
        setSeekBarInit();
        locationManagerInit();
        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch(requestCode)
        {
            case 0:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    knopfInitialisieren();
                return;
        }
    }


    /**
     * knopfInitialisieren() erzeugt einen Aktion listener für den oberen Knopf, der es ermöglicht zwischen den Modi zu wechseln.
     * Je nachdem wird dann der Text in den beiden Textfeldern angepasst.
     * wird der Knopf gedrück, Vibriert das Gerät für 100Millisekunden
     */
    private void knopfInitialisieren()
    {

        View.OnClickListener onClick = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch(view.getId())
                {
                    case R.id.posAnfragen: vibrator.vibrate(100); knopfdruck(); break;
                    case R.id.erweitert:
                        vibrator.vibrate(100);
                        if(zustandDaten==0)
                        {
                            Toast.makeText(getActivity(),"Werte gerundet",Toast.LENGTH_SHORT).show();
                            zustandDaten=1;
                            erweitert.setText(R.string.erweitert_Modus_Text);
                            Ergebnisse.setTextSize(20);
                            textView.setTextSize(20);
                            textView.setText(R.string.Text_einfach);
                            Ergebnisse.setText(R.string.drei_Platzhalter);
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"genaue Werte",Toast.LENGTH_SHORT).show();
                            zustandDaten=0;
                            erweitert.setText("einfacher Modus");
                            Ergebnisse.setTextSize(20);
                            textView.setTextSize(20);

                            textView.setText(R.string.text_erweitert);
                            Ergebnisse.setText(R.string.vier_Platzhalter);
                        }
                        break;
                }
            }
        };
        button.setOnClickListener(onClick);
        erweitert.setOnClickListener(onClick);

    }


    /**
     * knopfdruck wird aufgerufen, sollte der untere Knopf gedrückt werden. Je nachdem ob die Positionsbestimmung an- oder ausgeschaltet ist, wird nun das jeweils andere gamacht.
     */

    private void knopfdruck()
    {
        if(zustand==0)
        {
            button.setText(R.string.stop);
            locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);//Fehlermeldung nicht relevant, da der geforderte "permission check" seperat geprüft wird.
            zustand=1;
            Log.d("d","position angefragt");
        }
        else
        {
            button.setText(R.string.position_anfragen);
            textView.setText(R.string.Koordinaten);
            locationManager.removeUpdates(locationListener);
            zustand=0;
            Log.d("d","stop");
        }
    }

    /**
     * setSeekBarInit() erzeugt die seekbar und einen Listener, der je nach Eingabe durch den Benutzer das benachichtigungsinterval festlegt und auf dem darunter liegenden Textfeld "tvi" ausgibt.
     */

    private void setSeekBarInit()
    {
        seekBar=(SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vibrator.vibrate(5);
                switch (progress){
                    case 0:
                        benachrichtigungsintervall = -1;
                        TextView tvi = (TextView)view.findViewById(R.id.text1);
                        tvi.setText(R.string.Bintervall_aus);
                        break;
                    case 1:
                        benachrichtigungsintervall = 10;
                        break;
                    case 2:
                        benachrichtigungsintervall = 25;
                        break;
                    case 3:
                        benachrichtigungsintervall = 42;
                        break;
                    case 4:
                        benachrichtigungsintervall = 50;
                        break;
                    case 5:
                        benachrichtigungsintervall = 75;
                        break;
                    case 6:
                        benachrichtigungsintervall = 100;
                        break;
                    case 7:
                        benachrichtigungsintervall = 150;
                        break;
                    case 8:
                        benachrichtigungsintervall = 200;
                        break;
                    case 9:
                        TextView tve = (TextView)view.findViewById(R.id.text1);
                        tve.setText(R.string.Bintervall_custom);
                        break;
                    default:
                        benachrichtigungsintervall =1;
                }
                TextView tv = (TextView)view.findViewById(R.id.text1);

                if(progress!=0 && progress!=9)tv.setText(getString(R.string.bintervall)+" "+benachrichtigungsintervall+"m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("Seekbar","progress: "+seekBar.getProgress());
                switch (seekBar.getProgress()){
                    case 9:
                        dialogmethode();
                        break;
                }
                TextView tv = (TextView)view.findViewById(R.id.text1);
                if(seekBar.getProgress()!=0 && seekBar.getProgress()!=9)tv.setText(getString(R.string.bintervall)+benachrichtigungsintervall);
            }
        });
    }


    /**
     * dialogMethode erzeugt eine Dialog, mithilfe dessen eine beliebig große Zahl als Benachrichtigunsintervall festgelegt werden kann
     */
    private void dialogmethode()
    {
        AlertDialog.Builder builder;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder = new AlertDialog.Builder(getActivity());


        builder.setTitle(R.string.Bntervall_aendern);
        final View customView = inflater.inflate(R.layout.custom_dialog, null);

     builder.setView(customView)
                .setMessage(R.string.Bntervall_aendern)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText eingabe=(EditText)customView.findViewById(R.id.zahleingabe);

                        String s=eingabe.getText().toString();
                        benachrichtigungsintervall =Integer.valueOf(s);
                        if(benachrichtigungsintervall>=2000)
                        {
                            benachrichtigungsintervall=2000;
                        }
                        TextView g=(TextView) view.findViewById(R.id.text1);
                        g.setText(getString(R.string.bintervall)+benachrichtigungsintervall);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    /**
     * initAnzeige() initialisiert alle globalen Variablen sowie die graphischen Elemente der Benutzeroberfläche.
     */
    private void initAnzeige()
    {
        int i=0;

        benachrichtigungsintervall =-1;
        zustand=0;
        laenge=0;
        breite=0;
        hoehe=0;
        genauigkeit=0;
        zustandDaten=0;
        starthoehe=0;
        zwischenhoehe=0;
        fixierungStart=0;
        datenGraphZaehler=0;
        datenGraphDurchschnitt=0;
        xPosGraph=0;
        series=new LineGraphSeries<DataPoint>();

        datenGraph= new double[10];
        for(i=0;i<10;i++)
        {
            datenGraph[i]=0;
        }

        graph=(GraphView)view.findViewById(R.id.graph);
        graph.addSeries(series);

        button = (Button) view.findViewById(R.id.posAnfragen);
        erweitert = (Button) view.findViewById(R.id.erweitert);

        textView=(TextView) view.findViewById(R.id.textView);
        textView.setText(R.string.text_erweitert);
        textView.setTextSize(20);

        Ergebnisse = (TextView) view.findViewById(R.id.Ergebnisse);
        Ergebnisse.setText(R.string.vier_Platzhalter);
        Ergebnisse.setTextSize(20);

        info = (TextView) view.findViewById(R.id.info);
        info.setText(R.string.Genauigkeit_Text);
        info.setTextSize(10);

        tv_Genauigkeit = (TextView) view.findViewById(R.id.tv_Genauigkeit);
        tv_Genauigkeit.setText(R.string.Genauigkeit_Text);
        tv_Genauigkeit.setTextSize(10);

        vibrator=(Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
    }


    /**
     * locationManagerInit() initialisiert den Locationlistener und -manager.
     *      onLocationChanged wird in die Methode "knopfdruck" regelmäßig aufgerufen. dabei werden Längen- und Breitengrad, sowie Höhe über normal Null und die Genauigkeit der Messung bestimmt.
     *      Außerdem bildet es nach den ersten 20 Messungen von immer 10 Messugnen den Durchschnitt und trägt diesen in das Koordinaten System.
     *
     *      onProviderDisabled() wird automatisch vom System aufgerufen, sollte das GPS deaktiviert werden. Daraufhin öffnet sich ein Dialogfenster, welches bittet as GPS anzuschalten.
     *
     * zuletzt wird geprüft, ob alle erforderlichen Befugnisse erteilt wurden. Diese werden durch das Manifest erfragt.
     */
    private void locationManagerInit()
    {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                int i=0;

                laenge=location.getLatitude();
                breite= location.getLongitude();
                hoehe=location.getAltitude();
                genauigkeit= location.getAccuracy();


                if(fixierungStart<=20)          //nötig, da die ersten Werte nicht sehr genau sind (Abweichungen von 50-60Metern vorkommen)
                {
                    if(fixierungStart==20)
                    {
                        xPosGraph=0;
                        zwischenhoehe=hoehe;

                        starthoehe=hoehe;
                        starthoehe=starthoehe*100;
                        starthoehe=Math.round(starthoehe);
                        starthoehe=starthoehe/100;
                        Toast.makeText(getActivity(),getString(R.string.Starthoehe)+starthoehe+getString(R.string.Meter),Toast.LENGTH_LONG).show();
                        series.appendData(new DataPoint(xPosGraph,0),true,5000);
                        graph.addSeries(series);
                        xPosGraph=1;
                    }
                    fixierungStart++;
                }
                else
                {
                    datenGraph[datenGraphZaehler]=hoehe;
                    datenGraphZaehler++;
                    if(datenGraphZaehler==10)
                    {
                        for(i=0;i<10;i++)
                        {
                            datenGraphDurchschnitt=datenGraphDurchschnitt+datenGraph[i];
                        }
                        datenGraphZaehler=0;
                        datenGraphDurchschnitt=datenGraphDurchschnitt/10;
                        datenGraphDurchschnitt=datenGraphDurchschnitt-starthoehe;
                        series.appendData(new DataPoint(xPosGraph,datenGraphDurchschnitt),true,5000);            //eventuell noch die 500 auf einen Variablen wert legen,sodass es unendlich lange laufen kann
                        xPosGraph++;
                        graph.addSeries(series);
                    }
                }



                if(zustandDaten==0)
                {
                    datenGenau();
                }
                else
                {
                    datenEinfach();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.GPS_aktivieren)
                        .setMessage(R.string.Aktivieren_Sie_GPS)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("D","INTENT hilfe");
                                Intent hilfe = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(hilfe);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                zustand=1;
                                knopfdruck();
                                textView.setText(R.string.Zugriff_verweigert);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET
                }, 0);
            }

        } else
        {
            knopfInitialisieren();
        }
    }


    /**
     * datenGenau() rundet die durch den locationListener ermittelten Werte und schreibt sie in die entsprechende TextView. Außerdem wird geprüft ob das Benachrichtigungsintervall erreicht wurde.
     */
    private void datenEinfach()
    {
        double hoehendifferenz;
        laenge=laenge*100;
        laenge=Math.round(laenge);
        laenge=laenge/100;

        breite=breite*100;
        breite=Math.round(laenge);
        breite=breite/100;

        hoehe=hoehe*100;
        hoehe=Math.round(hoehe);
        hoehe=hoehe/100;

        hoehendifferenz=zwischenhoehe-hoehe;
        if(benachrichtigungsintervall!=-1)
        {
            if (hoehendifferenz >= benachrichtigungsintervall || hoehendifferenz <= 0 - benachrichtigungsintervall)
            {
                Toast.makeText(getActivity(), "Höhe erreicht", Toast.LENGTH_LONG).show();
                zwischenhoehe = hoehe;//Benachrichtigung hier einfügen
                vibrator.vibrate(1000);
            }
        }

        Ergebnisse.setText("\n"+laenge+"\n"+breite+"\n"+hoehe);
        tv_Genauigkeit.setText("Genauigkeit in Metern: "+genauigkeit);
    }


    /**
     * datenEinfach() schreib die durch den locationListener ermittelten Werte wie sie sind in die entaprechende TextView. Außerdem wird geprüft ob das Benachrichtigungsintervall erreicht wurde.
     */
    private void datenGenau()
    {
        double hoehendifferenz;

        hoehendifferenz=zwischenhoehe-hoehe;
        if(benachrichtigungsintervall!=-1)
        {
            if (hoehendifferenz >= benachrichtigungsintervall || hoehendifferenz <= 0 - benachrichtigungsintervall)
            {
                Toast.makeText(getActivity(), "Höhe erreicht", Toast.LENGTH_LONG).show();
                zwischenhoehe = hoehe;//Benachrichtigung hier einfügen
            }
        }

        Ergebnisse.setText("\n"+laenge+"\n"+breite+"\n"+hoehe);
        tv_Genauigkeit.setText("Genauigkeit in Metern: "+genauigkeit);;
    }
}