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

public class nubibusmeterFragment extends Fragment implements Camera.PreviewCallback{
    private View view;
    private CameraView cameraView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.nubibusmeter,container,false);
        cameraView=(CameraView) view.findViewById(R.id.camera);
        return view;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        for(byte b : data){
            Log.d("",b+"");
        }
        cameraView.setOneShotPreview(this);
    }
}
