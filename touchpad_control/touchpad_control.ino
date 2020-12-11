// Tells the Touchbot drive to follow a path around a circle shown on
// the touchscreen based on positional feedback from the android app through
// bluetooth communication.

// This controler does not require an encoder, and rather looks at changes in
// position to determine the cobot's path and required change in direction.


// Bluetooh setup
#include "BluetoothSerial.h"
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif
BluetoothSerial SerialBT;

// PWM properties
#define PWM 15
#define DIR 18
const int freq = 5000;
const int ledChannel = 0;
const int resolution = 8;

// Positional variables
const int cir_x = 400;
const int cir_y = 575;
const int rad = 150;
const int max_slope = 30;
const int min_dist = 7;
const int max_dist = 25;
const int min_pwm = 127;
const float internal_theta_perp = 3.14/2;

int xp_checker = 0;
String mov_direc = "";
String mov_direc1 = "";
String mov_direc2 = "";
String mov_direc_out = "";
String mov_angle = "";
String mov_angle1 = "";
String mov_angle2 = "";
String mov_angle_out = "";

String x1_pos, y11_pos, x2_pos, y2_pos, ser_read, center_string, cont, pos_string;
char number_char;
int x1, y11, x2, y2, contacts, bt_count, power2, min_flag, power3, max_power, power_input;
float xc, yc, xcp, ycp, slope_prev, slope_curr, slope_line, inter_line;
float a, b, c, d, x_inter1, x_inter2, y_inter1, y_inter2, inter_dist, init_dist;
float xc2, yc2, xcp2, ycp2, xpred, ypred, time1, time_prev, time_change;
float a_dist, b_dist, c_dist, b_angle, theta_perp, rpm;


void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32test"); //Bluetooth device name
  xcp = 0;
  ycp = 0;

  // configure Motor PWM functionalitites
  pinMode(PWM, OUTPUT);
  pinMode(DIR, OUTPUT);
  ledcSetup(ledChannel, freq, resolution);

  // attach the channel to the GPIO to be controlled
  ledcAttachPin(PWM, ledChannel);
  min_flag = 0;
  time_prev = millis();
}

void loop() {
  if (!SerialBT.available()) {
    // arduino pulling data faster than android output so sometimes there's
    // no signal between outputs. If no signal long enough, turn off motor
    bt_count += 1;
    if (bt_count > 40000){
      ledcWrite(ledChannel, 0);
      xp_checker = 0;
    }
  }
  else {
    delay(3);
    // read in 8 bytes for the center position (need to change to hex)
    get_pos();

    bt_count = 0;

    // find center point
    xc = x1;
    yc = y11;

    if (xp_checker == 0){
      xcp = xc;
      ycp = yc;
      xp_checker = 1;
    }

    else{
      // calculate distance between points
      init_dist = sqrt(pow((xc - xcp), 2) + pow((yc - ycp), 2));

      if (init_dist < min_dist){
        ledcWrite(ledChannel, 0);
        if (min_flag == 0){
          time1 = millis();
        }
        min_flag = 1;
      }

      else{
        if (min_flag == 1){
          min_flag = 0;
        }
        else{
          time1 = millis();
        }
        time_change = time1 - time_prev;
        time_prev = time1;

        // check if moving cw or ccw around the circle
        mov_dir();

        // change coordinates to circle's center as origin
        xc2 = xc - cir_x;
        xcp2 = xcp - cir_x;
        yc2 = yc - cir_y;
        ycp2 = ycp - cir_y;

        // predict next position based on current trajectory
        xpred = xc2 + (xc2 - xcp2);
        ypred = yc2 + (yc2 - ycp2);

        // solve for angle from ((x,y)prev to (x,y)curr) to origin
          // use side-side-side equation
        a_dist = init_dist;
        b_dist = sqrt(pow(xpred, 2) + pow(ypred, 2));
        c_dist = sqrt(pow(xc2, 2) + pow(yc2, 2));

        // angle version law of cosines
        b_angle = acos((c_dist*c_dist + a_dist*a_dist - b_dist*b_dist) / (2*c_dist*a_dist));


        // solve for tangent angle to circle
          // inside circle
        if (c_dist < rad){
          theta_perp = internal_theta_perp;
        }
        else{
          theta_perp = asin(rad/c_dist);
        }

        // turn based on b_angle - theta_perp
        if (b_angle > theta_perp){
          cont = "close ";
        }
        else if(b_angle < theta_perp){
          cont = "open ";
        }

        mov_angle2 = mov_angle1;
        mov_angle1 = mov_angle;
        mov_angle = cont;

        // avg last 3 values to remove single misdirections from popping up
        if (mov_angle==mov_angle1 || mov_angle==mov_angle2 || mov_angle1 == ""){
          mov_angle_out = mov_angle;
        }

        if (mov_angle_out == "close "){
          turn(mov_direc_out,0);
        }
        else if (mov_angle_out == "open "){
        }

        // replace previous position
        xcp = xc;
        ycp = yc;

      }     // else; if(init_dist < min_dist)
    }       // else; if(xp_checker = 1)
  }         // else ; if (!serialbt.available)
}           // loop


void MotorClockwise(int power){
  ledcWrite(ledChannel, power);
  digitalWrite(DIR, HIGH);
  }

void MotorCounterClockwise(int power){
  ledcWrite(ledChannel, power);
  digitalWrite(DIR, LOW);
}

void turn(String turn_direc, int contac){

  rpm = init_dist/(c_dist*(time_change/(1000*60)));

  power3 = map(rpm,0,100,0, 255);

  if (c_dist > 400){
    power3 = 255;
  }

  power_input = power3;

  if (turn_direc == "cw" && contac == 0){
    MotorClockwise(power_input);
  }
  else if (turn_direc == "cw" && contac == 2){
    MotorCounterClockwise(power_input);
  }
  else if (turn_direc == "ccw" && contac == 0){
    MotorCounterClockwise(power_input);
  }
  else if (turn_direc == "ccw" && contac == 2){
    MotorClockwise(power_input);
  }
}

void get_pos(){
  // For sending values as ascii characters
    x1_pos = "";
    y11_pos = "";
    for (int i=0; i<8; i++){
      number_char = SerialBT.read();
      if (i < 4){
        x1_pos += number_char;
      }
      else{
        y11_pos += number_char;
      }
    }
    x1 = x1_pos.toInt();
    y11 = y11_pos.toInt();
    // delay(15);
}

void mov_dir(){
  // check if moving cw or ccw around the circle

  // check the slope of the lines between the circle's
  //   center and the positional values (current and previous)
  slope_prev = (ycp - cir_y) / (xcp - cir_x);
  slope_curr = (yc  - cir_y) / (xc  - cir_x);

  // if slope_curr > slope_prev, moving cw
  if (slope_curr > slope_prev){
    mov_direc2 = mov_direc1;
    mov_direc1 = mov_direc;
    mov_direc = "cw";
  }
  else{
    mov_direc2 = mov_direc1;
    mov_direc1 = mov_direc;
    mov_direc = "ccw";
  }

  // avg last 3 values to remove single misdirections from popping up
  if (mov_direc==mov_direc1 || mov_direc==mov_direc2){
    mov_direc_out = mov_direc;
  }
}
