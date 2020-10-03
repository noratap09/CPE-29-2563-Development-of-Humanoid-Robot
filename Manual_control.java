package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Manual_control extends AppCompatActivity {
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;

    BluetoothSocket bt_socket;

    JoyStickClass js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        bt_socket = MainActivity.bt_socket;

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);

        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        try {
                            bt_socket.getOutputStream().write("f".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Up");
                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        try {
                            bt_socket.getOutputStream().write("f".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Up Right");
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        try {
                            bt_socket.getOutputStream().write("r".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Right");
                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        try {
                            bt_socket.getOutputStream().write("b".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Down Right");
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        try {
                            bt_socket.getOutputStream().write("b".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Down");
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        try {
                            bt_socket.getOutputStream().write("b".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Down Left");
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        try {
                            bt_socket.getOutputStream().write("l".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Left");
                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        try {
                            bt_socket.getOutputStream().write("f".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textView5.setText("Direction : Up Left");
                    } else if(direction == JoyStickClass.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                }
                return true;
            }
        });
    }
}