package com.david_mccann.rainbowtower;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.net.SocketException;

/**
 * Created by dmc on 25-May-17.
 */

public class ConnectionService extends Service {

    public class LocalBinder extends Binder {
        ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    private final HandlerThread handlerThread = new HandlerThread("ConnectionHandler");
    private ConnectionHandler handler;

    public void setUiHandler(Handler uiHandler) {
        this.handler.setUiHandler(uiHandler);
    }

    @Override
    public void onCreate() {
        handlerThread.start();
        try {
            handler = new ConnectionHandler(handlerThread.getLooper());
        } catch (SocketException e) {
            // FIXME
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public ConnectionHandler getHandler() {
        return this.handler;
    }
}
