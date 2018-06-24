package com.david_mccann.rainbowtower;

import android.graphics.Color;
import android.os.AsyncTask;

public class AsyncUpdateTask extends AsyncTask<Void, Void, Void> {
    private DeviceController deviceController;

    private int numLED;
    private byte[] frame;
    private int offset;

    AsyncUpdateTask(DeviceController deviceController) {
        this.deviceController = deviceController;

        this.numLED = deviceController.getNumLeds();
        this.frame = new byte[3 * numLED];
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!isCancelled()) {
            offset = (offset + 1) % 256;
            showRainbow(offset);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        return null;
    }

    private void showRainbow(int offset) {
        for (int i = 0; i < this.numLED; ++i) {
            int color = wheel((i + offset) % 256);
            this.frame[3 * i] = (byte) Color.red(color);
            this.frame[3 * i + 1] = (byte) Color.green(color);
            this.frame[3 * i + 2] = (byte) Color.blue(color);
        }
        this.deviceController.sendFrame(this.frame);
    }

    private int wheel(int pos) {
        pos = 255 - pos;
        if (pos < 85) {
            return Color.rgb(255 - pos * 3, 0, pos * 3);
        }
        if (pos < 170) {
            pos -= 85;
            return Color.rgb(0, pos * 3, 255 - pos * 3);
        }
        pos -= 170;
        return Color.rgb(pos * 3, 255 - pos * 3, 0);
    }
}
