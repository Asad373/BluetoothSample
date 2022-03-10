package com.example.bluetoothsample;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class sendRecieve extends Thread{

    private final BluetoothSocket mSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    Handler  mHandler;

    public sendRecieve(BluetoothSocket socket, Handler handler){
        mSocket = socket;
        mHandler = handler;
        InputStream is = null;
        OutputStream os = null;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream = is;
        outputStream = os;


    }

    @Override
    public void run() {

        byte[] buffer = new byte[1024];

        while(true){
            try {
                //InputStream in = socket.getInputStream();
                inputStream.read(buffer);
                mHandler.obtainMessage(Constants.MESSAGE_RECIEVED,buffer).sendToTarget();
                //inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
