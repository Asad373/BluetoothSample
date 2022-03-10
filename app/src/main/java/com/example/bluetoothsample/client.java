package com.example.bluetoothsample;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class client extends Thread{

    BluetoothDevice mDevice;
    BluetoothSocket mSocket;
    Handler mHandler;
    loadDeviceCallBack callBack;
    @SuppressLint("MissingPermission")
    public client(BluetoothDevice device, Handler handler){

        mHandler  = handler;
        mDevice = device;
        try {

            mSocket = device.createRfcommSocketToServiceRecord(Constants.Connection_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadDeviceCallBack(loadDeviceCallBack call){
     this.callBack = call;
    }
    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        String dataa = "Test Payload";
        try {
            mSocket.connect();
            OutputStream outputStream = mSocket.getOutputStream();
            if(mSocket.isConnected()){
                callBack.ConnectedSocket(mSocket);
                Message msg = Message.obtain();
                msg.what = Constants.STATE_CONNECTED;
                mHandler.sendMessage(msg);


                try {
                    byte[] data = dataa.getBytes(StandardCharsets.UTF_8);
                    if (outputStream != null) {
                        outputStream.write(data);

                        //return Constants.SUCCESS_SEND;
                    } else {
                        //return Constants.ERROR_OUTPUT_STREAM;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = Message.obtain();
            msg.what = Constants.CONNECTION_FAILED;
            mHandler.sendMessage(msg);
        }
    }

    public interface loadDeviceCallBack{
        void  ConnectedSocket(BluetoothSocket socket);
    }
}


