package com.david_mccann.rainbowtower;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skydoves.colorpickerview.ColorListener;
import com.skydoves.colorpickerview.ColorPickerView;

/**
 * Created by dmc on 01-May-17.
 */

public class ColorFragment extends Fragment {

    int numLED;
    int selectedColors[] = new int[4];
    byte[] frame;
    DeviceController deviceController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color, container, false);

        this.deviceController = (DeviceController) getActivity();
        this.numLED = this.deviceController.getNumLeds();
        this.frame = new byte[3 * this.numLED];

        ColorPickerView colorPickerView1 = view.findViewById(R.id.colorPickerView1);
        colorPickerView1.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColors[0] = color;
                sendFrame();
            }
        });

        ColorPickerView colorPickerView2 = view.findViewById(R.id.colorPickerView2);
        colorPickerView2.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColors[1] = color;
                sendFrame();
            }
        });

        ColorPickerView colorPickerView3 = view.findViewById(R.id.colorPickerView3);
        colorPickerView3.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColors[2] = color;
                sendFrame();
            }
        });

        ColorPickerView colorPickerView4 = view.findViewById(R.id.colorPickerView4);
        colorPickerView4.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColors[3] = color;
                sendFrame();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void sendFrame() {
        for (int i = 0; i < this.numLED; ++i) {
            int color = selectedColors[LedMapper.getInstance().ledToSide(i)];
            this.frame[3 * i] = (byte) Color.red(color);
            this.frame[3 * i + 1] = (byte) Color.green(color);
            this.frame[3 * i + 2] = (byte) Color.blue(color);
        }
        this.deviceController.sendFrame(this.frame);
    }
}
