package ml.p_seminar.apoutdoortools;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static ml.p_seminar.apoutdoortools.R.layout.handbuch_layout;

/**
 * Kein Kommentar
 */

public class HbFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        View view=inflater.inflate(handbuch_layout,container,false);
        return view;
    }
}
