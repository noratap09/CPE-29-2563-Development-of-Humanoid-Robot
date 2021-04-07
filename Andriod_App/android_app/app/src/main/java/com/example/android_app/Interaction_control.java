package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import android.speech.RecognizerIntent;
import android.widget.Toast;

public class Interaction_control extends AppCompatActivity {
    TextView txt_stt;
    ImageView img_eye_L,img_eye_R,img_mount,img_eyebrow_L,img_eyebrow_R,img_shy_L,img_shy_R,img_ui_detail;

    private final int REQ_CODE = 100;

    AnimationDrawable anime_eye_L,anime_eye_R,anime_mount;
    MediaPlayer mediaPlayer;

    boolean ck_ideal = true;
    Thread Ideal_thread;
    Handler UI_handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_control);

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
                MainActivity.send_to_bt("gun_mode");
                Intent to_ar = new Intent(getApplicationContext(), AR_Marker_detect.class);
                startActivity(to_ar);
            }
        });

        img_eye_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.send_to_bt("gun_mode");
                Intent to_ar = new Intent(getApplicationContext(), AR_Marker_detect.class);
                startActivity(to_ar);
            }
        });

        //------------------Ideal Animation------------------------------------------
        Ideal_thread = new Thread(){
            @Override
            public void run() {
                while (true) {
                    if (ck_ideal) {
                        try {
                            Thread.sleep(10000);

                            //Update UI
                            UI_handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ck_ideal) {
                                        Random random = new Random();
                                        int index = random.nextInt(2) + 2;
                                        switch_face(index);
                                    }
                                }
                            });

                            Thread.sleep(1000);

                            //Update UI
                            UI_handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ck_ideal) {
                                        switch_face(0);
                                    }
                                }
                            });

                            System.out.println(ck_ideal);
                            System.out.println("IDEAL_WORKING!!!!");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        Ideal_thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ck_ideal = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //ของ Ideal
        //switch_face(0);
        //ck_ideal = false;
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

    private  void finsih_action(int time_delay)
    {
        img_eye_R.setEnabled(false);
        img_eye_R.setEnabled(false);
        img_mount.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch_face(0);

                img_eye_R.setEnabled(true);
                img_eye_R.setEnabled(true);
                img_mount.setEnabled(true);

                ck_ideal=true;
            }
        }, time_delay );//time in milisecond
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Pattern intent_hello = Pattern.compile("(สวัสดี(ครับ|ค่ะ|)|ฮัลโหล)");
        Pattern intent_name = Pattern.compile("((นาย|เธอ|)ชื่อ(ว่า|)อะไร(ครับ|เหรอ|)|(นาย|เธอ|แก|)(ช่วย|)แนะนำตัว(หน่อยสิ|ให้เราหน่อย|ให้ฟังหน่อย|)|(ไหน|)(ลอง|)แนะนำตัว(หน่อยสิ|ให้เราหน่อย|ให้ฟังหน่อย|)|(นาย|เธอ|แก|)เป็นใคร)");
        Pattern intent_attention = Pattern.compile("((นรเทพหมายเลข 7 4 4 |)แถวตรง|(นรเทพหมายเลข 7 4 4 |)ยืนตรง(สิ|)|(ไหน|)(ลอง|)ยืนตัวตรง(หน่อยสิ|ให้เราหน่อย|))");
        Pattern intent_sitdown = Pattern.compile("((นรเทพหมายเลข 7 4 4 |)(ย่อตัว|ย่อเข่า)(สิ|)|(ไหน|)(ลอง|)(ย่อตัว|ย่อเข่า)(หน่อยสิ|ให้เราหน่อย|))");
        Pattern intent_exercise = Pattern.compile("((นาย|เธอ|)ออกกำลังกาย(ได้ไหม|เป็นไหม|ให้ดูหน่อย|))");
        Pattern intent_thank = Pattern.compile("(เก่ง|ชอบ|น่ารัก|สวย|เท่ห์|เทพ|แม่น|ขอบคุณ|ขอบใจ)");
        Pattern intent_howto = Pattern.compile("((ช่วยแนะนำ|)วิธีใช้(งานเธอหน่อย|)|(เจ้าหุ่นแก|)ทำอะไร(ได้บ้าง|)|(ฉันจะ|)ใช้งาน(แกได้ยังไง|))");
        Pattern intent_howto_manual = Pattern.compile("((คู่มือใช้งานโหมด|)Manual|แมนนวล|Manual)");
        Pattern intent_howto_interactive = Pattern.compile("((คู่มือใช้งานโหมด|)Auto|interactions|อินเตอร์แอคชั่น|อินเตอร์แอคทีฟ|Auto)");
        Pattern intent_dance = Pattern.compile("((นาย|เธอ|)เต้น(ได้ไหม|เป็นไหม|ให้ดูหน่อย|)|(เปิด|)เพลง(ให้ฟังหน่อย|))");
        Pattern intent_bad_words = Pattern.compile("(กระจอก|กาก|ควย|เหี้ย|สัส|โง่|ปัญญาอ่อน)");
        Pattern intent_long_take = Pattern.compile("(นำเสนองาน)");

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    //ของ Ideal
                    switch_face(0);
                    ck_ideal = false;

                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txt_stt.setText(result.get(0).toString());
                    System.out.println("STT : "+result.get(0).toString());
                    if(intent_bad_words.matcher(result.get(0).toString()).find())
                    {
                        switch_face(1);
                        play_voice(R.raw.badword_1);

                        finsih_action(1500);
                    }
                    else if(intent_hello.matcher(result.get(0).toString()).find())
                    {
                        Random random = new Random();
                        int index = random.nextInt(2);
                        System.out.println(index);

                        if(index==0) {
                            switch_face(2);
                            MainActivity.send_to_bt("hello");
                            play_voice(R.raw.hello);
                        } else if( index==1) {
                            switch_face(2);
                            MainActivity.send_to_bt("hello");
                            play_voice(R.raw.hello2);
                        }

                        finsih_action(2000);
                    }
                    else if(intent_name.matcher(result.get(0).toString()).find())
                    {
                        Random random = new Random();
                        int index = random.nextInt(2);
                        System.out.println(index);

                        if(index==0) {
                            switch_face(0);
                            MainActivity.send_to_bt("talk:2");
                            play_voice(R.raw.name);

                            finsih_action(5000);
                         } else if( index==1) {
                            switch_face(2);
                            MainActivity.send_to_bt("talk:4");
                            play_voice(R.raw.greeting);

                            finsih_action(9000);
                        }
                    }
                    else if(intent_attention.matcher(result.get(0).toString()).find())
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(1);
                            MainActivity.send_to_bt("attention");
                            play_voice(R.raw.attention);
                        } else if (index == 1) {
                            switch_face(1);
                            MainActivity.send_to_bt("attention");
                            play_voice(R.raw.sir_yes_sir);
                        } else if (index == 2) {
                            switch_face(1);
                            MainActivity.send_to_bt("attention");
                            play_voice(R.raw.copy_that);
                        }

                        finsih_action(3000);
                    }
                    else if(intent_sitdown.matcher(result.get(0).toString()).find())
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(1);
                            MainActivity.send_to_bt("sitdown:1");
                            play_voice(R.raw.sitdown);
                        } else if (index == 1) {
                            switch_face(1);
                            MainActivity.send_to_bt("sitdown:1");
                            play_voice(R.raw.sir_yes_sir);
                        } else if (index == 2) {
                            switch_face(1);
                            MainActivity.send_to_bt("sitdown:1");
                            play_voice(R.raw.copy_that);
                        }

                        finsih_action(3000);
                    }
                    else if(intent_exercise.matcher(result.get(0).toString()).find())
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
                                    MainActivity.send_to_bt("sitdown:3");
                                }
                            }, 2000 );//time in milisecond

                        } else if (index == 1) {
                            switch_face(1);
                            play_voice(R.raw.exercise2);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.send_to_bt("swing_arm:3");
                                }
                            }, 3000 );//time in milisecond

                        } else if (index == 2) {
                            switch_face(1);
                            play_voice(R.raw.exercise3);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.send_to_bt("swing_body:3");
                                }
                            }, 3000 );//time in milisecond

                        }

                        finsih_action(8000);
                    }
                    else if(intent_thank.matcher(result.get(0).toString()).find())
                    {
                        Random random = new Random();
                        int index = random.nextInt(3);
                        System.out.println(index);

                        if(index == 0) {
                            switch_face(3);
                            MainActivity.send_to_bt("talk:2");
                            play_voice(R.raw.thank);
                        } else if (index == 1) {
                            switch_face(4);
                            MainActivity.send_to_bt("talk:2");
                            play_voice(R.raw.thank2);
                        } else if (index == 2) {
                            switch_face(3);
                            MainActivity.send_to_bt("talk:1");
                            play_voice(R.raw.thank3);
                        }

                        finsih_action(2000);
                    }
                    else if(intent_howto_manual.matcher(result.get(0).toString()).find())
                    {
                        switch_face(0);
                        MainActivity.send_to_bt("talk:4");
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

                        finsih_action(16000);
                    }
                    else if(intent_howto_interactive.matcher(result.get(0).toString()).find())
                    {
                        switch_face(0);
                        MainActivity.send_to_bt("talk:4");
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

                        finsih_action(19000);
                    }
                    else if(intent_howto.matcher(result.get(0).toString()).find())
                    {
                        switch_face(0);
                        MainActivity.send_to_bt("talk:4");
                        play_voice(R.raw.howto);

                        finsih_action(9000);
                    }
                    else if(intent_dance.matcher(result.get(0).toString()).find())
                    {
                        switch_face(2);
                        play_voice(R.raw.copy_that);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                play_voice(R.raw.bg_song);
                                MainActivity.send_to_bt("sitdown:10");
                            }
                        }, 1000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.send_to_bt("swing_body:10");
                            }
                        }, 15000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.send_to_bt("swing_arm:1");
                            }
                        }, 31500 );//time in milisecond

                        finsih_action(31500);
                    }
                    else if(intent_long_take.matcher(result.get(0).toString()).find())
                    {
                        //เริ่ม -> เดิน
                        play_voice(R.raw.long_take);
                        MainActivity.send_to_bt("hello");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.send_to_bt("reposition");
                            }
                        }, 31000 );//time in milisecond

                        //เเดิน -> เต้น
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                play_voice(R.raw.long_take2);
                                MainActivity.send_to_bt("interaction_mode");
                            }
                        }, 41000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.send_to_bt("swing_arm:1");
                            }
                        }, 48000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(4);
                            }
                        }, 69000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(1);
                            }
                        }, 70000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(3);
                            }
                        }, 71000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(2);
                            }
                        }, 73000 );//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                            }
                        }, 75000 );//time in milisecond

                        //เต้น
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(2);
                            }
                        }, 101000);//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                play_voice(R.raw.bg_song);
                                MainActivity.send_to_bt("sitdown:10");
                            }
                        }, 101000+1000);//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.send_to_bt("swing_body:10");
                            }
                        }, 101000+15000);//time in milisecond

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.send_to_bt("swing_arm:1");
                            }
                        }, 101000+31500);//time in milisecond

                        //เเต้น -> จบ
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_face(0);
                                play_voice(R.raw.long_take3);
                            }
                        }, 135000);//time in milisecond
                    }
                    else
                    {
                        play_voice(R.raw.not_understand);
                        finsih_action(1500);
                    }
                }
                break;
            }
        }
    }

}