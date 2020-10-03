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

void setup() {
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

  //walk2();

  //Turn R
  //turn_R();

  //Turn L
  //turn_L();

  //center_pos();
}

void loop() {
    if (Blue_th.available())
    {
      char out_char = Blue_th.read();
      Blue_th_Flush(); //Clear Buffer
      Serial.write(out_char);
      if(out_char=='f') // Font
      {
        walk(10,0.03,10.00,0.015,5.00,0.00,10,'f');
      }
      else if(out_char=='r') //Right
      {
        walk(10,0.00,30.00,0.014,5.00,0.00,5,'r');
      }
      else if(out_char=='l') //Left
      {
        walk(10,0.00,-30.00,0.014,5.00,0.00,5,'l');
      }
      else if(out_char=='b') //Back
      {
        walk(10,-0.03,0.00,0.014,5.00,0.00,10,'b');
      }
      //Blue_th_Flush(); //Clear Buffer
    }

  if (Serial.available())
    {
      Blue_th.write(Serial.read());
    }
    /*
    if (Blue_th.available())
    {
      if(Blue_th.read()=="fd")
      {
        walk(5,0.03,0.00,0.015,5.00,0.00,10);
      }
      else if(Blue_th.read()=="rd")
      {
        walk(5,0.00,-30.00,0.014,5.00,0.00,5);
      }
      Serial.write(Blue_th.read());
    }
    */
}

void Blue_th_Flush(){
  while(Blue_th.available() > 0) {
    char t = Blue_th.read();
  }
}  

void center_pos()
{
  IK_L(0.00, 0.00, 0.1152, 0, 0, 0, 0, 95);
  IK_R(0.00, 0.00, 0.1152, 0, 0, 0, 0, 120);
}

float mapfloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  //float val = smooth(in_min,in_max,x,in_max);
  return (float)(x - in_min) * (out_max - out_min) / (float)(in_max - in_min) + out_min;
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

void walk(int num_walk,float walk_len,float walk_deg,float body_shift,float bia_body_shift,float bia_hit,int speed_per_f,char ck)
{
  if (num_walk > 0)
  {
    int n_sample = 20;
    int t_delay = speed_per_f;
    int t_delay_by_pos = 10;
    float h_com = 0.1152;

    //หมายถึงจังหวะที่ COM อยู่ตรงขาข้างนั้นๆ
    float body_shift_L = body_shift+0.007;
    float body_shift_R = body_shift+0.007; 
    
    float bia_body_shift_L = bia_body_shift+2.00;
    float bia_body_shift_R = bia_body_shift+4.00;

    float bia_hit_L = bia_hit+7.00;
    float bia_hit_R = bia_hit+10.00;

    float x_bia = -0.005;
    float y_bia = -0.00;

    //#I0 เริ่มต้น
    IK_L(0.00-x_bia, 0.00, h_com , 0, 0, 0, 0, 95);
    IK_R(0.00-x_bia, 0.00, h_com , 0, 0, 0, 0, 120);
    //delay(5000);

    //#I1 เถ COM ไปขา R
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L(0.00-x_bia, mapfloat(i, 1, n_sample, 0.00, -body_shift_R) - y_bia , h_com , 0, 0, 0, 0, 95);
      IK_R(0.00-x_bia, mapfloat(i, 1, n_sample, 0.00, -body_shift_R) - y_bia , h_com , 0, 0, 0, 0, 120);
      //Serial.println(mapfloat(i,1,n_sample,0.00,-0.02),10);
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

    //#P0 ยกขา L
    for (int i = 1; i <= n_sample; i++)
    {
      //IK_L(0.00, -0.02 , mapfloat(i,1,n_sample,0.081,0.041) ,0,0,0,0);
      IK_L(0.00-x_bia, -body_shift_R - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 95);
      IK_R(0.00-x_bia, -body_shift_R - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, bia_body_shift_R), 0, 0, -bia_hit_R , 120);
      //Serial.println((0.081 - bezier(mapfloat(i,1,n_sample,0.00,0.50))),10);
      delay(t_delay*0.2);
    }
    delay(t_delay_by_pos);

    for (int i_walk = 1; i_walk <= num_walk; i_walk++)
    {
      //#P1 วางขา L
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( mapfloat(i, 1, n_sample, 0.00, walk_len)-x_bia , -body_shift_R - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 0.90))) , 0, 0, 0, 0, 90 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg),0.00,walk_deg) );
          IK_R(0.00-x_bia, -body_shift_R - y_bia  , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R,bia_body_shift_R*0.2), 0, 0, -bia_hit_R , 120);
          delay(t_delay*0.2);
      }
      delay(t_delay_by_pos);

      //#P2 เถ COM ไปขา L
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( mapfloat(i, 1, n_sample, walk_len, 0.00) -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L*0.5) - y_bia , h_com , 0, 0, 0, 0, 90  + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg) );
          IK_R( mapfloat(i, 1, n_sample, 0.00, -walk_len) -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L*0.5) - y_bia , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R*0.2, 0.00), 0, 0, 0, 120 -  constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,0.00,walk_deg) );
          delay(t_delay);
      }
      delay(t_delay_by_pos);

      //#P3 ยกขา R
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample,body_shift_L*0.5,body_shift_L) - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, -bia_body_shift_L) , 0, 0, bia_hit_L , 95);
          IK_R( mapfloat(i, 1, n_sample, -walk_len, 0.00) -x_bia , mapfloat(i, 1, n_sample,body_shift_L*0.5,body_shift_L) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 120 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg));
          delay(t_delay*0.5);
      }
    delay(t_delay_by_pos);

    //check signal จาก App หากไม่มีหยุด
    if (Blue_th.available())
    {
      char out_char = Blue_th.read();
      Blue_th_Flush(); //Clear Buffer
      Serial.write(out_char);
      Serial.println("Stop_R");
      if(out_char != ck)
      {
        //#F0 วางขา R
        for (int i = 1; i <= n_sample; i++)
        {
          IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , mapfloat(i, 1, n_sample, -bia_body_shift_L, 0.00) , 0, 0, bia_hit_L , 95);
          IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia, h_com , 0 , 0, 0, 0 , 120);
          delay(t_delay*0.2);
        }
        delay(t_delay_by_pos);
    
        IK_L(0.00 -x_bia, 0.00 - y_bia , h_com , 0, 0, 0, 0, 95);
        IK_R(0.00 -x_bia, 0.00 - y_bia , h_com , 0, 0, 0, 0, 120);
    
        break;
      }
    }
    else
    {
      Serial.println("Stop_R without signal");
    }    

    //#P4 วางขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L(0.00 -x_bia , body_shift_L , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L, -bia_body_shift_L*0.2) - y_bia , 0, 0, bia_hit_L , 95);
        //IK_R( mapfloat(i,1,n_sample,0.00,walk_len) , 0.02 , mapfloat(i,1,n_sample,0.041,0.081) ,0,0,0,0);
        IK_R( mapfloat(i, 1, n_sample, 0.00, walk_len) -x_bia , body_shift_L - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 120 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,walk_deg,0.00));
        delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

    //#P5 เถ COM ไปขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( mapfloat(i, 1, n_sample, 0.00, -walk_len) -x_bia , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R*0.5) - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L*0.2, 0.00), 0, 0, 0, 95 - constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,walk_deg,0.00));
        IK_R( mapfloat(i, 1, n_sample, walk_len, 0.00) -x_bia , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R*0.5) - y_bia , h_com , 0, 0, 0, 0, 120 + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,walk_deg,0.00));
        delay(t_delay);
    }
    delay(t_delay_by_pos);

    //#P6 ยกขา L
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( mapfloat(i, 1, n_sample, -walk_len, 0.00) -x_bia, mapfloat(i, 1, n_sample, -body_shift_R*0.5, -body_shift_R) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 95 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,walk_deg,0.00));
        IK_R( 0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R*0.5, -body_shift_R) - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, bia_body_shift_R), 0, 0, -bia_hit_R , 120);
        delay(t_delay*0.2); 
    }
    delay(t_delay_by_pos);

    //check signal จาก App หากไม่มีหยุด
    if (Blue_th.available())
    {
      char out_char = Blue_th.read();
      Blue_th_Flush(); //Clear Buffer
      Serial.write(out_char);
      Serial.println("Stop_L");
      if(out_char != ck)
      {
        //#F0 วางขา L
        for (int i = 1; i <= n_sample; i++)
        {
          IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
          IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, -bia_hit_R, 120);
          delay(t_delay*0.2);
        }
        delay(t_delay_by_pos);
    
        IK_L(0.00 -x_bia, 0.00, h_com , 0, 0, 0, 0, 95);
        IK_R(0.00 -x_bia, 0.00, h_com , 0, 0, 0, 0, 120);
    
        break;
      }
    }
    else
    {
      Serial.println("Stop_L without signal");
      //#F0 วางขา L
      for (int i = 1; i <= n_sample; i++)
      {
        IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
        IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, -bia_hit_R, 120);
        delay(t_delay*0.2);
      }
      delay(t_delay_by_pos);
  
      IK_L(0.00 -x_bia, 0.00 - y_bia, h_com , 0, 0, 0, 0, 95);
      IK_R(0.00 -x_bia, 0.00 - y_bia, h_com , 0, 0, 0, 0, 120);
      
      break;
    }

  }
    /*
    //#F1 เถCom มาตรงกลาง
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L( 0.00 , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) , h_com , 0, 0, 0, 0, 90);
      IK_R(0.00, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, 0, 120);
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);
    */
  }
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
    float body_shift_R = body_shift+0.000; 
    
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
