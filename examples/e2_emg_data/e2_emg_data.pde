import de.voidplus.myo.*;

Myo myo;

void setup() {
  size(800, 500);
  background(255);
  // ...

  myo = new Myo(this);
  // myo.setVerbose(true);
  // myo.setVerboseLevel(4); // Default: 1 (1-4)
  
  myo.withEmg();
  // myo.withoutEmg();
}

void draw() {
  background(255);
  // ...
}

// ----------------------------------------------------------

void myoOnEmg(Myo myo, long timestamp, int[] data) {
  println("Sketch: myoOnEmg");
  for(int i = 0; i<data.length; i++){
    println(data[i]); // [-128 - 127]
  }
}

// ----------------------------------------------------------

void myoOn(Myo.Event event, Myo myo, long timestamp) {
  switch(event) {
  case EMG:
    println("myoOn EMG");
    int[] data = myo.getEmg();
    for(int i = 0; i<data.length; i++){
      println(data[i]); // [-128 - 127] 
    }
    break;
  }
}
