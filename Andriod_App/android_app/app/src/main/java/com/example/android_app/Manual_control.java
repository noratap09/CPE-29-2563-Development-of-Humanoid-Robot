package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Manual_control extends AppCompatActivity {
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;
    Button btn_shoting_mode,btn_ls,btn_rs;

    JoyStickClass js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        btn_shoting_mode = (Button)findViewById(R.id.btn_shoting_mode);
        btn_ls = (Button)findViewById(R.id.btn_ls);
        btn_rs = (Button)findViewById(R.id.btn_rs);

        btn_shoting_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.send_to_bt("gun_mode");
                System.out.println("Mode M_shoting");
                Intent to_manual_shoting = new Intent(getApplicationContext(), Manual_shoting.class);
                startActivity(to_manual_shoting);
            }
        });

        btn_ls.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.send_to_bt("ls");
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("Slide Left");
                    textView5.setText("Direction : Slide Left");
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    textView5.setText("Direction : ");
                }
                return false;
            }
        });

        btn_rs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.send_to_bt("rs");
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("Slide Right");
                    textView5.setText("Direction : Slide Right");
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    textView5.setText("Direction : ");
                }
                return false;
            }
        });

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button,false);

        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(255);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(js.get_touch_state()) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get4Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        MainActivity.send_to_bt("fd");
                        System.out.println("UP");
                        textView5.setText("Direction : Up");
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        MainActivity.send_to_bt("rd");
                        System.out.println("Right");
                        textView5.setText("Direction : Right");
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        MainActivity.send_to_bt("bd");
                        System.out.println("Down");
                        textView5.setText("Direction : Down");
                    }  else if(direction == JoyStickClass.STICK_LEFT) {
                        MainActivity.send_to_bt("ld");
                        System.out.println("Left");
                        textView5.setText("Direction : Left");
                    }  else if(direction == JoyStickClass.STICK_NONE) {
                        System.out.println("Center");
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