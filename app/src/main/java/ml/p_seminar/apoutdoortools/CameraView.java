package ml.p_seminar.apoutdoortools;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

import static android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS;

/**
 * Created by lthyr_000 on 26.09.2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    
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
        //camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.setParameters(parameters);
        }catch(Exception e){
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
