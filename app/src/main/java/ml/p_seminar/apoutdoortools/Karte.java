package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.os.Bundle;

public class Karte extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Manipulates the map once available.

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}

