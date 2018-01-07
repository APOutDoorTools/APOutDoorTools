package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

/**
 Created by lukas thyroff, felix kühn
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
                Toast.makeText(getActivity(),"Berühren --> Auswertung pausieren.",Toast.LENGTH_LONG).show();
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
        //Log.e("datalenght",""+data.length);
        //Log.e("Camerasize","w"+cameraView.getCameraSize().width+" h"+cameraView.getCameraSize().height);

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

        int bewoelkung=bewoelkungberechnen(w,b,s,nul);
        String bewoelkungsstring="";

        bewoelkungsstring = bewoelkung < 25 ? "kaum bewölkt."
                : bewoelkung < 50 ? "etwas bewölkt."
                : bewoelkung < 75 ? "bewölkt"
                : bewoelkung < 100 ? "stark bewölkt."
                : "Kamera auf den Himmel richten";

        int achtel=0;
        if(bewoelkung == 12.5){
            achtel=0;
        }
        else if(bewoelkung < 12.5){
            achtel=1;
        }else if(bewoelkung < 25){
            achtel=2;
        }else if(bewoelkung < 37.5){
            achtel=3;
        }else if(bewoelkung < 50){
            achtel=4;
        }else if(bewoelkung < 62.5){
            achtel=5;
        }else if(bewoelkung < 75){
            achtel=6;
        }else if(bewoelkung < 87.5){
            achtel=7;
        }else if(bewoelkung < 100){
            achtel=8;
        }
        else if(bewoelkung == 100){
            achtel=9;
        }

        TextView textView=(TextView)view.findViewById(R.id.anteile);
        String t= "Weißanteil: "+w*100/pg+"% \n" +
                "Blauanteil: "+b*100/pg+"% \n" +
                "Schwarzanteil: "+s*100/pg+"% \n" +
                "Bewölkung: "+ bewoelkung+"% ; "+achtel+"/8"+"\n"+
                bewoelkungsstring;
        textView.setText(t);
    }

    private int bewoelkungberechnen(int w,int b,int s,int nul){
        int wolken=w+s;
        int himmel=w+s+b+nul;

        return wolken*100/himmel;
    }

    private nubibusmeterFragment getInstance(){
        return this;
    }
}
