package ml.p_seminar.apoutdoortools;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

import static android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder surfaceHolder;
    Camera camera;


    public Camera.Size getCameraSize() {
        return camera.getParameters().getPreviewSize();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context,attrs);
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        //noinspection deprecation
        surfaceHolder.setType(SURFACE_TYPE_PUSH_BUFFERS);
    }
    public CameraView(Context context){
        super(context);
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        //noinspection deprecation
        surfaceHolder.setType(SURFACE_TYPE_PUSH_BUFFERS);
    }

private boolean switchcam=false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        View cam_switch = getRootView().findViewById(R.id.cam_switch);
        cam_switch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchcam=true;
            }
        });
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touch = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // touch move code
                        break;

                    case MotionEvent.ACTION_UP:
                        touch=false;
                        setOneShotPreview(cb);
                        break;
                }
                return true;

            }
        });

        camera = Camera.open(MainActivity.cam);

        camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }

        try {/*
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);*/
        }catch(Exception e){
            e.printStackTrace();
        }
        nubibusmeterFragment.fragment.onPreviewFrame(null,camera);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            /*
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);*/
            camera.setPreviewDisplay(holder);
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);
        }catch(Exception e){
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private boolean touch;
    private Camera.PreviewCallback cb=null;
    public void setOneShotPreview(Camera.PreviewCallback callback) {
        cb=callback;
        if(camera!=null && !touch) {
            //Log.d("DEBUG","setOneShotPreviewCallback");
            if(switchcam){
                switchcam=false;
                switchCam();
            }
            camera.setOneShotPreviewCallback(callback);
        }
    }

    public void switchCam() {
        if(MainActivity.cam < Camera.getNumberOfCameras()-1) {
            MainActivity.cam++;
        } else {
            MainActivity.cam = 0;
        }
        android.app.FragmentManager fragmentmanager=MainActivity.getMFragmentManager();
        fragmentmanager.beginTransaction().replace(R.id.content_frame,new nubibusmeterFragment()).commit();
    }

}