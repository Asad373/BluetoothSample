package com.example.bluetoothsample;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

public class server extends Thread{
    BluetoothServerSocket mSocket;
    Handler mHandler;
    //InputStream inS;
    @SuppressLint("MissingPermission")
    public server(BluetoothAdapter adapter, Handler handler){
        mHandler = handler;
        try {
            mSocket = adapter.listenUsingRfcommWithServiceRecord(Constants.App_Name, Constants.Connection_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        while (socket == null){
            try  {
                Message ms = Message.obtain();
                ms.what = Constants.STATE_CONNECTING;
                mHandler.sendMessage(ms);
                socket = mSocket.accept();
                //inS = socket.getInputStream();
            }catch (Exception e){
                e.printStackTrace();
                Message ms = Message.obtain();
                ms.what = Constants.CONNECTION_FAILED;
                mHandler.sendMessage(ms);
            }
            if(socket != null){
                Message ms = Message.obtain();
                ms.what = Constants.STATE_CONNECTED;
                mHandler.sendMessage(ms);
                sendRecieve recieve = new sendRecieve(socket, mHandler);
                recieve.start();
                //send/receive code


                //

            }
        }



    }
}
