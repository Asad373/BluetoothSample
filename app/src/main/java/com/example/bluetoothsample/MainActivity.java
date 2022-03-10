package com.example.bluetoothsample;

import static com.example.bluetoothsample.sendRecieve.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    ArrayAdapter adapter_paired_devices;
    BluetoothAdapter b_adapter;
    TextView textView;
    ListView pairedDeviceslist;
    ImageView imageView;
    Set<BluetoothDevice> devices;
    BluetoothDevice mDevice;
    Button button4;
    int counter;
    TextView payloads;
    BluetoothSocket mSocket;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        button4 = findViewById(R.id.button4);
        payloads = findViewById(R.id.payloads);
        pairedDeviceslist = findViewById(R.id.pairedDeviceslist);
        b_adapter = BluetoothAdapter.getDefaultAdapter();
        adapter_paired_devices = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1);
        pairedDeviceslist.setAdapter(adapter_paired_devices);
        devices = b_adapter.getBondedDevices();
        //counter = 2;
        //adapter

        if (b_adapter == null) {
            textView.setText("Blue Tooth is not Available");
        } else {
            textView.setText("Blue Tooth is  Available");
        }

        if (b_adapter.isEnabled()) {
            imageView.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
            loadPairedDevices();
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
        }
        if(devices != null){
            ClickItem();
        }


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){

                case Constants.STATE_LISTENING:
                    textView.setText("Device is in listening mode");
                    break;
                case Constants.CONNECTION_FAILED:
                    showToast("Failed to connect");
                    break;
                case Constants.STATE_CONNECTED:
                    textView.setText("connected");
                    break;
                case Constants.STATE_CONNECTING:
                    textView.setText("Connecting");
                    break;
                case Constants.MESSAGE_RECIEVED:
                    byte[] bytes = (byte[]) msg.obj;
                    String tempMessage = new String(bytes,StandardCharsets. UTF_8);
                    //showToast(tempMessage);
                    showMessage(tempMessage);
                    //payloads.append(tempMessage);
                    break;

            }
            return true;
        }
    });
    @SuppressLint("MissingPermission")
    public void turnOn(View v) {

        if (!b_adapter.isEnabled()) {
            showToast("Turning on blueTooth");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }else{
            showToast("BlueTooth is already Available");
        }

    }
    public void showMessage(String temp){

            payloads.append(temp);


    }

    private void ClickItem() {
    pairedDeviceslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Object[] objects = devices.toArray();
            mDevice = (BluetoothDevice) objects[i];

            client mClient = new client(mDevice, handler);
            mClient.loadDeviceCallBack(new client.loadDeviceCallBack() {
                @Override
                public void ConnectedSocket(BluetoothSocket socket) {
                if(socket.isConnected()){
                    mSocket = socket;
                    Log.e("Connected", "device Connected");
                }
                }
            });
            mClient.start();
        }
    });
    }

    @SuppressLint("MissingPermission")
    public void turnOff (View v){
        if(b_adapter.isEnabled()){
            b_adapter.disable();
            imageView.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);

        }
    }

    @SuppressLint("MissingPermission")
    public void discover (View v){
      /*if(!b_adapter.isDiscovering()){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intent,REQUEST_DISCOVER_BT);
      }*/
        server mServer = new server(b_adapter, handler);
        mServer.start();
    }

    public void pairedDevices (View v){
        

            String dataa = "Test Payload 2";
            writeToStream(dataa);






    }
private void writeToStream(String dataa){
    try {
        OutputStream outputStream = mSocket.getOutputStream();
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

    private void showToast(String message){
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                imageView.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
            }
        }

        if(requestCode == 1){
            if(resultCode == 120){
                showToast("Mark Your Device for Discoverable");
            }
        }
    }

    public void loadPairedDevices(){
      if(b_adapter.isEnabled()){


          for(BluetoothDevice device:devices){
           @SuppressLint("MissingPermission")
           String name = device.getName();
           String address = device.getAddress();
           adapter_paired_devices.add(name +"\n" + address);

          }
      }
    }

}

