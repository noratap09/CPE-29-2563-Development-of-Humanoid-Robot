package com.example.android_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SubtitleData;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

public class Interaction_control extends AppCompatActivity {
    TextView txt_stt;
    ImageView img_eye_L,img_eye_R,img_mount,img_eyebrow_L,img_eyebrow_R,img_shy_L,img_shy_R,img_ui_detail;

    BluetoothSocket bt_socket;
    private final int REQ_CODE = 100;

    AnimationDrawable anime_eye_L,anime_eye_R,anime_mount;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_control);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bt_socket = MainActivity.bt_socket;

        txt_stt = (TextView)findViewById(R.id.txt_stt);
        img_eye_L = (ImageView)findViewById(R.id.img_eye_L);
        img_eye_R = (ImageView)findViewById(R.id.img_eye_R);
        img_mount = (ImageView) findViewById(R.id.img_mount);
        img_eyebrow_L = (ImageView) findViewById(R.id.img_eyebrow_L);
        img_eyebrow_R = (ImageView) findViewById(R.id.img_eyebrow_R);
        img_shy_L = (ImageView) findViewById(R.id.img_shy_L);
        img_shy_R = (ImageView) findViewById(R.id.img_shy_R);
        img_ui_detail = (ImageView) findViewById(R.id.img_ui_detail);

        img_mount.setImageResource(R.drawable.animation_mount);

        anime_mount = (AnimationDrawable) img_mount.getDrawable();

        switch_face(0);
        /*
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://app/res/raw/hello"));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */



        img_mount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null)
                {
                    try {
                        switch_face(0);
                        anime_mount.setOneShot(true);

                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer=null;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

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

        img_eye_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bt_socket.getOutputStream().write("gun_mode\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent to_ar = new Intent(getApplicationContext(), AR_Marker_detect.class);
                startActivity(to_ar);
            }
        });

        img_eye_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bt_socket.getOutputStream().write("gun_mode\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent to_ar = new Intent(getApplicationContext(), AR_Marker_detect.class);
                startActivity(to_ar);
            }
        });

        /*
        Runnable runnable = new Runnable() {
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
                            try {
                                bt_socket.getOutputStream().write("walk_mode\n".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

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
        new Thread(runnable).start();
         */

    }

    private  void  switch_face(int face_id)
    {
        int color_blue = Color.argb(255, 2, 209, 168);
        int color_red = Color.argb(255, 255, 54, 0);
        int color_pink = Color.argb(255, 255, 0, 180);
        if(face_id == 0)
        {
            img_eye_L.setImageResource(R.drawable.animation_eye);
            img_eye_R.setImageResource(R.drawable.animation_eye);

            img_shy_L.setVisibility(View.INVISIBLE);
            img_shy_R.setVisibility(View.INVISIBLE);
            img_eyebrow_L.setVisibility(View.INVISIBLE);
            img_eyebrow_R.setVisibility(View.INVISIBLE);

            anime_eye_L = (AnimationDrawable) img_eye_L.getDrawable();
            anime_eye_R = (AnimationDrawable) img_eye_R.getDrawable();

            anime_eye_L.start();
            anime_eye_R.start();

            img_eye_L.setColorFilter(color_blue);
            img_eye_R.setColorFilter(color_blue);
            img_mount.setColorFilter(color_blue);
        }
        else if(face_id == 1)
        {
            img_eye_L.setImageResource(R.drawable.animation_eye);
            img_eye_R.setImageResource(R.drawable.animation_eye);
            img_eyebrow_L.setImageResource(R.drawable.eyebrown_l);
            img_eyebrow_R.setImageResource(R.drawable.eyebrown_r);

            img_shy_L.setVisibility(View.INVISIBLE);
            img_shy_R.setVisibility(View.INVISIBLE);
            img_eyebrow_L.setVisibility(View.VISIBLE);
            img_eyebrow_R.setVisibility(View.VISIBLE);

            anime_eye_L = (AnimationDrawable) img_eye_L.getDrawable();
            anime_eye_R = (AnimationDrawable) img_eye_R.getDrawable();

            anime_eye_L.stop();
            anime_eye_R.stop();

            img_eye_L.setColorFilter(color_red);
            img_eye_R.setColorFilter(color_red);
            img_mount.setColorFilter(color_red);
        }
        else if(face_id == 2)
        {
            anime_eye_L.stop();
            anime_eye_R.stop();

            img_eye_L.setImageResource(R.drawable.eye_smile);
            img_eye_R.setImageResource(R.drawable.eye_smile);

            img_shy_L.setVisibility(View.INVISIBLE);
            img_shy_R.setVisibility(View.INVISIBLE);
            img_eyebrow_L.setVisibility(View.INVISIBLE);
            img_eyebrow_R.setVisibility(View.INVISIBLE);

            img_eye_L.setColorFilter(color_blue);
            img_eye_R.setColorFilter(color_blue);
            img_mount.setColorFilter(color_blue);
        }
        else if(face_id == 3)
        {
            img_eye_L.setImageResource(R.drawable.animation_eye);
            img_eye_R.setImageResource(R.drawable.animation_eye);
            img_shy_L.setImageResource(R.drawable.shy_sigh);
            img_shy_R.setImageResource(R.drawable.shy_sigh);

            img_shy_L.setVisibility(View.VISIBLE);
            img_shy_R.setVisibility(View.VISIBLE);
            img_eyebrow_L.setVisibility(View.INVISIBLE);
            img_eyebrow_R.setVisibility(View.INVISIBLE);

            anime_eye_L = (AnimationDrawable) img_eye_L.getDrawable();
            anime_eye_R = (AnimationDrawable) img_eye_R.getDrawable();

            anime_eye_L.start();
            anime_eye_R.start();

            img_eye_L.setColorFilter(color_blue);
            img_eye_R.setColorFilter(color_blue);
            img_mount.setColorFilter(color_blue);
        }
        else if(face_id == 4)
        {
            anime_eye_L.stop();
            anime_eye_R.stop();

            img_eye_L.setImageResource(R.drawable.eye_love);
            img_eye_R.setImageResource(R.drawable.eye_love);

            img_shy_L.setVisibility(View.INVISIBLE);
            img_shy_R.setVisibility(View.INVISIBLE);
            img_eyebrow_L.setVisibility(View.INVISIBLE);
            img_eyebrow_R.setVisibility(View.INVISIBLE);

            img_eye_L.setColorFilter(color_pink);
            img_eye_R.setColorFilter(color_pink);
            img_mount.setColorFilter(color_pink);
        }
    }

    private void play_voice(int voice_data)
    {
        try {
            anime_mount.setOneShot(false);
            anime_mount.run();

            mediaPlayer = MediaPlayer.create(this, voice_data);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    anime_mount.setOneShot(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            switch_face(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] intent_hello = {"สวัสดี"};
        String[] intent_name = {"ชื่ออะไร"};
        String[] intent_attention = {"แถวตรง"};
        String[] intent_sitdown = {"นั่งลง"};
        String[] intent_exercise = {"ออกกำลังกาย"};
        String[] intent_thank = {"คำชม"};
        String[] intent_greeting = {"แนะนำตัว"};
        String[] intent_howto = {"คู่มือใช้งาน"};
        String[] intent_howto_manual = {"คู่มือใช้งานโหมด Manual","คู่มือใช้งานโหมดแมนนวล"};
        String[] intent_howto_interactive = {"คู่มือใช้งานโหมด Auto"};

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txt_stt.setText(result.get(0).toString());
                    System.out.println("STT : "+result.get(0).toString());

                    if(Arrays.asList(intent_hello).contains(result.get(0).toString()))
                    {
                        Random random = new Random();
                        int index = random.nextInt(2);
                        System.out.println(index);

                        if(index==0) {
                            switch_face(2);
                            send_to_bt("hello");
                            play_voice(R.raw.hello);
                        } else if( index==1) {
                            switch_face(2);
                            send_to_bt("hello");
                            play_voice(R.raw.hello2);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 2000 );//time in milisecond
                    }
                    else if(Arrays.asList(intent_name).contains(result.get(0).toString()))
                    {
                        switch_face(0);
                        send_to_bt("talk:2");
                        play_voice(R.raw.name);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 5000 );//time in milisecond
                    }
                    else if(Arrays.asList(intent_attention).contains(result.get(0).toString()))
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(1);
                            send_to_bt("attention");
                            play_voice(R.raw.attention);
                        } else if (index == 1) {
                            switch_face(1);
                            send_to_bt("attention");
                            play_voice(R.raw.sir_yes_sir);
                        } else if (index == 2) {
                            switch_face(1);
                            send_to_bt("attention");
                            play_voice(R.raw.copy_that);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 3000 );//time in milisecond
                    }
                    else if(Arrays.asList(intent_sitdown).contains(result.get(0).toString()))
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(1);
                            send_to_bt("sitdown:1");
                            play_voice(R.raw.sitdown);
                        } else if (index == 1) {
                            switch_face(1);
                            send_to_bt("sitdown:1");
                            play_voice(R.raw.sir_yes_sir);
                        } else if (index == 2) {
                            switch_face(1);
                            send_to_bt("sitdown:1");
                            play_voice(R.raw.copy_that);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 3000 );//time in milisecond
                    }
                    else if(Arrays.asList(intent_exercise).contains(result.get(0).toString()))
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(1);
                            play_voice(R.raw.exercise);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    send_to_bt("sitdown:3");
                                }
                            }, 2000 );//time in milisecond

                        } else if (index == 1) {
                            switch_face(1);
                            play_voice(R.raw.exercise2);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    send_to_bt("swing_arm:3");
                                }
                            }, 3000 );//time in milisecond

                        } else if (index == 2) {
                            switch_face(1);
                            play_voice(R.raw.exercise3);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    send_to_bt("swing_body:3");
                                }
                            }, 3000 );//time in milisecond

                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 8000 );//time in milisecond

                    }
                    else if(Arrays.asList(intent_thank).contains(result.get(0).toString()))
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(3);
                            send_to_bt("talk:2");
                            play_voice(R.raw.thank);
                        } else if (index == 1) {
                            switch_face(4);
                            send_to_bt("talk:2");
                            play_voice(R.raw.thank2);
                        } else if (index == 2) {
                            switch_face(3);
                            send_to_bt("talk:1");
                            play_voice(R.raw.thank3);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 2000 );//time in milisecond
                    }
                    else if(Arrays.asList(intent_greeting).contains(result.get(0).toString()))
                    {
                        switch_face(2);
                        send_to_bt("talk:4");
                        play_voice(R.raw.greeting);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 9000 );//time in milisecond
                    }
                    else if(Arrays.asList(intent_howto).contains(result.get(0).toString()))
                    {
                        switch_face(0);
                        send_to_bt("talk:4");
                        play_voice(R.raw.howto);
                    }
                    else if(Arrays.asList(intent_howto_manual).contains(result.get(0).toString()))
                    {
                        switch_face(0);
                        send_to_bt("talk:4");
                        play_voice(R.raw.howto_manual);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                img_ui_detail.setImageResource(R.drawable.manual_detail);
                                img_ui_detail.setVisibility(View.VISIBLE);
                            }
                        }, 8000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                img_ui_detail.setImageResource(R.drawable.shooting_detail);
                            }
                        }, 12000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                img_ui_detail.setVisibility(View.INVISIBLE);
                            }
                        }, 16000 );//time in milisecond


                    }
                    else if(Arrays.asList(intent_howto_interactive).contains(result.get(0).toString()))
                    {
                        switch_face(0);
                        send_to_bt("talk:4");
                        play_voice(R.raw.howto_interactive);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                img_ui_detail.setImageResource(R.drawable.ar_detail);
                                img_ui_detail.setVisibility(View.VISIBLE);
                            }
                        }, 15000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                img_ui_detail.setVisibility(View.INVISIBLE);
                            }
                        }, 19000 );//time in milisecond
                    }
                }
                break;
            }
        }
    }

    void send_to_bt(String input)
    {
        try {
            bt_socket.getOutputStream().write((input+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}