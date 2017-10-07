package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lthyr_000 on 26.09.2017.
 */

public class nubibusmeterFragment extends Fragment implements Camera.PreviewCallback{
    private View view;
    private CameraView cameraView;
    public static nubibusmeterFragment fragment;
    private int anInt=0;
    private Handler h;
    private Runnable r;
    private NV21Image bild;
    private final int pxlgp=5;
    private int w=0;
    private int s=0;
    private int b=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        view=inflater.inflate(R.layout.nubibusmeter,container,false);
        cameraView=(CameraView) view.findViewById(R.id.camera);
        fragment=this;

        h=new Handler();
        r=new Runnable() {
            @Override
            public void run() {
                cameraView.setOneShotPreview(getInstance());
            }
        };

        return view;
    }

    public CameraView getCameraView(){
        return cameraView;
    }

    @Override
    public void onPreviewFrame(byte[] data, final Camera camera) {
        //Log.d("DEBUG","onPreviewFrame");

        if(data==null){
            h.postDelayed(r,20);
            return;
        }

        /*
        data=new byte[3];
        data[0]= 0x52;
        data[1]= (byte)0xF0;
        data[2]= (byte)0x5A;
        */

        w=0;
        s=0;
        b=0;
        int n=0;

        bild=new NV21Image(data,cameraView.getWidth(),cameraView.getHeight());
        //1,1);

        for(int I=0; I<bild.getBreite();I+=pxlgp){
            for(int Q=0; Q<bild.getHoehe();Q+=pxlgp){
                switch (bild.istPixelFarbig(I,Q)){
                    case BLAU:
                        b++;
                        break;
                    case WEIß:
                        w++;
                        break;
                    case SCHWARZ:
                        s++;
                        break;
                    case NULL:
                        n++;
                        break;
                }
            }
        }
        int pg=w+b+s+n;
        anInt++;
        if(anInt>10){
            anInt=0;
            Log.e("erster Pixel NV21","Y "+data[0]+" Cr "+data[1]+" Cb "+data[2]);
            int[] x=bild.holePixel(0,0);
            Log.e("erster Pixel RGB",""+ Color.red(x[1])+" "+Color.green(x[1])+" "+Color.blue(x[1]));
            Log.e("weißanteil: ",""+w/pg+"%");
            Log.e("blauanteil: ",""+b/pg+"%");
            Log.e("schwarzanteil: ",""+s/pg+"%");
            Log.e("nullanteil: ",""+n/pg+"%");
            Log.e("--------------","--------------");
        }

        cameraView.setOneShotPreview(this);
    }


    private nubibusmeterFragment getInstance(){
        return this;
    }
}
