package com.example.chirath.security;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    UsbDevice device;
    UsbDeviceConnection connection;
    UsbManager usbManager;
    UsbSerialDevice serialPort;
    PendingIntent pendingIntent;
    boolean isSerialStarted = false;

    Context c = this;

    Button startButton;
    Button stop;
    Button light;

    TextView text;

    boolean isLedON = false;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) {
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERMISSION NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                //onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //can add something to close the connection
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stop = (Button) findViewById(R.id.stop);
        startButton = (Button) findViewById(R.id.Connect);
        text = (TextView) findViewById(R.id.textView);
        light = (Button) findViewById(R.id.testAlarm);

        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(broadcastReceiver, filter);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickStart(v);
                text.setText("Connection Sucessfull");
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickToggle(v);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               try {
                   Intent callIntent = new Intent(Intent.ACTION_CALL);
                   callIntent.setData(Uri.parse("tel:8547801861"));

                   if (ActivityCompat.checkSelfPermission(c, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                       // TODO: Consider calling
                       //    ActivityCompat#requestPermissions
                       // here to request the missing permissions, and then overriding
                       //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                       //                                          int[] grantResults)
                       // to handle the case where the user grants the permission. See the documentation
                       // for ActivityCompat#requestPermissions for more details.
                       return;
                   }
                   startActivity(callIntent);
               } catch (ActivityNotFoundException e) {
                   Log.e("dialing", "Call failed", e);
               }
           }
        });
    }

    public void onClickStart(View view) {


        if (!isSerialStarted) {
            usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
            if (!usbDevices.isEmpty()) {
                boolean keep = true;
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    device = entry.getValue();
                    int deviceVID = device.getVendorId();

                    if (deviceVID == 1027 || deviceVID == 9025) { //Arduino Vendor ID
                        usbManager.requestPermission(device, pendingIntent);
                        keep = false;
                    } else {
                        connection = null;
                        device = null;
                    }
                    if (!keep)
                        break;
                }
            }
        }
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                //data.concat("/n");


                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:8547801861"));

                        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);
                    } catch (ActivityNotFoundException e) {
                        Log.e("dialing", "Call failed", e);
                    }

                
                    Log.e("datas", data);

                tvAppend(text, data);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private void tvAppend(final TextView tv, final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (text != null) {
                    tv.append(text);
                }
            }
        });
    }


    public void onClickToggle(View view) {
        if (isLedON == false) {
            isLedON = true;
            serialPort.write("TONLED".getBytes());
        } else {
            isLedON = false;
            serialPort.write("TOFFLED".getBytes());
        }
    }


}
