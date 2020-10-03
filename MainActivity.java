package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btn_Scan;
    ListView listV_device;
    TextView lab_status;

    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btArray;
    public static BluetoothSocket bt_socket;


    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        btn_Scan = (Button)findViewById(R.id.btn_scan);
        listV_device = (ListView)findViewById(R.id.listV_device);
        lab_status = (TextView)findViewById(R.id.lab_status);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!myBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
    }

    private void implementListeners()
    {
        btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size() > 0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listV_device.setAdapter(arrayAdapter);
                }
            }
        });

        listV_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bt_device = myBluetoothAdapter.getRemoteDevice(btArray[position].getAddress());
                try {
                     bt_socket = bt_device.createRfcommSocketToServiceRecord(MY_UUID);
                     bt_socket.connect();
                     lab_status.setText("Connected");
                     Intent to_manual = new Intent(getApplicationContext(),Manual_control.class);
                     startActivity(to_manual);

                } catch (IOException e) {
                    e.printStackTrace();
                    lab_status.setText("Connection Fail!");
                }

            }
        });
    }

}