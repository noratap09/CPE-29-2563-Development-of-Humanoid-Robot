#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

//BlueTooth hc-05
#include <SoftwareSerial.h>
//BlueTooth hc-05
SoftwareSerial Blue_th(10, 11); // RX, TX

// called this way, it uses the default address 0x40
Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();
// you can also call it with a different address you want
//Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver(0x41);

// Depending on your servo make, the pulse width min and max may vary, you
// want these to be as small/large as possible without hitting the hard stop
// for max range. You'll have to tweak them as necessary to match the servos you
// have!
#define SERVOMIN  150 // this is the 'minimum' pulse length count (out of 4096)
#define SERVOMAX  600 // this is the 'maximum' pulse length count (out of 4096)

const int sw_pin = 5;
const int dc_motor_pin_A1 = 6;
const int dc_motor_pin_A2 = 7;
const int dc_motor_pin_B1 = 8;
const int dc_motor_pin_B2 = 9;
const int laser_L_pin_vcc = A0;
const int laser_L_pin_gnd = A1;
const int laser_R_pin_vcc = A2;
const int laser_R_pin_gnd = A3;


char mode = 'M';

void setup() {
  pinMode(sw_pin,INPUT);
  digitalWrite(sw_pin,HIGH); //Pull Up Switch

  pinMode(dc_motor_pin_A1,OUTPUT);
  pinMode(dc_motor_pin_A2,OUTPUT);
  pinMode(dc_motor_pin_B1,OUTPUT);
  pinMode(dc_motor_pin_B2,OUTPUT);

  pinMode(laser_L_pin_gnd,OUTPUT);// Pull out to GND
  digitalWrite(laser_L_pin_gnd,LOW); //Make it GND

  pinMode(laser_R_pin_gnd,OUTPUT);// Pull out to GND
  digitalWrite(laser_R_pin_gnd,LOW); //Make it GND

  pinMode(laser_L_pin_vcc,OUTPUT);
  pinMode(laser_R_pin_vcc,OUTPUT);
  
  Serial.begin(9600);
  Serial.println("Robot Start!!");
  pwm.begin();
  pwm.setPWMFreq(60);  // Analog servos run at ~60 Hz updates

  Blue_th.begin(9600);

  for (int i = 0 ; i < 15 ; i++)
  {
    //moveDegree(i, 90);
  }

  //walk test
  //walk(5,0.03,0.00,0.015,5.00,0.00,10);
  //walk(5,0.00,-30.00,0.014,5.00,0.00,5);
  //walk(10,-0.012,0);

  randomSeed(analogRead(0)); 
  center_pos();
  arm_walk();
  delay(1000);
  //walk(5,0.03,0.00,0.00,0.015,4.00,0.00,5,"fd",true);
  //walk(5,-0.03,0.00,0.00,0.015,4.00,0.00,5,"bd",true);
  //walk(5,0.00,30.00,0.00,0.015,4.00,0.00,5,"rd",true);
  //walk(5,0.00,-30.00,0.00,0.015,4.00,0.00,5,"ld",true);
  //walk(5,0.00,0.00,-0.03,0.015,4.00,0.00,5,"ls",true);
  //walk(5,0.00,0.00,0.03,0.015,4.00,0.00,5,"rs",true);

  //walk2();

  //Turn R
  //turn_R();

  //Turn L
  //turn_L();
  
  //walk(10,0.03,-10.00,0.015,5.00,0.00,10,'f');
}

void loop() {
    //Check Switch 
    
    int sw_value = digitalRead(sw_pin);
    //Serial.println(mode);

    if(mode=='M' && sw_value == 1)
    { 
      Serial.println("To_A");
      Blue_th.write("A"); 
      delay(100);
    }
    else if(mode=='A' && sw_value == 0)
    { 
      Serial.println("To_M");
      Blue_th.write("M"); 
      delay(100);
    }

    if(sw_value){ mode = 'A'; }
    else{ mode = 'M'; }
  
    

    //Check Bluetooth 
    if (Blue_th.available())
    {
      String out_char = Blue_th.readStringUntil('\n');
      Blue_th_Flush(); //Clear Buffer
      Serial.println(out_char);
      if(out_char=="fd") // Font
      {
        walk(50,0.03,0.00,0.00,0.015,6.00,0.00,5,"fd",false);
      }
      else if(out_char=="rd") //Right
      {
        //Trun_R(30.00,0.015,5.00,0.00,10,'r'); ยังไม่ OK
        walk(50,0.00,30.00,0.00,0.015,6.00,0.00,5,"rd",false);
      }
      else if(out_char=="ld") //Left
      {
        //Trun_L(-30.00,0.015,5.00,0.00,10,'l'); ยังไม่ OK
        walk(50,0.00,-30.00,0.00,0.015,6.00,0.00,5,"ld",false);
      }
      else if(out_char=="bd") //Back
      {
        walk(50,-0.03,0.00,0.00,0.015,6.00,0.00,5,"bd",false);
      }
      else if(out_char=="ls") //slide left
      {
        walk(50,0.00,0.00,0.03,0.015,6.00,0.00,5,"ls",false);
      }
      else if(out_char=="rs") //slide right
      {
        walk(50,0.00,0.00,-0.03,0.015,6.00,0.00,5,"rs",false);
      }
      else if(out_char=="hello") //Pos Hello
      {
        pos_hello();
      }
      else if(out_char=="attention") //Pos attention
      {
        pos_attention();
      }
      else if(out_char.indexOf("talk")>=0) //talking motion
      {
        int num_talk = out_char.substring(out_char.indexOf(":")+1).toInt();
        pos_talk(num_talk);
      }
      else if(out_char.indexOf("sitdown")>=0) //sitdown motion
      {
        int num = out_char.substring(out_char.indexOf(":")+1).toInt();
        pos_sitdown(num);
      }
      else if(out_char.indexOf("swing_arm")>=0) //swing_arm motion
      {
        int num = out_char.substring(out_char.indexOf(":")+1).toInt();
        pos_swing_arm(num);
      }
      else if(out_char.indexOf("swing_body")>=0) //swing_body motion
      {
        int num = out_char.substring(out_char.indexOf(":")+1).toInt();
        pos_swing_body(num);
      }
      else if(out_char.indexOf("gun_l_pos")>=0) // gun_l_pos
      {
        String pos_x = out_char.substring(out_char.indexOf(":")+1,out_char.indexOf(","));
        String pos_y = out_char.substring(out_char.indexOf(",")+1);
        gun_l_set_pos(pos_x.toInt(),pos_y.toInt());
      }
      else if(out_char.indexOf("gun_r_pos")>=0) // gun_r_pos
      {
        String pos_x = out_char.substring(out_char.indexOf(":")+1,out_char.indexOf(","));
        String pos_y = out_char.substring(out_char.indexOf(",")+1);
        gun_r_set_pos(pos_x.toInt(),pos_y.toInt());
      }
      else if(out_char=="gun_l_shot") // gun_l_shot
      {
        dc_motor_L(1);
        delay(500);
        dc_motor_L(0);
      }
      else if(out_char=="gun_l_reload") // gun_l_reload
      {
        dc_motor_L(-1);
        delay(500);
        dc_motor_L(0);
      }
      else if(out_char=="gun_r_shot") // gun_r_shot
      {
        dc_motor_R(1);
        delay(500);
        dc_motor_R(0);
      }
      else if(out_char=="gun_r_reload") // gun_r_reload
      {
        dc_motor_R(-1);
        delay(500);
        dc_motor_R(0);
      }
      else if(out_char=="gun_mode") // gun_mode
      {
        laser_L(1);
        laser_R(1);
      }
      else if(out_char=="walk_mode" or out_char=="interaction_mode") // walk_mode
      {
        laser_L(0);
        laser_R(0);
        arm_walk();
      }
      //Blue_th_Flush(); //Clear Buffer
    }

  if (Serial.available())
  {
      Blue_th.write(Serial.read());
  }
}

void Blue_th_Flush(){
  while(Blue_th.available() > 0) {
    char t = Blue_th.read();
  }
}  

void center_pos()
{
  IK_L(0.00, 0.01, 0.1152, 0, 0, 0, 0, 95);
  IK_R(0.00, 0.01, 0.1152, 0, 0, 0, 0, 120);
}

float mapfloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  //float val = smooth(in_min,in_max,x,in_max);
  return (float)(x - in_min) * (out_max - out_min) / (float)(in_max - in_min) + out_min;
}

float constrainfloat(float x, float x_min, float x_max)
{
  if(x <= x_max && x >= x_min){return x; }
  else{ return 0.00; }
}

float smooth(float x0 , float xf , float t , float tf)
{
  float c = (xf - x0) / tf;
  float w = 2 * 3.14 / tf;
  return (c * t - (c / w) * sin(w * t) + x0);
}

float bezier(float t)
{
  float P0 = 0;
  float P1 = 0.64;
  float P2 = 0.64;
  float P3 = 0;
  return 0.1 * ( (pow((1 - t), 3) * P0) + (3 * pow((1 - t), 2) * t * P1) + (3 * (1 - t) * pow(t, 2) * P2) + (pow(t, 3) * P3) );
}

void walk2()
{
    int n_sample = 20;
    int t_delay = 10;
    int t_delay_by_pos = 10;
    float h_com = 0.1252;
    float walk_len = 0.03;
    float walk_deg = 0.00;
    
    float body_shift = 0.015;
    float bia_body_shift = 10.00;
    float bia_body_shift_2 = 0.00;
    float bia_hit = 0.000;
    

    //หมายถึงจังหวะที่ COM อยู่ตรงขาข้างนั้นๆ
    float body_shift_L = body_shift+0.007;
    float body_shift_R = body_shift+0.007; 
    
    float bia_body_shift_L = bia_body_shift+0.00;
    float bia_body_shift_R = bia_body_shift+0.00;
    float bia_body_shift_R_2 = -bia_body_shift_2;

    float bia_hit_L = bia_hit+0.00;
    float bia_hit_R = bia_hit+0.00;

    //#I0 เริ่มต้น
    IK_L(0.00, 0.00, h_com , 0, 0, 0, 0, 95);
    IK_R(0.00, 0.00, h_com , 0, 0, 0, 0, 120);
    delay(3000);

    //#I1 เถ COM ไปขา R
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L(0.00, mapfloat(i, 1, n_sample, 0.00, -body_shift_R) , h_com , 0, 0, 0, 0, 95);
      IK_R(0.00, mapfloat(i, 1, n_sample, 0.00, -body_shift_R) , h_com , 0, 0, 0, 0, 120);
      //Serial.println(mapfloat(i,1,n_sample,0.00,-0.02),10);
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

    //#P0 ยกขา L
    for (int i = 1; i <= n_sample; i++)
    {
      //IK_L(0.00, -0.02 , mapfloat(i,1,n_sample,0.081,0.041) ,0,0,0,0);
      IK_L(0.00, -body_shift_R , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 95);
      IK_R(0.00, -body_shift_R , h_com , mapfloat(i, 1, n_sample, 0.00, bia_body_shift_R), 0, 0, -bia_hit_R , 120);
      //Serial.println((0.081 - bezier(mapfloat(i,1,n_sample,0.00,0.50))),10);
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

    for(int i = 1;i<=10;i++)
{
      //#P1 วางขา L
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( mapfloat(i, 1, n_sample, 0.00, walk_len) , mapfloat(i, 1, n_sample,-body_shift_R,0.00) , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 90 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg),0.00,walk_deg) );
          IK_R(0.00, mapfloat(i, 1, n_sample,-body_shift_R,0.00) , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R,0.00), 0, 0, -bia_hit_R , 120);
          delay(t_delay);
      }
      delay(t_delay_by_pos);

      //#P2 เถ COM ไปขา L
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( mapfloat(i, 1, n_sample, walk_len, 0.00) , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L) , h_com , 0, 0, 0, 0, 90  + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg) );
          IK_R( mapfloat(i, 1, n_sample, 0.00, -walk_len) , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L) , h_com , 0, 0, 0, 0, 120 -  constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,0.00,walk_deg) );
          delay(t_delay);
      }
      delay(t_delay_by_pos);

      //#P3 ยกขา R
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( 0.00 , body_shift_L , h_com , mapfloat(i, 1, n_sample, 0.00, -bia_body_shift_L) , 0, 0, bia_hit_L , 95);
          IK_R( mapfloat(i, 1, n_sample, -walk_len, 0.00) , body_shift_L , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 120 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg));
          delay(t_delay);
      }
    delay(t_delay_by_pos);

      //#P4 วางขา R
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L(0.00, mapfloat(i, 1, n_sample,body_shift_L,0.00) , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L,0.00), 0, 0, bia_hit_L , 95);
          //IK_R( mapfloat(i,1,n_sample,0.00,walk_len) , 0.02 , mapfloat(i,1,n_sample,0.041,0.081) ,0,0,0,0);
          IK_R( mapfloat(i, 1, n_sample, 0.00, walk_len) , mapfloat(i, 1, n_sample,body_shift_L,0.00) , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 120 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,walk_deg,0.00));
          delay(t_delay);
      }
      delay(t_delay_by_pos);

    //#P5 เถ COM ไปขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( mapfloat(i, 1, n_sample, 0.00, -walk_len) , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R) , h_com ,0.00, 0, 0, 0, 95 - constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,walk_deg,0.00));
        IK_R( mapfloat(i, 1, n_sample, walk_len, 0.00) , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R) , h_com , 0, 0, 0, 0, 120 + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,walk_deg,0.00));
        delay(t_delay);
    }
    delay(t_delay_by_pos);

     //#P6 ยกขา L
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( mapfloat(i, 1, n_sample, -walk_len, 0.00) , -body_shift_R , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 95 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,walk_deg,0.00));
        IK_R( 0.00 , -body_shift_R , h_com , mapfloat(i, 1, n_sample, 0.00, bia_body_shift_R), 0, 0, -bia_hit_R , 120);
        delay(t_delay); 
    }
    delay(t_delay_by_pos);
}

    //#F0 วางขา L
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L( 0.00 , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
      IK_R(0.00, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, -bia_hit_R, 120);
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

}

void arm_walk()
{
  moveDegree(5, 0);//shoulder_Y L 
  moveDegree(6, 120);//shoulder_X L  90 ตรง
  moveDegree(7, 90);//arm L   

  moveDegree(10, 180);//shoulder_Y R 
  moveDegree(9, 60);//shoulder_X R 90 ตรง
  moveDegree(8, 90);//arm R
}

void pos_hello()
{
    int n_sample = 20;
    int t_delay = 10;
    int t_delay_by_pos = 1000;
    //ยกมือ
    for (int i = 1; i <= n_sample; i++)
    {
      moveDegree(5, map(i,1,n_sample,0,90));//shoulder_Y L 
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);
    
    //มือลง
    for (int i = 1; i <= n_sample; i++)
    {
      moveDegree(5, map(i,1,n_sample,90,0));//shoulder_Y L 
      delay(t_delay*0.5);
    }
}

void pos_attention()
{
    int n_sample = 20;
    int t_delay = 10;
    int t_delay_by_pos = 3000;
    //ยกมือ
    for (int i = 1; i <= n_sample; i++)
    {
      moveDegree(6, map(i,1,n_sample,120,90));//shoulder_X L  90 ตรง
      moveDegree(9, map(i,1,n_sample,60,90));//shoulder_X R 90 ตรง
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

    for (int i = 1; i <= n_sample; i++)
    {
      moveDegree(6, map(i,1,n_sample,90,120));//shoulder_X L  90 ตรง
      moveDegree(9, map(i,1,n_sample,90,60));//shoulder_X R 90 ตรง
      delay(t_delay*0.5);
    } 
}

void pos_sitdown(int num)
{
    int n_sample = 20;
    int t_delay = 20;
    int t_delay_by_pos = 100;
    for(int k=0;k<num;k++)
    {
      //นั่งลง
      for (int i = 1; i <= n_sample; i++)
      {
        IK_L(0.00, 0.01, (0.1152 - 0.5*bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 95);
        IK_R(0.00, 0.01, (0.1152 - 0.5*bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))), 0, 0, 0, 0, 120);
        delay(t_delay);
      }
      delay(t_delay_by_pos);

      //ยื่นขึ้น
      for (int i = 1; i <= n_sample; i++)
      {
        IK_L(0.00, 0.01, (0.1152 - 0.5*bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
        IK_R(0.00, 0.01, (0.1152 - 0.5*bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))), 0, 0, 0, 0, 120);
        delay(t_delay);
      } 
    }
}

void pos_swing_arm(int num)
{
    int n_sample = 30;
    int t_delay = 20;
    int t_delay_by_pos = 100;
    for(int k=0;k<num;k++)
    {
      //ยกมือ
      for (int i = 1; i <= n_sample; i++)
      {
        moveDegree(5, map(i,1,n_sample,0,130));//shoulder_Y L 
        moveDegree(10, map(i,1,n_sample,180,50));//shoulder_Y R 
        delay(t_delay);
      }
      delay(t_delay_by_pos);

      //มือลง
      for (int i = 1; i <= n_sample; i++)
      {
        moveDegree(5, map(i,1,n_sample,130,0));//shoulder_Y L
        moveDegree(10, map(i,1,n_sample,50,180));//shoulder_Y R
        delay(t_delay);
      } 
    }
}

void pos_swing_body(int num)
{
    if(num<=0) return; //ภ้า num <= 0 ออก funtion
  
    int n_sample = 20;
    int t_delay = 20;
    int t_delay_by_pos = 100;

    //กลาง->ขวา
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L(0.00, mapfloat(i, 1, n_sample, 0.01, -0.015) , 0.1152, 0, 0, 0, 0, 95);
      IK_R(0.00, mapfloat(i, 1, n_sample, 0.01, -0.015), 0.1152, 0, 0, 0, 0, 120);

      moveDegree(6, 120);//shoulder_X L  90 ตรง
      moveDegree(9, map(i,1,n_sample,60,10));//shoulder_X R 90 ตรง
      delay(t_delay);
    }
    delay(t_delay_by_pos);
    
    for(int k=0;k<num;k++)
    {
      //ขวา->ซ้าย
      for (int i = 1; i <= n_sample; i++)
      {
        IK_L(0.00, mapfloat(i, 1, n_sample, -0.015, 0.015) , 0.1152, 0, 0, 0, 0, 95);
        IK_R(0.00, mapfloat(i, 1, n_sample, -0.015, 0.015), 0.1152, 0, 0, 0, 0, 120);

        moveDegree(6, map(i,1,n_sample,120,170));//shoulder_X L  90 ตรง
        moveDegree(9, map(i,1,n_sample,10,60));//shoulder_X R 90 ตรง
        delay(t_delay);
      }
      delay(t_delay_by_pos);

      //ซ้าย->ขวา
      for (int i = 1; i <= n_sample; i++)
      {
        IK_L(0.00, mapfloat(i, 1, n_sample, 0.015, -0.015) , 0.1152, 0, 0, 0, 0, 95);
        IK_R(0.00, mapfloat(i, 1, n_sample, 0.015, -0.015), 0.1152, 0, 0, 0, 0, 120);

        moveDegree(6, map(i,1,n_sample,170,120));//shoulder_X L  90 ตรง
        moveDegree(9, map(i,1,n_sample,60,10));//shoulder_X R 90 ตรง
        delay(t_delay);
      } 
      delay(t_delay_by_pos);
    }
    //ซ้าย->กลาง
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L(0.00, mapfloat(i, 1, n_sample, -0.015, 0.01) , 0.1152, 0, 0, 0, 0, 95);
      IK_R(0.00, mapfloat(i, 1, n_sample, -0.015, 0.01), 0.1152, 0, 0, 0, 0, 120);

      moveDegree(6, 120);//shoulder_X L  90 ตรง
      moveDegree(9, map(i,1,n_sample,10,60));//shoulder_X R 90 ตรง
      delay(t_delay);
    }
    delay(t_delay_by_pos);
}

void pos_talk(int num_talk)
{
    int n_sample = 20;
    int t_delay = 10;
    int t_delay_by_pos = 1000;
    for(int num=0;num<num_talk;num++)
    {
      long int num_ran = random(2);
      if(num_ran==0)
      {
        //ยกมือ L
        for (int i = 1; i <= n_sample; i++)
        {
          moveDegree(5, map(i,1,n_sample,0,130));//shoulder_Y L 
          delay(t_delay*0.5);
        }
        delay(t_delay_by_pos);
        
        //มือลง L
        for (int i = 1; i <= n_sample; i++)
        {
          moveDegree(5, map(i,1,n_sample,130,0));//shoulder_Y L 
          delay(t_delay*0.5);
        }
        delay(t_delay_by_pos);
      }
      else if(num_ran==1)
      {
        //ยกมือ R
        for (int i = 1; i <= n_sample; i++)
        {
          moveDegree(10, map(i,1,n_sample,180,50));//shoulder_Y R 
          delay(t_delay*0.5);
        }
        delay(t_delay_by_pos);
        
        //มือลง R
        for (int i = 1; i <= n_sample; i++)
        {
          moveDegree(10, map(i,1,n_sample,50,180));//shoulder_Y R
          delay(t_delay*0.5);
        }
        delay(t_delay_by_pos);
      }
    }
}
