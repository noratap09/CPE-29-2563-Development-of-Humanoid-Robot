void walk(int num_walk,float walk_len,float walk_deg,float walk_slide,float body_shift,float bia_body_shift,float bia_hit,int speed_per_f,String ck,boolean auto_mode)
{
  if (num_walk > 0)
  {
    bool ck_stop = false;
    int n_sample = 20;
    int t_delay = speed_per_f;
    int t_delay_by_pos = 10;
    float h_com = 0.1152;

    //หมายถึงจังหวะที่ COM อยู่ตรงขาข้างนั้นๆ
    float body_shift_L = body_shift+0.012;
    float body_shift_R = body_shift+0.012; 
    
    float bia_body_shift_L = bia_body_shift+3.00;
    float bia_body_shift_R = bia_body_shift+3.00;

    float bia_hit_L = bia_hit+0.00;
    float bia_hit_R = bia_hit+0.00;

    float x_bia = -0.005;
    float y_bia = -0.01;

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
          IK_L( mapfloat(i, 1, n_sample, 0.00, walk_len)-x_bia , -body_shift_R + constrainfloat(mapfloat(i, 1, n_sample, 0.00, walk_slide),walk_slide,0.00) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 0.90))) , 0, 0, 0, 0, 90 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg),0.00,walk_deg) );
          IK_R(0.00-x_bia, -body_shift_R - y_bia  , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R,bia_body_shift_R*0.2), 0, 0, -bia_hit_R , 120);
          delay(t_delay*0.2);
      }
      delay(t_delay_by_pos);

      //#P2 เถ COM ไปขา L
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( mapfloat(i, 1, n_sample, walk_len, 0.00) -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L*0.9) + constrainfloat(mapfloat(i, 1, n_sample,walk_slide,0.00),walk_slide,0.00) - y_bia , h_com , 0, 0, 0, 0, 90  + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg) );
          IK_R( mapfloat(i, 1, n_sample, 0.00, -walk_len) -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L*0.9) - constrainfloat(mapfloat(i, 1, n_sample,0.00,walk_slide),walk_slide,0.00) - y_bia , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R*0.2, 0.00), 0, 0, 0, 120 -  constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,0.00,walk_deg) );
          delay(t_delay*2.0);
      }
      delay(t_delay_by_pos);

      //#P3 ยกขา R
      for (int i = 1; i <= n_sample; i++)
      {
          IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample,body_shift_L*0.9,body_shift_L) - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, -bia_body_shift_L) , 0, 0, bia_hit_L , 95);
          IK_R( mapfloat(i, 1, n_sample, -walk_len, 0.00) -x_bia , mapfloat(i, 1, n_sample,body_shift_L*0.9,body_shift_L) - constrainfloat(mapfloat(i, 1, n_sample,walk_slide,0.00),walk_slide,0.00) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 120 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg));
          delay(t_delay*0.5);
      }
    delay(t_delay_by_pos);

    //check signal จาก App หากไม่มีหยุด
    if(!auto_mode)
    {
      if (Blue_th.available())
      {
        String out_char = Blue_th.readStringUntil('\n');
        Blue_th_Flush(); //Clear Buffer
        //Serial.write(out_char);
        //Serial.println("Stop_R");
        if(out_char != ck)
        {
          //#F1 เถCom มาตรงกลาง R
            for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , body_shift_L - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L, 0.00) , 0, 0, bia_hit_L, 95);
              IK_R(0.00 -x_bia, body_shift_L - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00)))  , 0 , 0, 0, 0 , 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
        
           for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia , h_com , 0, 0, 0, mapfloat(i, 1, n_sample, bia_hit_L, 0.00), 95);
              IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia , h_com , 0, 0, 0, 0 , 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
            ck_stop = true;
          break;
        }
        
      }
      else
      {
        Serial.println("Stop_R without signal");
          //#F1 เถCom มาตรงกลาง R
            for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , body_shift_L - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L, 0.00) , 0, 0, bia_hit_L, 95);
              IK_R(0.00 -x_bia, body_shift_L - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00)))  , 0 , 0, 0, 0 ,120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
        
           for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia , h_com , 0, 0, 0, mapfloat(i, 1, n_sample, bia_hit_L, 0.00), 95);
              IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia , h_com , 0, 0, 0, 0 , 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
            ck_stop = true;
        break;
      }
    }

  
    //#P4 วางขา R
    for (int i = 1; i <= n_sample; i++)
    {

        IK_L(0.00 -x_bia , body_shift_L , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L, -bia_body_shift_L*0.2) - y_bia , 0, 0, bia_hit_L , 95);
        //IK_R( mapfloat(i,1,n_sample,0.00,walk_len) , 0.02 , mapfloat(i,1,n_sample,0.041,0.081) ,0,0,0,0);
        IK_R( mapfloat(i, 1, n_sample, 0.00, walk_len) -x_bia , body_shift_L + constrainfloat(mapfloat(i, 1, n_sample,0.00,walk_slide),0.00,walk_slide) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 120 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,walk_deg,0.00));
        delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);
    
    //#P5 เถ COM ไปขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( mapfloat(i, 1, n_sample, 0.00, -walk_len) -x_bia , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R*0.9) - constrainfloat(mapfloat(i, 1, n_sample,0.00,walk_slide),0.00,walk_slide) - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L*0.2, 0.00), 0, 0, 0, 95 - constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,walk_deg,0.00));
        IK_R( mapfloat(i, 1, n_sample, walk_len, 0.00) -x_bia , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R*0.9) + constrainfloat(mapfloat(i, 1, n_sample,walk_slide,0.00),0.00,walk_slide) - y_bia , h_com , 0, 0, 0, 0, 120 + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,walk_deg,0.00));
        delay(t_delay);
    }
    delay(t_delay_by_pos);

    //#P6 ยกขา L
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( mapfloat(i, 1, n_sample, -walk_len, 0.00) -x_bia, mapfloat(i, 1, n_sample, -body_shift_R*0.9, -body_shift_R) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 95 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,walk_deg,0.00));
        IK_R( 0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R*0.9, -body_shift_R)  + constrainfloat(mapfloat(i, 1, n_sample,walk_slide,0.00),0.00,walk_slide) - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, bia_body_shift_R), 0, 0, -bia_hit_R , 120);
        delay(t_delay*0.2); 
    }
    delay(t_delay_by_pos);
    
    //check signal จาก App หากไม่มีหยุด
    if(!auto_mode)
    {
      if (Blue_th.available())
      {
        String out_char = Blue_th.readStringUntil('\n');
        Blue_th_Flush(); //Clear Buffer
        //Serial.write(out_char);
        //Serial.println("Stop_L");
        if(out_char != ck)
        {
          //#F1 เถCom มาตรงกลาง L
            for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , -body_shift_R - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
              IK_R(0.00 -x_bia, -body_shift_R - y_bia , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, -bia_hit_R, 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
        
           for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , 0, 0, 0, 0, 95);
              IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , 0, 0, 0, mapfloat(i, 1, n_sample, -bia_hit_R, 0.00) , 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
            ck_stop = true;
          break;
        }
      }
      else
      {
        Serial.println("Stop_L without signal");
          //#F1 เถCom มาตรงกลาง L
            for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , -body_shift_R - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
              IK_R(0.00 -x_bia, -body_shift_R - y_bia , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, -bia_hit_R, 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
        
           for (int i = 1; i <= n_sample; i++)
            {
              IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , 0, 0, 0, 0, 95);
              IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , 0, 0, 0, mapfloat(i, 1, n_sample, -bia_hit_R, 0.00) , 120);
              delay(t_delay);
            }
            delay(t_delay_by_pos);
            ck_stop = true;
        break;
        
      }
    }
    
  }
  

    if(!ck_stop)
    {
      //#F1 เถCom มาตรงกลาง
        for (int i = 1; i <= n_sample; i++)
        {
          IK_L( 0.00 -x_bia , -body_shift_R - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0, 95);
          IK_R(0.00 -x_bia, -body_shift_R - y_bia , h_com , mapfloat(i, 1, n_sample, bia_body_shift_R, 0.00), 0, 0, -bia_hit_R, 120);
          delay(t_delay);
        }
        delay(t_delay_by_pos);
  
       for (int i = 1; i <= n_sample; i++)
        {
          IK_L( 0.00 -x_bia , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , 0, 0, 0, 0, 95);
          IK_R(0.00 -x_bia, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , h_com , 0, 0, 0, mapfloat(i, 1, n_sample, -bia_hit_R, 0.00) , 120);
          delay(t_delay);
        }
        delay(t_delay_by_pos);
    
        //IK_L(0.00 -x_bia, 0.00 - y_bia, h_com , 0, 0, 0, 0, 95);
        //IK_R(0.00 -x_bia, 0.00 - y_bia, h_com , 0, 0, 0, 0, 120);
    }
    
  }
}

void Trun_L(float walk_deg,float body_shift,float bia_body_shift,float bia_hit,int speed_per_f,char ck)
{
    int n_sample = 20;
    int t_delay = speed_per_f;
    int t_delay_by_pos = 10;
    float h_com = 0.1152;

    //หมายถึงจังหวะที่ COM อยู่ตรงขาข้างนั้นๆ
    float body_shift_L = body_shift+0.007;
    float body_shift_R = body_shift+0.000; 
    
    float bia_body_shift_L = bia_body_shift+3.00;
    float bia_body_shift_R = bia_body_shift+0.00;

    float bia_hit_L = bia_hit+7.00;
    float bia_hit_R = bia_hit+0.00;

    float x_bia = -0.005;
    float y_bia = -0.01;

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

    //#P1 วางขา L
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( 0.00-x_bia , -body_shift_R - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 0.90))) , 0, 0, 0, 0, 90 + constrain( mapfloat(i,1,n_sample,0.00,walk_deg),0.00,walk_deg) );
        IK_R(0.00-x_bia, -body_shift_R - y_bia  , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R,bia_body_shift_R*0.2), 0, 0, -bia_hit_R , 120);
        delay(t_delay*0.2);
    }
    delay(t_delay_by_pos);

    //#P2 เถ COM ไปขา L
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( 0.00-x_bia , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L*0.5) - y_bia , h_com , 0, 0, 0, 0, 90  + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg) );
        IK_R( 0.00-x_bia , mapfloat(i, 1, n_sample, -body_shift_R, body_shift_L*0.5) - y_bia , h_com , mapfloat(i, 1, n_sample,bia_body_shift_R*0.2, 0.00), 0, 0, 0, 120 -  constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,0.00,walk_deg) );
        delay(t_delay);
    }
    delay(t_delay_by_pos);

    //#P3 ยกขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( 0.00-x_bia , mapfloat(i, 1, n_sample,body_shift_L*0.5,body_shift_L) - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, -bia_body_shift_L) , 0, 0, bia_hit_L , 95);
        IK_R( 0.00-x_bia , mapfloat(i, 1, n_sample,body_shift_L*0.5,body_shift_L) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0, 120 - constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg));
        delay(t_delay*0.5);
    }
  delay(t_delay_by_pos);

  //#F0 วางขา R
  for (int i = 1; i <= n_sample; i++)
  {
    IK_L( 0.00-x_bia , mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L, 0.00) , 0, 0, bia_hit_L , 95);
    IK_R(0.00-x_bia, mapfloat(i, 1, n_sample, body_shift_L, 0.00) - y_bia, (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0 , 0, 0, 0 , 120);
    delay(t_delay*0.2);
  }
  delay(t_delay_by_pos);

  IK_L(0.00 -x_bia, 0.00 - y_bia , h_com , 0, 0, 0, 0, 95);
  IK_R(0.00 -x_bia, 0.00 - y_bia , h_com , 0, 0, 0, 0, 120);
}

void Trun_R(float walk_deg,float body_shift,float bia_body_shift,float bia_hit,int speed_per_f,char ck)
{
    int n_sample = 20;
    int t_delay = speed_per_f;
    int t_delay_by_pos = 10;
    float h_com = 0.1152;

    //หมายถึงจังหวะที่ COM อยู่ตรงขาข้างนั้นๆ
    float body_shift_L = body_shift+0.007;
    float body_shift_R = body_shift+0.007; 
    
    float bia_body_shift_L = bia_body_shift+3.00;
    float bia_body_shift_R = bia_body_shift+3.00;

    float bia_hit_L = bia_hit+7.00;
    float bia_hit_R = bia_hit+10.00;

    float x_bia = -0.005;
    float y_bia = -0.01;

    //#I0 เริ่มต้น
    IK_L(0.00-x_bia, 0.00, h_com , 0, 0, 0, 0, 95);
    IK_R(0.00-x_bia, 0.00, h_com , 0, 0, 0, 0, 120);
    //delay(5000);

    //#I1 เถ COM ไปขา L
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L(0.00-x_bia, mapfloat(i, 1, n_sample, 0.00, body_shift_L) - y_bia , h_com , 0, 0, 0, 0, 95);
      IK_R(0.00-x_bia, mapfloat(i, 1, n_sample, 0.00, body_shift_L) - y_bia , h_com , 0, 0, 0, 0, 120);
      delay(t_delay*0.5);
    }
    delay(t_delay_by_pos);

    //#P0 ยกขา R
    for (int i = 1; i <= n_sample; i++)
    {
      IK_L(0.00-x_bia, body_shift_L - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, -bia_body_shift_L) , 0, 0, bia_hit_L, 95);
      IK_R(0.00-x_bia, body_shift_L - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0, 0, 0, 0 , 120);
      delay(t_delay*0.2);
    }
    delay(t_delay_by_pos);

    //#P1 วางขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( 0.00-x_bia , body_shift_L - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L,-bia_body_shift_L*0.2) , 0, 0, bia_hit_L, 95);
        IK_R(0.00-x_bia, body_shift_L - y_bia  , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0, 0, 0, 0 ,  120 -  constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,0.00,walk_deg));
        delay(t_delay*0.2);
    }
    delay(t_delay_by_pos);

    //#P2 เถ COM ไปขา R
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( 0.00-x_bia , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R*0.5) - y_bia , h_com , mapfloat(i, 1, n_sample, -bia_body_shift_L*0.2,0.0) , 0, 0, 0, 90  + constrain( mapfloat(i,1,n_sample,0.00,walk_deg) ,0.00,walk_deg) );
        IK_R( 0.00-x_bia , mapfloat(i, 1, n_sample, body_shift_L, -body_shift_R*0.5) - y_bia , h_com , 0, 0, 0, 0, 120 -  constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg) );
        delay(t_delay);
    }
    delay(t_delay_by_pos);

    //#P3 ยกขา L
    for (int i = 1; i <= n_sample; i++)
    {
        IK_L( 0.00-x_bia , mapfloat(i, 1, n_sample,-body_shift_R*0.5,body_shift_R) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.00, 0.50))) , 0 , 0, 0, 0 , 90  + constrain( mapfloat(i,1,n_sample,walk_deg,0.00) ,0.00,walk_deg));
        IK_R( 0.00-x_bia , mapfloat(i, 1, n_sample,-body_shift_R*0.5,body_shift_R) - y_bia , h_com , mapfloat(i, 1, n_sample, 0.00, bia_body_shift_R), 0, 0, -bia_hit_R, 120);
        delay(t_delay*0.5);
    }
  delay(t_delay_by_pos);

  //#F0 วางขา L
  for (int i = 1; i <= n_sample; i++)
  {
    IK_L( 0.00-x_bia , mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia , (h_com - bezier(mapfloat(i, 1, n_sample, 0.50, 1.00))) , 0 , 0, 0, 0 , 95);
    IK_R(0.00-x_bia, mapfloat(i, 1, n_sample, -body_shift_R, 0.00) - y_bia, h_com , mapfloat(i, 1, n_sample, bia_body_shift_R , 0.00) , 0, 0, -bia_hit_R , 120);
    delay(t_delay*0.2);
  }
  delay(t_delay_by_pos);

  IK_L(0.00 -x_bia, 0.00 - y_bia , h_com , 0, 0, 0, 0, 95);
  IK_R(0.00 -x_bia, 0.00 - y_bia , h_com , 0, 0, 0, 0, 120);
}
