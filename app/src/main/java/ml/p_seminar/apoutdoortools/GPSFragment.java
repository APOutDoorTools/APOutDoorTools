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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.content.Context.LOCATION_SERVICE;


public class GPSFragment extends Fragment{
    private Button button;
    private TextView textView;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int zustand;

    private View view;
    private SeekBar seekBar;

    @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        view=inflater.inflate(R.layout.gps,container,false);

        zustand=0;

        button = (Button) view.findViewById(R.id.button);

        textView=(TextView) view.findViewById(R.id.textView);
        textView.setText("Koordinaten:");
        textView.setTextSize(20);


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
                knopfdruck();
            }
        };
        button.setOnClickListener(onClick);

    }

    private void knopfdruck(){
        if(zustand==0)
        {
            button.setText("Stop");
            textView.setText("GPS Signal wird gesucht");
            //noinspection MissingPermission
            locationManager.requestLocationUpdates("gps", 500, 0, locationListener); //(wodurch das Signal zur verfügung gestellt wird, Zeit in Millisekunden, nach der der Standrt erneut überprüft werden soll,Distanz in Metern, nach der der Standort erneut überprüft werden soll)
            zustand=1;
            Log.d("d","position angefragt");
        }
        else
        {
            button.setText("Position Anfragen");
            textView.setText("Koordinaten");
            locationManager.removeUpdates(locationListener);
            zustand=0;
            Log.d("d","stop");
        }
    }

    private void setSeekBarInit(){
        seekBar=(SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void locationManagerInit(){
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.e("debug","neue position");



                textView.setText("Koordinaten\n\n "+location.getLatitude()+ "\n" + location.getLongitude() + "\nProvider:\t" + location.getProvider() + "\nHöhe:\t" + location.getAltitude() + "\nGenauigkeit:\t" + location.getAccuracy()+" mögliche Abweichung in Metern\n");
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

                builder.setTitle("Activate GPS")
                        .setMessage("Aktivieren Sie GPS!")
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
                                textView.setText("GPS Zugriff verweigert");
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
            return null;
        }
        else
        {
            knopfInitialisieren();
        }
    }

}


