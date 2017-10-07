package ml.p_seminar.apoutdoortools;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder surfaceHolder;
    Camera camera;

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



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }

        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);
        }catch(Exception e){
            e.printStackTrace();
        }
        nubibusmeterFragment.fragment.onPreviewFrame(null,camera);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            Log.d("breite",width+"");
            Log.d("hoehe",height+"");
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);
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

    public void setOneShotPreview(Camera.PreviewCallback callback) {
        if(camera!=null) {
            //Log.d("DEBUG","setOneShotPreviewCallback");

            camera.setOneShotPreviewCallback(callback);
        }
    }
}
