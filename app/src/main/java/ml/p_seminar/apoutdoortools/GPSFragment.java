package ml.p_seminar.apoutdoortools;


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
    private TextView Ergebnisse;
    private TextView info;

    private Button erweitert;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int zustand;

    private View view = null;
    private SeekBar seekBar;

    private Vibrator vibrator;

    private int benachrichtigungsintervall;
    private double laenge;
    private double breite;
    private double hoehe;
    private float genauigkeit;
    private int zustandDaten;
    private String provider;
    private int fixierungStart;
    private int zaehlerMessungen;               //Verwendet um aus zehn Messungen den Durchschnittswert zu bilden, der dann im Diagramm angezeigt wird.
    private LineGraphSeries<DataPoint> series;
    private double[] datenGraph;
    private double datenGraphDurchschnitt;
    private int datenGraphZaehler;
    private int xPosGraph;
    private GraphView graph;

    private double starthoehe;
    private double zwischenhoehe;

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
                            zustandDaten=1;
                            erweitert.setText(R.string.erweitert_Modus_Text);
                            Ergebnisse.setTextSize(20);
                            textView.setTextSize(20);
                            textView.setText(R.string.Text_einfach);
                            Ergebnisse.setText(R.string.drei_Platzhalter);
                        }
                        else
                        {
                            zustandDaten=0;
                            erweitert.setText("einfacher Modus");
                            Ergebnisse.setTextSize(15);
                            textView.setTextSize(15);

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

    private void knopfdruck()
    {
        if(zustand==0)
        {
            button.setText(R.string.stop);
            textView.setText(R.string.Signal_wird_gesucht);
            //noinspection MissingPermission
            locationManager.requestLocationUpdates("gps", 2000, 10, locationListener);//Fehlermeldung nicht relevant, da der geforderte "permission check" seperat geprüft wird.
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
        textView.setText(R.string.Text_einfach);
        textView.setTextSize(20);

        Ergebnisse = (TextView) view.findViewById(R.id.Ergebnisse);
        Ergebnisse.setText(R.string.drei_Platzhalter);
        Ergebnisse.setTextSize(20);
        //Ergebnisse.setGravity();

        info = (TextView) view.findViewById(R.id.info);
        info.setText(R.string.Genauigkeit_Text);
        info.setTextSize(10);

        vibrator=(Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
    }

    private void locationManagerInit()
    {


        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                int i=0;
                //Log.e("debug","neue position");


                laenge=location.getLatitude();
                breite= location.getLongitude();
                hoehe=location.getAltitude();
                genauigkeit= location.getAccuracy();
                provider= location.getProvider();

                if(fixierungStart<=20)          //nötig, da die ersten Werte nicht sehr genau sind (Abweichungen von 50-60Metern vorkommen)
                {
                    if(fixierungStart==20)
                    {
                        zwischenhoehe=hoehe;

                        starthoehe=hoehe;
                        starthoehe=starthoehe*100;
                        starthoehe=Math.round(starthoehe);
                        starthoehe=starthoehe/100;
                        Toast.makeText(getActivity(),getString(R.string.Starthoehe)+starthoehe+getString(R.string.Meter),Toast.LENGTH_LONG).show();
                        series.appendData(new DataPoint(xPosGraph,0),true,500);
                        xPosGraph++;
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
                        series.appendData(new DataPoint(xPosGraph,datenGraphDurchschnitt),true,500);            //eventuell noch die 500 auf einen Variablen wert legen,sodass es unendlich lange laufen kann
                        xPosGraph++;

                    }
                }



                if(zustandDaten==0)
                {
                    datenEinfach();
                }
                else
                {
                    datenGenau();
                }
                //textView.setText("Koordinaten\n\n "+location.getLatitude()+ "\n" + location.getLongitude() + "\nProvider:\t" + location.getProvider() + "\nHöhe:\t" + location.getAltitude() + "\nGenauigkeit:\t" + location.getAccuracy()+" mögliche Abweichung in Metern\n");
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)     //Hier wird geprüft, ob eine bestimmte SDK vorhanden ist.
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

    private void datenGenau()
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

        hoehendifferenz=hoehe-zwischenhoehe;
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
        info.setText("Genauigkeit in Metern: "+genauigkeit);
    }

    private void datenEinfach()
    {
        double hoehendifferenz;

        hoehendifferenz=hoehe-zwischenhoehe;
        if(benachrichtigungsintervall!=-1)
        {
            if (hoehendifferenz >= benachrichtigungsintervall || hoehendifferenz <= 0 - benachrichtigungsintervall)
            {
                Toast.makeText(getActivity(), "Höhe erreicht", Toast.LENGTH_LONG).show();
                zwischenhoehe = hoehe;//Benachrichtigung hier einfügen
            }
        }

        Ergebnisse.setText("\n"+laenge+"\n"+breite+"\n"+hoehe+"\n"+provider);
        info.setText("Genauigkeit in Metern: "+genauigkeit);;
    }
}


