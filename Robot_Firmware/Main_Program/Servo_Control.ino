class Servo_Joint
{
  int Pin;    
  int MIN_deg;
  int MAX_deg;   
 
  public:
    Servo_Joint(int pin, int min_deg, int max_deg)
    {
      Pin = pin;
      MIN_deg = min_deg;
      MAX_deg = max_deg;
    }

    void set_deg(int degree)
    {
      if(degree >= MIN_deg && degree <= MAX_deg)
      {
        moveDegree(Pin,degree);
        //Serial.println("Pin:"+String(Pin)+" => "+String(degree));
      }
      else if(degree < MIN_deg)
      {
        moveDegree(Pin,MIN_deg);
        //Serial.println("!!!Error Pin:"+String(Pin)+" should between["+String(MIN_deg)+","+String(MAX_deg)+"] but It is "+String(degree));
      }
      else if(degree > MAX_deg)
      {
        moveDegree(Pin,MAX_deg);
        //Serial.println("!!!Error Pin:"+String(Pin)+" should between["+String(MIN_deg)+","+String(MAX_deg)+"] but It is "+String(degree));
      }
    }
};

void moveDegree(int servonum,int degree)
{
  uint16_t pulselen = map(degree, 0, 180, SERVOMIN, SERVOMAX);
  pwm.setPWM(servonum, 0, pulselen);
}

void dis_active(int servonum)
{
  pwm.setPWM(servonum, 0, 4096);
}

//Define L Leg 
Servo_Joint L_M_Hit_z = Servo_Joint(0,90,120);
Servo_Joint L_M_Hit_y = Servo_Joint(1,45,135);
Servo_Joint L_M_Knee2Hit = Servo_Joint(2,0,180);
Servo_Joint L_M_Knee2Foot = Servo_Joint(3,0,180);
Servo_Joint L_M_Foot_y = Servo_Joint(4,45,135);

//Define R Leg 
Servo_Joint R_M_Hit_z = Servo_Joint(15,90,120);
Servo_Joint R_M_Hit_y = Servo_Joint(14,45,135);
Servo_Joint R_M_Knee2Hit = Servo_Joint(13,0,180);
Servo_Joint R_M_Knee2Foot = Servo_Joint(12,0,180);
Servo_Joint R_M_Foot_y = Servo_Joint(11,45,135);
