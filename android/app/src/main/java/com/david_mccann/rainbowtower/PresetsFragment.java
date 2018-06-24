package com.david_mccann.rainbowtower;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class PresetsFragment extends Fragment {

    AsyncTask<Void, Void, Void> updateTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_presets, container, false);

        RadioButton rainbowButton = view.findViewById(R.id.rainbowButton);
        rainbowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask = new AsyncUpdateTask((DeviceController)getActivity());
                updateTask.execute();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        updateTask.cancel(false);
    }
}
