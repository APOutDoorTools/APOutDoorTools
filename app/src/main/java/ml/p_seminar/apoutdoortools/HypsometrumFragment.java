package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lthyr_000 on 26.09.2017.
 */

public class HypsometrumFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        view=inflater.inflate(R.layout.hypsometrum,container,false);
        return view;
    }
}
