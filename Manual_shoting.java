package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import java.io.IOException;

public class Manual_shoting extends AppCompatActivity {
    RelativeLayout joystick_L, joystick_R;
    ImageView image_joystick, image_border;
    TextView txt_x_L, txt_y_L, txt_x_R, txt_y_R;
    Button btn_walk_mode,btn_shot_R,btn_reload_R,btn_shot_L,btn_reload_L;

    BluetoothSocket bt_socket;

    JoyStickClass js_L, js_R;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_shoting);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        bt_socket = MainActivity.bt_socket;

        txt_x_L = (TextView) findViewById(R.id.txt_x_L);
        txt_y_L = (TextView) findViewById(R.id.txt_y_L);
        txt_x_R = (TextView) findViewById(R.id.txt_x_R);
        txt_y_R = (TextView) findViewById(R.id.txt_y_R);
        btn_walk_mode = (Button) findViewById(R.id.btn_walk_mode);
        btn_shot_L = (Button) findViewById(R.id.btn_shot_L);
        btn_shot_R = (Button)findViewById(R.id.btn_shot_R);
        btn_reload_L = (Button) findViewById(R.id.btn_reload_L);
        btn_reload_R = (Button) findViewById(R.id.btn_reload_R);

        btn_walk_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bt_socket.getOutputStream().write("walk_mode\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Mode M_walk");
                Intent to_manual = new Intent(getApplicationContext(), Manual_control.class);
                startActivity(to_manual);
            }
        });

        btn_shot_R.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    bt_socket.getOutputStream().write("gun_r_shot\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        btn_reload_R.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    bt_socket.getOutputStream().write("gun_r_reload\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        btn_shot_L.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    bt_socket.getOutputStream().write("gun_l_shot\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        btn_reload_L.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    bt_socket.getOutputStream().write("gun_l_reload\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        joystick_L = (RelativeLayout) findViewById(R.id.joystick_L);
        joystick_R = (RelativeLayout) findViewById(R.id.joystick_R);

        js_L = new JoyStickClass(getApplicationContext(), joystick_L, R.drawable.image_button,true);
        js_R = new JoyStickClass(getApplicationContext(), joystick_R, R.drawable.image_button,true);

        js_L.setStickSize(150, 150);
        js_L.setLayoutSize(400, 400);
        js_L.setLayoutAlpha(150);
        js_L.setStickAlpha(100);
        js_L.setOffset(0);
        js_L.setMinimumDistance(0);

        js_R.setStickSize(150, 150);
        js_R.setLayoutSize(400, 400);
        js_R.setLayoutAlpha(150);
        js_R.setStickAlpha(100);
        js_R.setOffset(0);
        js_R.setMinimumDistance(0);


        joystick_L.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js_L.drawStick(arg1);
                if (js_L.get_touch_state()) {
                    int value_x = map_value(-200, 200, 0, 180, js_L.getX());
                    int value_y = map_value(-200, 200, 0, 180, js_L.getY());

                    txt_x_L.setText("X : " + String.valueOf(value_x));
                    txt_y_L.setText("Y : " + String.valueOf(value_y));
                    try {
                        bt_socket.getOutputStream().write(("gun_l_pos:"+String.valueOf(value_x)+","+String.valueOf(180-value_y)+"\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        joystick_R.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js_R.drawStick(arg1);
                if (js_R.get_touch_state()) {
                    int value_x = map_value(-200, 200, 0, 180, js_R.getX());
                    int value_y = map_value(-200, 200, 0, 180, js_R.getY());

                    txt_x_R.setText("X : " + String.valueOf(value_x));
                    txt_y_R.setText("Y : " + String.valueOf(value_y));
                    try {
                        bt_socket.getOutputStream().write(("gun_r_pos:"+String.valueOf(value_x)+","+String.valueOf(180-value_y)+"\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

    }

    private int map_value(int min1, int max1, int min2, int max2, int x) {
        return (((max2 - min2) * (x - min1)) / (max1 - min1)) + min2;
    }
}