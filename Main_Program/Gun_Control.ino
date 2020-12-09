void dc_motor_R(int value)
{
  if(value == 0)
  {
    digitalWrite(dc_motor_pin_A1,LOW);
    digitalWrite(dc_motor_pin_A2,LOW);
  }
  else if(value==1)
  {
    digitalWrite(dc_motor_pin_A1,HIGH);
    digitalWrite(dc_motor_pin_A2,LOW);
  }
  else if(value==-1)
  {
    digitalWrite(dc_motor_pin_A1,LOW);
    digitalWrite(dc_motor_pin_A2,HIGH);
  }
}

void dc_motor_L(int value)
{
  if(value == 0)
  {
    digitalWrite(dc_motor_pin_B1,LOW);
    digitalWrite(dc_motor_pin_B2,LOW);
  }
  else if(value==1)
  {
    digitalWrite(dc_motor_pin_B1,LOW);
    digitalWrite(dc_motor_pin_B2,HIGH);
  }
  else if(value==-1)
  {
    digitalWrite(dc_motor_pin_B1,HIGH);
    digitalWrite(dc_motor_pin_B2,LOW);
  }
}

void gun_l_set_pos(int x,int y)
{
  int x_value = constrain(x, 80, 180);
  int y_value = constrain(y, 0, 180);

  moveDegree(5, y_value);//shoulder_Y L 
  moveDegree(6, x_value);//shoulder_X L 
}

void gun_r_set_pos(int x,int y)
{
  int x_value = constrain(x, 0, 100);
  int y_value = constrain(y, 0, 180);

  moveDegree(10, 180-y_value);//shoulder_Y L 
  moveDegree(9, x_value);//shoulder_X L 
}
