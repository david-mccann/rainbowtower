package com.david_mccann.rainbowtower;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dmc on 18-May-17.
 */

public class WarningFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        String text = getArguments().getString("Text");
        TextView textView = view.findViewById(R.id.label);
        textView.setText(text);
        return view;
    }
}
