package com.example.bluetoothsample;

import java.util.UUID;

public class Constants {
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int CONNECTION_FAILED = 4;
    static final int MESSAGE_RECIEVED = 5;
    static final String App_Name = "bluetoothsample";
    static final UUID Connection_ID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
}
