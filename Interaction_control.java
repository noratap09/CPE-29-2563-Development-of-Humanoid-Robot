package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

public class Interaction_control extends AppCompatActivity {
    Button btn_stt;
    TextView txt_stt;
    BluetoothSocket bt_socket;
    private final int REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_control);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bt_socket = MainActivity.bt_socket;

        btn_stt = (Button)findViewById(R.id.btn_stt);
        txt_stt = (TextView)findViewById(R.id.txt_stt);

        btn_stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Sorry your device not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;
                while (true) {
                    try {
                        //Check Mode Change
                        bytes = bt_socket.getInputStream().read(buffer);
                        String str = new String(buffer, 0,bytes);
                        System.out.println(str);
                        if(str.equals("M"))
                        {
                            System.out.println("Mode M");
                            Intent to_manual = new Intent(getApplicationContext(), Manual_control.class);
                            startActivity(to_manual);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        //Check BT Connect
                        System.out.println("BT DISCONECT!");
                        Intent to_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(to_main);
                        break;
                    }
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txt_stt.setText(result.get(0).toString());

                    if(result.get(0).toString().equals("สวัสดี"))
                    {
                        try {
                            bt_socket.getOutputStream().write("hello\n".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }
}