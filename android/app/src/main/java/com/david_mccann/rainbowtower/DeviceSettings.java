package com.david_mccann.rainbowtower;

/**
 * Created by dmc on 17-Jun-17.
 */

class DeviceSettings {
    public int firmwareVersion;
    public int brightness;
    public int numLeds;

    DeviceSettings(int firmwareVersion, int brightness, int numLeds) {
        this.firmwareVersion = firmwareVersion;
        this.brightness = brightness;
        this.numLeds = numLeds;
    }
}
