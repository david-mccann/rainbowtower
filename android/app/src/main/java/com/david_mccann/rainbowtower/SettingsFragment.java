package com.david_mccann.rainbowtower;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by dmc on 18-May-17.
 */

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final DeviceController deviceController = (DeviceController) getActivity();
        TextView deviceSettingsTextView = view.findViewById(R.id.deviceSettingsTextView);
        String deviceSettingsString = String.format(getResources().getString(R.string.device_settings),
                deviceController.getNumLeds());
        deviceSettingsTextView.setText(deviceSettingsString);

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ssid = ((EditText) view.findViewById(R.id.ssidEdit)).getText().toString();
                String pass = ((EditText) view.findViewById(R.id.passEdit)).getText().toString();
                deviceController.updateWifiSettings(new WiFiSettings(ssid, pass));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("WiFi Settings")
                        .setMessage("WiFi settings updated")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return view;
    }
}
