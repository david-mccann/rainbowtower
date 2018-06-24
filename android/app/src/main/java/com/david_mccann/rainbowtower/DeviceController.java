package com.david_mccann.rainbowtower;

/**
 * Created by dmc on 17-Jun-17.
 */

public interface DeviceController {
    int getBrightness();
    void setBrightness(int brightness);
    void sendFrame(byte[] frame);
    int getNumLeds();
    void updateWifiSettings(WiFiSettings settings);
}
