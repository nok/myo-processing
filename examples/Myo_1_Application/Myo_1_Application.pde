import de.voidplus.myo.*;

Myo myo;

PImage[] img;
boolean[] active;

void setup() {
  size(800, 200);
  background(255);
  // ...

  myo = new Myo(this);
  // myo.setVerbose(true);
  // myo.setVerboseLevel(2); // Default: 1 (1-3)
  
  myo.setFrequency(10);
  
  img = new PImage[5];
  img[0] = loadImage("data/double_tab.png");
  img[1] = loadImage("data/spread_fingers.png");
  img[2] = loadImage("data/wave_right.png");
  img[3] = loadImage("data/wave_left.png");
  img[4] = loadImage("data/make_a_fist.png");
  
  active = new boolean[5];
  resetImages();
}

void resetImages(){
  for(int i = 0; i<5; i++){
    active[i] = false;
  }
}

void draw() {
  background(255);
  // ...

  for (int i = 0; i<5; i++) {
    tint(255, (active[i]) ? 100 : 50);
    image(img[i], ((140*i)+(i*10))+30, 30, 140, 140);
  }
}

void myoOnPose(Device myo, long timestamp, Pose pose) {
  
  if (!pose.getType().toString().equals("REST")) {
    resetImages();
  }
  
  switch (pose.getType()) {
  case REST:
    // resetImages();
    break;
  case FIST:
    active[4] = true;
    myo.vibrate();
    break;
  case FINGERS_SPREAD:
    active[1] = true;
    break;
  case DOUBLE_TAP:
    active[0] = true;
    break;
  case WAVE_IN:
    active[2] = true;
    break;
  case WAVE_OUT:
    active[3] = true;
    break;
  default:
    break;
  }
}

void myoOnLock(Device myo, long timestamp) {
  resetImages();
}

void myoOnUnLock(Device myo, long timestamp) {
  resetImages();
}