package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * Created by lthyr_000 on 26.09.2017.
 */

public class nubibusmeterFragment extends Fragment implements Camera.PreviewCallback{
    private View view;
    private CameraView cameraView;
    public static nubibusmeterFragment fragment;
    private Handler h;
    private Runnable r;
    private NV21Image bild;
    private final int pxlgp=10;
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
        Runnable r2=new Runnable() {
            @Override
            public void run() {
                RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(800);

                view.findViewById(R.id.cam_switch).startAnimation(rotate);
            }
        };

        h.postDelayed(r2, 800);

        return view;
    }

    public CameraView getCameraView(){
        return cameraView;
    }

    @Override
    public void onPreviewFrame(byte[] data, final Camera camera) {
        //Log.d("DEBUG","onPreviewFrame");

        if(data==null || camera == null){
            h.postDelayed(r,1200);
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
        try {
            bild = new NV21Image(data, cameraView.getCameraSize().width, cameraView.getCameraSize().height);
        } catch(NullPointerException e) {e.printStackTrace();}
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
        anteilanzeigen(w,b,s,n);

        cameraView.setOneShotPreview(this);
    }

    private void anteilanzeigen(int w,int b,int s,int nul) {
        int pg=w+b+s+nul;
        TextView textView=(TextView)view.findViewById(R.id.anteile);
        String t= "Weißanteil: "+w*100/pg+"% \n" +
                "Blauanteil: "+b*100/pg+"% \n" +
                "Schwarzanteil: "+s*100/pg+"% \n" +
                "Nullanteil: "+nul*100/pg+"%";
        textView.setText(t);
    }

    private nubibusmeterFragment getInstance(){
        return this;
    }
}
