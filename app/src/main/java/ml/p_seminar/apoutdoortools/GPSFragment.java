package ml.p_seminar.apoutdoortools;


import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.R;

import static android.content.Context.LOCATION_SERVICE;


public class GPSFragment extends Fragment{
    private Button button;
    private TextView textView;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int zustand;

    private View view;

    @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(android.R.layout.gps, container, false);

        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.gps);

        button = (Button) getActivity().findViewById(R.id.button1);

        textView=(TextView) getView().findViewById(R.id.textView);
        textView.setText("Koordinaten:");
        textView.setTextSize(20);

        zustand=0;


        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {

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
                Intent hilfe = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(hilfe);
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
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if(zustand==0)
                {
                    button.setText("Stop");
                    textView.setText("GPS Signal wird gesucht");
                    locationManager.requestLocationUpdates("gps", 500, 0, locationListener); //(wodurch das Signal zur verfügung gestellt wird, Zeit in Millisekunden, nach der der Standrt erneut überprüft werden soll,Distanz in Metern, nach der der Standort erneut überprüft werden soll)
                    zustand=1;
                }
                else
                {
                    button.setText("Position Anfragen");
                    textView.setText("Koordinaten");
                    locationManager.removeUpdates(locationListener);
                    zustand=0;
                }
            }
        });

    }
}
