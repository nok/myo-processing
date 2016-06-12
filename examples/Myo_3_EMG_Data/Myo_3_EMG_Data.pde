import de.voidplus.myo.*;

Myo myo;
ArrayList<ArrayList<Integer>> sensors;

void setup() {
  size(800, 400);
  background(255);
  noFill();
  stroke(0);
  // ...

  myo = new Myo(this, true); // true, with EMG data
  
  sensors = new ArrayList<ArrayList<Integer>>();
  for (int i=0; i<8; i++) {
    sensors.add(new ArrayList<Integer>()); 
  }
}

void draw() {
  background(255);
  // ...
  
  // Drawing:
  synchronized (this) {
    for (int i=0; i<8; i++) {
      if (!sensors.get(i).isEmpty()) {
        beginShape();
        for (int j=0; j<sensors.get(i).size(); j++) {
          vertex(j, sensors.get(i).get(j)+(i*50));
        }
        endShape();
      } 
    }
  }
}

// ----------------------------------------------------------

void myoOnEmgData(Device myo, long timestamp, int[] data) {
  // println("Sketch: myoOnEmgData, device: " + myo.getId());
  // int[] data <- 8 values from -128 to 127
  
  // Data:
  synchronized (this) {
    for (int i = 0; i<data.length; i++) {
      sensors.get(i).add((int) map(data[i], -128, 127, 0, 50)); // [-128 - 127]
    }
    while (sensors.get(0).size() > width) {
      for(ArrayList<Integer> sensor : sensors) {
        sensor.remove(0);
      }
    }
  }
}

// ----------------------------------------------------------

/*
void myoOn(Myo.Event event, Device myo, long timestamp) {
  switch(event) {
  case EMG_DATA:
    // println("myoOn EMG & Device: "+myo.getId());
    // int[] data <- 8 values from -128 to 127
    int[] data = myo.getEmg();
    for(int i = 0; i<data.length; i++){
      println(map(data[i], -128, 127, 0, 50)); // [-128 - 127] 
    }
    break;
  }
}
*/