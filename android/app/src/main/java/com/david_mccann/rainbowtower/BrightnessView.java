package com.david_mccann.rainbowtower;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by dmc on 25-May-17.
 */

public class BrightnessView extends LinearLayout {
    DeviceController deviceController;

    public BrightnessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.deviceController = (DeviceController) context;

        View view = inflate(context, R.layout.brightness_view, this);

        final TextView valueTextView = view.findViewById(R.id.brightnessValueTextView);
        valueTextView.setText(String.valueOf(this.deviceController.getBrightness()));

        final SeekBar brightnessSeekBar = view.findViewById(R.id.brightnessSeekBar);
        brightnessSeekBar.setProgress(this.deviceController.getBrightness());
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                valueTextView.setText(String.valueOf(value));
                deviceController.setBrightness(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void setDeviceController(DeviceController deviceController) {
        this.deviceController = deviceController;
    }
}
