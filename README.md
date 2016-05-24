# Myo for Processing

[![Join the chat at https://gitter.im/nok/myo-processing](https://badges.gitter.im/nok/myo-processing.svg)](https://gitter.im/nok/myo-processing?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Library to use the [Myo](https://www.thalmic.com) in [Processing](http://processing.org/).


## Table of Contents

- [About](#about)
- [Download](#download)
- [Installation](#installation)
- [Dependencies](#dependencies)
- [Tested](#tested)
- [Examples](#examples)
- [Usage](#usage)
- [Questions?](#questions)
- [License](#license)


## About

The [Myo](https://www.thalmic.com) armband lets you use the electrical activity in your muscles to wirelessly control your computer, phone, and other favorite digital technologies. With the wave of your hand, it will transform how you interact with your digital world.


## Download

- [Myo for Processing v0.8.1.2](download/MyoForProcessing.zip?raw=true)

Note: If you are interested in the newest **development** implementation, so have a look at the [dev branch](https://github.com/nok/myo-processing/tree/dev).


## Installation

Unzip and put the extracted *MyoForProcessing* folder into the libraries folder of your Processing sketches. Reference and examples are included in the *MyoForProcessing* folder. For more help read the [tutorial](http://www.learningprocessing.com/tutorials/libraries/) by [Daniel Shiffman](https://github.com/shiffman).


## Dependencies

- [Myo Connect v0.8.1](https://developer.thalmic.com/downloads)
- [Myo Firmware v1.1.5](https://developer.thalmic.com/downloads)


## Tested

System:

- **OSX** (*Mac OS X 10.7 and higher - tested with Mac OS X 10.10 Yosemite*)
- **Windows** (*not tested yet, but x86 and x64 should work*) (*Windows 7 and 8*)

Myo hardware device:

- **Myo Alpha**

Myo SDK version:

- **0.8.1**

Processing version:

- **3.0a4**
- **2.2.1**


## Examples

- [Basic Data-Access](#basic-data-access) → [e1_basic.pde](examples/e1_basic/e1_basic.pde)
- [Raw Data-Access (EMG)](#raw-data-access-emg) → [e2_emg_data.pde](examples/e2_emg_data/e2_emg_data.pde)
- Visual application → [e3_application.pde](examples/e3_application/e3_application.pde)

## Usage

- Run ```Guides/ Getting Started ...``` of **Myo Connect** to learn the different poses.
- Install the library to Processing (see [Installation](#installation)).
- Run the [example](#examples) and **have fun**.

### Basic Data-Access

```java
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

void myoOnArmSync(Myo myo, long timestamp, Arm arm) {
  println("Sketch: myoOnArmSync");

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
```

### Raw Data-Access (EMG)

```java
import de.voidplus.myo.*;

Myo myo;

void setup() {
  size(800, 500);
  background(255);
  // ...

  myo = new Myo(this);

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
```


## Questions?

Don't be shy and feel free to contact me on Twitter: [@darius_morawiec](https://twitter.com/darius_morawiec)


## License

The library is Open Source Software released under the [License](LICENSE).
