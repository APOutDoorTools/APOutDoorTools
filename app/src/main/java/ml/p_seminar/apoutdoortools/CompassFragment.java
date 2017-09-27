package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by felix kühn_000 on 27.09.2017.
 */

public class CompassFragment extends Fragment implements SensorEventListener {

    private View view;
    ImageView img_compass;
    int Azimuth;
    private SensorManager sensorManager;
    private Sensor rotationY, accellerometer, magnetometer;
    private float[] rMat = new float[9];
    private float[] oriantation = new float[9];
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean haveSensor = false, havesensor2 = false;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        img_compass = (ImageView) getActivity().findViewById(R.id.img_compass);
        start();

        view=inflater.inflate(R.layout.hypsometrum,container,false);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    public void start() {
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)==null) {
            if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)==null || sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)==null) {

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
