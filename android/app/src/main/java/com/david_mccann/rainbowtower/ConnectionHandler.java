package com.david_mccann.rainbowtower;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dmc on 04-May-17.
 */

class ConnectionHandler extends Handler {
    private static final int PORT = 7777;

    private static int TYPE_FRAME = 1;
    private static int TYPE_BRIGHTNESS = 2;
    private static int TYPE_WIFI = 3;
    private static int TYPE_DISCOVERY = 4;
    private static int TYPE_PING = 5;


    static int MSG_WIFI_SETTINGS = 900;
    static int MSG_CONNECT = 1000;
    static int MSG_DISCONNECT = 1100;
    static int MSG_FRAME = 1200;
    static int MSG_BRIGHTNESS = 2400;

    private ScheduledExecutorService executorService;
    private DatagramSocket socket = new DatagramSocket();
    private InetAddress remoteAddress = null;
    private Handler uiHandler;

    private Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
                packer.packInt(TYPE_PING).close();
                byte[] discoveryMessage = packer.toByteArray();

                DatagramPacket packet = new DatagramPacket(discoveryMessage, discoveryMessage.length, remoteAddress, PORT);
                socket.send(packet);

                Log.d("rainbow", "sending ping");

                byte[] buffer = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(receivePacket);
                    executorService.schedule(pingRunnable, 5000, TimeUnit.MILLISECONDS);
                } catch (SocketTimeoutException e) {
                    Log.d("rainbow", "ping: no reply");
                    // device did not respond to ping
                    sendEmptyMessage(ConnectionHandler.MSG_DISCONNECT);
                    uiHandler.sendEmptyMessage(MainActivity.MSG_DISCONNECTED);
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendEmptyMessage(ConnectionHandler.MSG_DISCONNECT);
                executorService.schedule(discoveryRunnable, 500, TimeUnit.MILLISECONDS);
            }
        }
    };

    private Runnable discoveryRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
                packer.packInt(TYPE_DISCOVERY).close();
                byte[] discoveryMessage = packer.toByteArray();

                DatagramPacket packet = new DatagramPacket(discoveryMessage, discoveryMessage.length,
                        InetAddress.getByName("255.255.255.255"), PORT);
                socket.send(packet);

                Log.d("rainbow", "sending discovery message");

                byte[] buffer = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(receivePacket);
                    if (remoteAddress == null && receivePacket.getLength() > 0) {
                        sendConnectMessage(receivePacket);
                    }
                } catch (SocketTimeoutException e) {
                    // this is ok, no device was found
                    Log.d("rainbow", "discovery: no reply");
                    executorService.schedule(discoveryRunnable, 5000, TimeUnit.MILLISECONDS);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendConnectMessage(DatagramPacket receivePacket) throws IOException {
            remoteAddress = receivePacket.getAddress();
            obtainMessage(ConnectionHandler.MSG_CONNECT, receivePacket.getAddress()).sendToTarget();

            Message uiMessage = uiHandler.obtainMessage(MainActivity.MSG_CONNECTED);
            byte[] data = receivePacket.getData();
            if (receivePacket.getLength() == 2 && new String(data, 0, 2).equals("OK")) {
                // legacy firmware without version indicator
                uiMessage.obj = new DeviceSettings(0, 0, 0);
            } else {
                final MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(receivePacket.getData());
                int firmwareVersion = unpacker.unpackShort();
                int brightness = unpacker.unpackShort();
                int numLeds = unpacker.unpackShort();
                uiMessage.obj = new DeviceSettings(firmwareVersion, brightness, numLeds);
            }
            uiMessage.sendToTarget();
        }
    };

    ConnectionHandler(Looper looper) throws SocketException {
        super(looper);
        this.socket.setBroadcast(true);
        this.socket.setSoTimeout(4000);
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(discoveryRunnable, 500, TimeUnit.MILLISECONDS);
    }

    void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            if (msg.what == MSG_WIFI_SETTINGS) {
                sendWiFiSettings((WiFiSettings) msg.obj);
            } else if (msg.what == MSG_CONNECT) {
                remoteAddress = ((InetAddress)msg.obj);
                executorService.schedule(pingRunnable, 5000, TimeUnit.MILLISECONDS);
            } else if (msg.what == MSG_DISCONNECT) {
                remoteAddress = null;
                executorService.schedule(discoveryRunnable, 500, TimeUnit.MILLISECONDS);
            } else if (msg.what == MSG_FRAME) {
                sendFrame((byte[]) msg.obj);
            } else if (msg.what == MSG_BRIGHTNESS) {
                sendBrightness((int) msg.obj);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendWiFiSettings(WiFiSettings wiFiSettings) throws IOException {
        final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packInt(TYPE_WIFI)
                .packString(wiFiSettings.ssid)
                .packString(wiFiSettings.pass)
                .close();
        sendMessage(packer.toByteArray());
    }

    public void sendFrame(byte[] frame) throws IOException {
        final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packInt(TYPE_FRAME);
        for (int i = 0; i < frame.length; ++i) {
            packer.packByte(frame[i]);
        }
        packer.close();
        sendMessage(packer.toByteArray());
    }

    private void sendBrightness(int brightness) throws IOException {
        final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packInt(TYPE_BRIGHTNESS).packInt(brightness).close();
        sendMessage(packer.toByteArray());
    }

    private void sendMessage(byte[] message) {
        try {
            if (remoteAddress != null) {
                DatagramPacket packet = new DatagramPacket(message, message.length, remoteAddress, PORT);
                socket.send(packet);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
