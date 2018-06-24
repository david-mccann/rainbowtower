package com.david_mccann.rainbowtower;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.net.SocketException;

public class MainActivity extends AppCompatActivity implements DeviceController {

    public static int MSG_CONNECTED = 1000;
    public static int MSG_DISCONNECTED = 1100;

    private DeviceSettings deviceSettings = null;

    private Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (message.what == MSG_CONNECTED) {
                deviceSettings = (DeviceSettings) message.obj;
                if (firmwareVersionMatches()) {
                    showColorFragment();
                    navigation.setVisibility(View.VISIBLE);
                } else {
                    showWarningFragment(getResources().getString(R.string.firmware_version_mismatch));
                }
            } else if (message.what == MSG_DISCONNECTED) {
                showWarningFragment(getResources().getString(R.string.not_connected));
                navigation.setVisibility(View.INVISIBLE);
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            ConnectionService connectionService = binder.getService();
            connectionHandler = connectionService.getHandler();
            connectionService.setUiHandler(uiHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ConnectionHandler connectionHandler;

    BottomNavigationView navigation;

    public MainActivity() throws SocketException {}

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_color:
                    showColorFragment();
                    break;
                case R.id.navigation_presets:
                    showPresetsFragment();
                    break;
                case R.id.navigation_settings:
                    showSettingsFragment();
                    break;
            }

            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        if (savedInstanceState == null) {
            showWarningFragment(getResources().getString(R.string.not_connected));
            navigation.setVisibility(View.INVISIBLE);
        }
    }

    private void showColorFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new ColorFragment();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        fragmentManager.popBackStack();
    }

    private void showPresetsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new PresetsFragment();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        fragmentManager.popBackStack();
    }

    private void showSettingsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new SettingsFragment();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        fragmentManager.popBackStack();
    }

    private void showWarningFragment(String text) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new WarningFragment();
        Bundle arguments = new Bundle();
        arguments.putString("Text", text);
        fragment.setArguments(arguments);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        fragmentManager.popBackStack();
    }

    private boolean firmwareVersionMatches() {
        int expected = Integer.valueOf(getResources().getString(R.string.FIRMWARE_VERSION));
        return this.deviceSettings.firmwareVersion == expected;
    }

    @Override
    public int getBrightness() {
        return this.deviceSettings.brightness;
    }

    @Override
    public void setBrightness(int brightness) {
        this.connectionHandler.obtainMessage(ConnectionHandler.MSG_BRIGHTNESS, brightness).sendToTarget();
        this.deviceSettings.brightness = brightness;
    }

    @Override
    public void sendFrame(byte[] frame) {
        this.connectionHandler.obtainMessage(ConnectionHandler.MSG_FRAME, frame).sendToTarget();
    }

    @Override
    public int getNumLeds() {
        return this.deviceSettings.numLeds;
    }

    @Override
    public void updateWifiSettings(WiFiSettings settings) {
        this.connectionHandler.obtainMessage(ConnectionHandler.MSG_WIFI_SETTINGS, settings).sendToTarget();
    }
}
