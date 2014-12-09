import de.voidplus.myo.*;

Myo myo;

void setup() {
  size(800, 500);
  background(255);
  // ...

  myo = new Myo(this);
  // myo.setVerbose(true);
  // myo.setVerboseLevel(4); // Default: 1 (1-4)
}

void draw() {
  background(255);
  // ...
}

// ----------------------------------------------------------

void myoOnPair(Myo myo, long timestamp, String firmware) {
  println("Sketch: myoOnPair");
}

void myoOnUnpair(Myo myo, long timestamp) {
  println("Sketch: myoOnUnpair");
}

void myoOnConnect(Myo myo, long timestamp, String firmware) {
  println("Sketch: myoOnConnect");
}

void myoOnDisconnect(Myo myo, long timestamp) {
  println("Sketch: myoOnDisconnect");
}

void myoOnArmRecognized(Myo myo, long timestamp, Arm arm) {
  println("Sketch: myoOnArmRecognized");

  switch (arm.getType()) {
  case LEFT:
    println("Left arm.");
    break;
  case RIGHT:
    println("Right arm.");
    break;
  }

  if (myo.hasArm()) {
    if (myo.isArmLeft()) {
      println("Left arm.");
    } else {
      println("Right arm.");
    }
  }
}

void myoOnLock(Myo myo, long timestamp){
  println("Sketch: myoOnLock");
}
  
void myoOnUnLock(Myo myo, long timestamp){
  println("Sketch: myoOnUnLock");
}

void myoOnArmUnsync(Myo myo, long timestamp) {
  println("Sketch: myoOnArmUnsync");
}

void myoOnPose(Myo myo, long timestamp, Pose pose) {
  println("Sketch: myoOnPose");
  switch (pose.getType()) {
  case REST:
    println("Pose: REST");
    break;
  case FIST:
    println("Pose: FIST");
    myo.vibrate();
    break;
  case FINGERS_SPREAD:
    println("Pose: FINGERS_SPREAD");
    break;
  case DOUBLE_TAP:
    println("Pose: DOUBLE_TAP");
    break;
  case WAVE_IN:
    println("Pose: WAVE_IN");
    break;
  case WAVE_OUT:
    println("Pose: WAVE_OUT");
    break;
  default:
    break;
  }
}

void myoOnOrientation(Myo myo, long timestamp, PVector orientation) {
  // println("Sketch: myoOnOrientation");
}

void myoOnAccelerometer(Myo myo, long timestamp, PVector accelerometer) {
  // println("Sketch: myoOnAccelerometer");
}

void myoOnGyroscope(Myo myo, long timestamp, PVector gyroscope) {
  // println("Sketch: myoOnGyroscope");
}

void myoOnRssi(Myo myo, long timestamp, int rssi) {
  println("Sketch: myoOnRssi");
}

// ----------------------------------------------------------

void myoOn(Myo.Event event, Myo myo, long timestamp) {
  switch(event) {
  case PAIR:
    println("myoOn PAIR");
    break;
  case UNPAIR:
    println("myoOn UNPAIR");
    break;
  case CONNECT:
    println("myoOn CONNECT");
    break;
  case DISCONNECT:
    println("myoOn DISCONNECT");
    break;
  case ARM_SYNC:
    println("myoOn ARM_SYNC");
    break;
  case ARM_UNSYNC:
    println("myoOn ARM_UNSYNC");
    break;
  case POSE:
    println("myoOn POSE");  
    break;
  case ORIENTATION:
    // println("myoOn ORIENTATION");
    PVector orientation = myo.getOrientation();
    break;
  case ACCELEROMETER:
    // println("myoOn ACCELEROMETER");
    break;
  case GYROSCOPE:
    // println("myoOn GYROSCOPE");
    break;
  case RSSI:
    println("myoOn RSSI");
    break;
  }
}
