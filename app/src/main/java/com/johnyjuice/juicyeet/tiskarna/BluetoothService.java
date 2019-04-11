
package com.johnyjuice.juicyeet.tiskarna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_CONNECTION_LOST = 5;
    public static final int MESSAGE_UNABLE_CONNECT = 6;
    private static final String NAME = "BTPrinter";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState = 0;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BluetoothService(Context context, Handler handler) {
        this.mHandler = handler;
    }

    public synchronized boolean isAvailable() {
        if (this.mAdapter == null) {
            return false;
        }
        return true;
    }

    public synchronized boolean isBTopen() {
        if (!this.mAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    public synchronized BluetoothDevice getDevByMac(String mac) {
        return this.mAdapter.getRemoteDevice(mac);
    }

    public synchronized BluetoothDevice getDevByName(String name) {
        BluetoothDevice tem_dev = null;
        Set<BluetoothDevice> pairedDevices = this.getPairedDev();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().indexOf(name) == -1) continue;
                tem_dev = device;
                break;
            }
        }
        return tem_dev;
    }

    public synchronized void sendMessage(String message, String charset) {
        if (message.length() > 0) {
            byte[] send;
            byte[] left = new byte[]{ 0x1b, 0x61, 0x00 };
            byte[] right = new byte[]{ 0x1b, 0x61, 0x02 };
            try {
                send = message.getBytes("852");
            }
            catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }

            byte[] center = new byte[]{ 0x1b, 0x74, 0x12 };
            this.write(center);
            this.write(left);
            this.write(send);

            byte[] tail = new byte[3];
            tail[0] = 10;
            tail[1] = 13;
            this.write(tail);
        }
    }

    public synchronized Set<BluetoothDevice> getPairedDev() {
        Set dev = null;
        dev = this.mAdapter.getBondedDevices();
        return dev;
    }

    public synchronized boolean cancelDiscovery() {
        return this.mAdapter.cancelDiscovery();
    }

    public synchronized boolean isDiscovering() {
        return this.mAdapter.isDiscovering();
    }

    public synchronized boolean startDiscovery() {
        return this.mAdapter.startDiscovery();
    }

    private synchronized void setState(int state) {
        this.mState = state;
        this.mHandler.obtainMessage(1, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized void start() {
        Log.d((String)"BluetoothService", (String)"start");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread == null) {
            this.mAcceptThread = new AcceptThread();
            this.mAcceptThread.start();
        }
        this.setState(1);
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d((String)"BluetoothService", (String)("connect to: " + (Object)device));
        if (this.mState == 2 && this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        this.mConnectThread = new ConnectThread(device);
        this.mConnectThread.start();
        this.setState(2);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d((String)"BluetoothService", (String)"connected");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }
        this.mConnectedThread = new ConnectedThread(socket);
        this.mConnectedThread.start();
        Message msg = this.mHandler.obtainMessage(4);
        this.mHandler.sendMessage(msg);
        this.setState(3);
    }

    public synchronized void stop() {
        Log.d((String)"BluetoothService", (String)"stop");
        this.setState(0);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(byte[] out) {
        ConnectedThread r;
        BluetoothService bluetoothService = this;
        synchronized (bluetoothService) {
            if (this.mState != 3) {
                return;
            }
            r = this.mConnectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        this.setState(1);
        Message msg = this.mHandler.obtainMessage(6);
        this.mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        Message msg = this.mHandler.obtainMessage(5);
        this.mHandler.sendMessage(msg);
    }

    static /* synthetic */ void access$4(BluetoothService bluetoothService, ConnectThread connectThread) {
        bluetoothService.mConnectThread = connectThread;
    }

    private class AcceptThread
    extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = BluetoothService.this.mAdapter.listenUsingRfcommWithServiceRecord("BTPrinter", MY_UUID);
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"listen() failed", (Throwable)e);
            }
            this.mmServerSocket = tmp;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */

        public void cancel() {
            Log.d((String)"BluetoothService", (String)("cancel " + this));
            try {
                this.mmServerSocket.close();
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"close() of server failed", (Throwable)e);
            }
        }
    }

    private class ConnectThread
    extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"create() failed", (Throwable)e);
            }
            this.mmSocket = tmp;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Log.i((String)"BluetoothService", (String)"BEGIN mConnectThread");
            this.setName("ConnectThread");
            BluetoothService.this.mAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
            }
            catch (IOException e3) {
                BluetoothService.this.connectionFailed();
                try {
                    this.mmSocket.close();
                }
                catch (IOException e2) {
                    Log.e((String)"BluetoothService", (String)"unable to close() socket during connection failure", (Throwable)e2);
                }
                BluetoothService.this.start();
                return;
            }
            BluetoothService e3 = BluetoothService.this;
            synchronized (e3) {
                BluetoothService.access$4(BluetoothService.this, null);
            }
            BluetoothService.this.connected(this.mmSocket, this.mmDevice);
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"close() of connect socket failed", (Throwable)e);
            }
        }
    }

    private class ConnectedThread
    extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d((String)"BluetoothService", (String)"create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"temp sockets not created", (Throwable)e);
            }
            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        public void write(byte[] buffer) {
            try {
                this.mmOutStream.write(buffer);
                BluetoothService.this.mHandler.obtainMessage(3, -1, -1, (Object)buffer).sendToTarget();
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"Exception during write", (Throwable)e);
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            }
            catch (IOException e) {
                Log.e((String)"BluetoothService", (String)"close() of connect socket failed", (Throwable)e);
            }
        }
    }

}

