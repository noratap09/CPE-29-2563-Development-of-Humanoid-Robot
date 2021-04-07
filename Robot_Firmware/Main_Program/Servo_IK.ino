#include <math.h> 

const double L = 0.04;
const double D_hit = 0.023;

void IK_L(double target_x,double target_y,double target_z,int bia_foot_y,int bia_knee2foot,int bia_knee2hit,int bia_hit_y,int deg_hit_z)
{
  double zeta1,zeta2,zeta3,zeta4;
  double l_1,l_2;
  
  int Knee2Hit_deg,Knee2Foot_deg,Hit_Foot_y_deg;
  int M_Hit_y_deg,M_Knee2Hit_deg,M_Knee2Foot_deg,M_Foot_y_deg;
  
  if(target_z != 0)
  {
    zeta4 = atan(target_y/target_z);
  } 
  else
  {
    zeta4 = 0;  
  }
  zeta4 = rad2deg(zeta4);
  //Serial.println("ZETA 4:"+String(zeta4));

  l_1 = sqrt( pow(target_x,2) + pow(target_y,2) + pow(target_z,2) );
  //Serial.println("l_1:"+String(l_1));

  double a = (target_y - (D_hit*sin( deg2rad(zeta4) )*2));
  double b = (target_z - (D_hit*cos( deg2rad(zeta4) )*2));
  //Serial.println("a,b:"+String(a)+","+String(b));

  l_2 = sqrt( pow(target_x,2) + pow(a,2) + pow(b,2) );
  //Serial.println("l_2:"+String(l_2));

  if(l_2 != 0)
  {
    zeta1 = asin(target_x/l_2);  
  }
  else
  {
    zeta1 = 0;
  }
  zeta1 = rad2deg(zeta1);
  //Serial.println("ZETA 1:"+String(zeta1));

  zeta2 = acos(l_2/(2*L));
  if(isnan(zeta2))
  {
    zeta2 = 0;
  }
  zeta2 = rad2deg(zeta2);
  //Serial.println("ZETA 2:"+String(zeta2));

  zeta3 = (180 -(2*zeta2));
  //Serial.println("ZETA 3:"+String(zeta3));

  Knee2Hit_deg = 90 - zeta1 - zeta2;
  Knee2Foot_deg = zeta3 - Knee2Hit_deg;
  Hit_Foot_y_deg = zeta4;
  //Serial.println("Knee2Hit_deg:"+String(Knee2Hit_deg));
  //Serial.println("Knee2Foot_deg:"+String(Knee2Foot_deg));
  //Serial.println("Hit_Foot_y_deg:"+String(Hit_Foot_y_deg));    

  M_Hit_y_deg = 90 - Hit_Foot_y_deg + bia_hit_y;
  M_Knee2Hit_deg = 135 - Knee2Hit_deg + bia_knee2hit;
  M_Knee2Foot_deg = 45 + Knee2Foot_deg + bia_knee2foot;
  M_Foot_y_deg = 90 - Hit_Foot_y_deg + bia_foot_y;

  M_Hit_y_deg = map(M_Hit_y_deg, (0+0) , (180-0) , 0 , 180 );
  M_Knee2Hit_deg = map(M_Knee2Hit_deg, (0+0) , (180-0) , 0 , 180 );
  M_Knee2Foot_deg = map(M_Knee2Foot_deg, (0+0) , (180-0) , 0 , 180 );
  M_Foot_y_deg = map(M_Foot_y_deg, (0+0) , (180-0) , 0 , 180 );

  L_M_Hit_y.set_deg(M_Hit_y_deg);
  L_M_Knee2Hit.set_deg(M_Knee2Hit_deg);
  L_M_Knee2Foot.set_deg(M_Knee2Foot_deg);
  L_M_Foot_y.set_deg(M_Foot_y_deg);
  L_M_Hit_z.set_deg(deg_hit_z);
}

void IK_R(double target_x,double target_y,double target_z,int bia_foot_y,int bia_knee2foot,int bia_knee2hit,int bia_hit_y,int deg_hit_z)
{
  double zeta1,zeta2,zeta3,zeta4;
  double l_1,l_2;
  
  int Knee2Hit_deg,Knee2Foot_deg,Hit_Foot_y_deg;
  int M_Hit_y_deg,M_Knee2Hit_deg,M_Knee2Foot_deg,M_Foot_y_deg;
  
  if(target_z != 0)
  {
    zeta4 = atan(target_y/target_z);
  } 
  else
  {
    zeta4 = 0;  
  }
  zeta4 = rad2deg(zeta4);
  //Serial.println("ZETA 4:"+String(zeta4));

  l_1 = sqrt( pow(target_x,2) + pow(target_y,2) + pow(target_z,2) );
  //Serial.println("l_1:"+String(l_1));

  double a = (target_y - (D_hit*sin( deg2rad(zeta4) )*2));
  double b = (target_z - (D_hit*cos( deg2rad(zeta4) )*2));
  //Serial.println("a,b:"+String(a)+","+String(b));

  l_2 = sqrt( pow(target_x,2) + pow(a,2) + pow(b,2) );
  //Serial.println("l_2:"+String(l_2));

  if(l_2 != 0)
  {
    zeta1 = asin(target_x/l_2);  
  }
  else
  {
    zeta1 = 0;
  }
  zeta1 = rad2deg(zeta1);
  //Serial.println("ZETA 1:"+String(zeta1));

  zeta2 = acos(l_2/(2*L));
  if(isnan(zeta2))
  {
    zeta2 = 0;
  }
  zeta2 = rad2deg(zeta2);
  //Serial.println("ZETA 2:"+String(zeta2));

  zeta3 = (180 -(2*zeta2));
  //Serial.println("ZETA 3:"+String(zeta3));

  Knee2Hit_deg = 90 - zeta1 - zeta2;
  Knee2Foot_deg = zeta3 - Knee2Hit_deg;
  Hit_Foot_y_deg = zeta4;
  //Serial.println("Knee2Hit_deg:"+String(Knee2Hit_deg));
  //Serial.println("Knee2Foot_deg:"+String(Knee2Foot_deg));
  //Serial.println("Hit_Foot_y_deg:"+String(Hit_Foot_y_deg));    

  M_Hit_y_deg = 90 - Hit_Foot_y_deg + bia_hit_y;
  M_Knee2Hit_deg = 45 + Knee2Hit_deg + bia_knee2hit;
  M_Knee2Foot_deg = 135 - Knee2Foot_deg + bia_knee2foot;
  M_Foot_y_deg = 90 - Hit_Foot_y_deg + bia_foot_y;

  M_Hit_y_deg = map(M_Hit_y_deg, (0+0) , (180-0) , 0 , 180 );
  M_Knee2Hit_deg = map(M_Knee2Hit_deg, (0+0) , (180-0) , 0 , 180 );
  M_Knee2Foot_deg = map(M_Knee2Foot_deg, (0+0) , (180-0) , 0 , 180 );
  M_Foot_y_deg = map(M_Foot_y_deg, (0+0) , (180-0) , 0 , 180 );

  R_M_Hit_y.set_deg(M_Hit_y_deg);
  R_M_Knee2Hit.set_deg(M_Knee2Hit_deg);
  R_M_Knee2Foot.set_deg(M_Knee2Foot_deg);
  R_M_Foot_y.set_deg(M_Foot_y_deg);
  R_M_Hit_z.set_deg(deg_hit_z);
}

double rad2deg(double rad)
{
  return rad * (180/PI);
}

double deg2rad(double deg)
{
  return deg * (PI/180);
}
