[![Join the chat at https://gitter.im/nok/myo-processing](https://badges.gitter.im/nok/myo-processing.svg)](https://gitter.im/nok/myo-processing?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/nok/myo-processing/master/LICENSE)

---

![Myo for Processing](reference/github_cover.png)

===

Contributed library to use the [Myo](https://www.thalmic.com) in [Processing](http://processing.org/).


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

- [Myo for Processing v0.9.0.2](download/MyoForProcessing.zip?raw=true)


## Installation

Unzip and put the extracted *MyoForProcessing* folder into the libraries directory of your Processing sketches. The References and examples are stored in the *MyoForProcessing* directory. For more help read the [tutorial](http://www.learningprocessing.com/tutorials/libraries/) by [Daniel Shiffman](https://github.com/shiffman).


## Dependencies

- [Myo Connect v0.9.0](https://developer.thalmic.com/downloads)
- [Myo Firmware v1.5.1970](https://developer.thalmic.com/downloads)


## Tested

System:

- **OSX** (*Mac OS X 10.7 and higher - tested with Mac OS X 10.10 Yosemite*)
- **Windows** (*not tested yet, but x86 and x64 should work*) (*Windows 7 and 8*)

Myo SDK version:

- **0.9.0**

Processing version:

- **3.1.1**
- 3.0.2
- 3.0a4


## Examples

- Application → [Myo_1_Application.pde](examples/Myo_1_Application/Myo_1_Application.pde)
- [Callbacks of events](#basics) → [Myo_2_Callbacks.pde](examples/Myo_2_Callbacks/Myo_2_Callbacks.pde)
- EMG data streams → [Myo_3_EMG_Data.pde](examples/Myo_3_EMG_Data/Myo_3_EMG_Data.pde)
- Multiple devices → [Myo_4_Multiple.pde](examples/Myo_4_Multiple/Myo_4_Multiple.pde)

Here you see a screenshot of the viewable example [Myo_1_Application.pde](examples/Myo_1_Application/Myo_1_Application.pde):

![Application](reference/example_application.png)


## Basics

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
  println("Sketch: myoOnPair & Device: " + myo.getId());
}

void myoOnUnpair(Myo myo, long timestamp) {
  println("Sketch: myoOnUnpair & Device: " + myo.getId());
}

void myoOnConnect(Myo myo, long timestamp, String firmware) {
  println("Sketch: myoOnConnect & Device: " + myo.getId());
}

void myoOnDisconnect(Myo myo, long timestamp) {
  println("Sketch: myoOnDisconnect & Device: " + myo.getId());
}

void myoOnArmSync(Myo myo, long timestamp, Arm arm) {
  println("Sketch: myoOnArmSync & Device: " + myo.getId());

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
  println("Sketch: myoOnLock & Device: " + myo.getId());
}

void myoOnUnLock(Myo myo, long timestamp){
  println("Sketch: myoOnUnLock & Device: " + myo.getId());
}

void myoOnArmUnsync(Myo myo, long timestamp) {
  println("Sketch: myoOnArmUnsync & Device: " + myo.getId());
}

void myoOnPose(Myo myo, long timestamp, Pose pose) {
  println("Sketch: myoOnPose & Device: " + myo.getId());
  
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
  // println("Sketch: myoOnOrientation & Device: " + myo.getId());
}

void myoOnAccelerometer(Myo myo, long timestamp, PVector accelerometer) {
  // println("Sketch: myoOnAccelerometer & Device: " + myo.getId());
}

void myoOnGyroscope(Myo myo, long timestamp, PVector gyroscope) {
  // println("Sketch: myoOnGyroscope & Device: " + myo.getId());
}

void myoOnRssi(Myo myo, long timestamp, int rssi) {
  println("Sketch: myoOnRssi & Device: " + myo.getId());
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


## Questions?

Don't be shy and feel free to contact me on [Twitter](https://twitter.com/darius_morawiec) or [Gitter](https://gitter.im/nok/myo-processing).


## License

The library is Open Source Software released under the [license](LICENSE).
