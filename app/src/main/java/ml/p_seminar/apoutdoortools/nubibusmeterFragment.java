package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lthyr_000 on 26.09.2017.
 */

public class NubibusmeterFragment extends Fragment implements Camera.PreviewCallback{
    private View view;
    private CameraView cameraView;
    public static NubibusmeterFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.nubibusmeter,container,false);
        cameraView=(CameraView) view.findViewById(R.id.camera);
        fragment=this;
        return view;
    }

    public CameraView getCameraView(){
        return cameraView;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.d("DEBUG","onPreviewFrame");
/*
        if(data !=null){
            int i[]=decodeYUV420SP(new int[cameraView.getWidth()*cameraView.getHeight()],data,cameraView.getWidth(),cameraView.getHeight());


            for(int a : i){
                Log.d("i","ergebnis "+a);
            }
        }
*/
        cameraView.setOneShotPreview(this);
    }

    private int[] decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

}
