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

// IMPORTANT: "Device" (not class "Myo") !!!        
void myoOnPair(Device myo, long timestamp, String firmware) {
  println("Sketch: myoOnPair & Device: "+myo.getId());
}

void myoOnUnpair(Device myo, long timestamp) {
  println("Sketch: myoOnUnpair & Device: "+myo.getId());
}

void myoOnConnect(Device myo, long timestamp, String firmware) {
  println("Sketch: myoOnConnect & Device: "+myo.getId());
}

void myoOnDisconnect(Device myo, long timestamp) {
  println("Sketch: myoOnDisconnect & Device: "+myo.getId());
}

void myoOnArmRecognized(Device myo, long timestamp, Arm arm) {
  println("Sketch: myoOnArmRecognized & Device: "+myo.getId());

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

void myoOnLock(Device myo, long timestamp){
  println("Sketch: myoOnLock & Device: "+myo.getId());
}
  
void myoOnUnLock(Device myo, long timestamp){
  println("Sketch: myoOnUnLock & Device: "+myo.getId());
}

void myoOnArmUnsync(Device myo, long timestamp) {
  println("Sketch: myoOnArmUnsync & Device: "+myo.getId());
}

void myoOnPose(Device myo, long timestamp, Pose pose) {
  println("Sketch: myoOnPose & Device: "+myo.getId());
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

void myoOnOrientation(Device myo, long timestamp, PVector orientation) {
  // println("Sketch: myoOnOrientation & Device: "+myo.getId());
}

void myoOnAccelerometer(Device myo, long timestamp, PVector accelerometer) {
  // println("Sketch: myoOnAccelerometer & Device: "+myo.getId());
}

void myoOnGyroscope(Device myo, long timestamp, PVector gyroscope) {
  // println("Sketch: myoOnGyroscope & Device: "+myo.getId());
}

void myoOnRssi(Device myo, long timestamp, int rssi) {
  // println("Sketch: myoOnRssi & Device: "+myo.getId());
}

// ----------------------------------------------------------

void myoOn(Myo.Event event, Device myo, long timestamp) {
  // println("Sketch: myoOn & Device: "+myo.getId());
  
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
