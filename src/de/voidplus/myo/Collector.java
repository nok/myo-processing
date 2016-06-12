package de.voidplus.myo;


import com.thalmic.myo.*;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.WarmupResult;
import com.thalmic.myo.enums.WarmupState;
import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;
import de.voidplus.myo.Myo.Event;


//=====================================================================================
// Table of Content
//=====================================================================================
//
// 1 Properties
// 2 Constructors
// 3 Callbacks

/**
 * Internal class which organize the event handling.
 */
public class Collector implements DeviceListener {


    //=================================================================================
    // 1 Properties
    //=================================================================================

    private de.voidplus.myo.Myo main;


    //=================================================================================
    // 2 Constructors
    //=================================================================================

    public Collector(de.voidplus.myo.Myo main) {
        this.main = main;
    }

    private Device identifyDevice(com.thalmic.myo.Myo _myo) {
        if (this.main.devices.isEmpty()) {
            Device device = new Device(_myo, 0);
            if (this.main.withEmg) {
                device.withEmg();
            }
            this.main.devices.add(device);
            return device;
        } else {
            for (Device device : this.main.devices) {
                if (device.getMyo() == _myo){
                    return device;
                }
            }
        }
        Device device = new Device(_myo, this.main.devices.size());
        if (this.main.withEmg) {
            device.withEmg();
        }
        this.main.devices.add(device);
        return device;
    }


    //=================================================================================
    // 3 Callbacks
    //=================================================================================

    //---------------------------------------------------------------------------------
    // Application lifecycle:

    @Override
    public void onPair(Myo _myo, long _timestamp, FirmwareVersion _firmwareVersion) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);
            device.setFirmware(_firmwareVersion);

            int log = 1;
            this.main.dispatch("myoOnPair",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.PAIR, device, _timestamp}, log);
        }
    }

    @Override
    public void onUnpair(Myo _myo, long _timestamp) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            int log = 1;
            this.main.dispatch("myoOnUnpair",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.UNPAIR, device, _timestamp}, log);
        }
    }

    @Override
    public void onConnect(Myo _myo, long _timestamp, FirmwareVersion _firmwareVersion) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);
            device.setFirmware(_firmwareVersion);

            int log = 1;
            this.main.dispatch("myoOnConnect",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.CONNECT, device, _timestamp}, log);
        }
    }

    @Override
    public void onDisconnect(Myo _myo, long _timestamp) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            int log = 1;
            this.main.dispatch("myoOnDisconnect",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.DISCONNECT, device, _timestamp}, log);
        }
    }

    @Override
    public void onWarmupCompleted(Myo _myo, long _timestamp, WarmupResult _warmupResult) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            int log = 1;
            this.main.dispatch("myoOnWarmupCompleted",
                    new Class[]{device.getClass(), long.class, int[].class},
                    new Object[]{device, _timestamp, device.emg}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.WARMUP_COMPLETED, device, _timestamp}, log);
        }
    }

    @Override
    public void onArmSync(Myo _myo, long _timestamp, Arm _arm, XDirection _xDirection, float _v, WarmupState _warmupState) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            if (device.arm.type.asRaw() != _arm) {
                switch (_arm) {
                    case ARM_LEFT:
                        device.arm.type = de.voidplus.myo.Arm.Type.LEFT;
                        break;
                    case ARM_RIGHT:
                        device.arm.type = de.voidplus.myo.Arm.Type.RIGHT;
                        break;
                    case ARM_UNKNOWN:
                        device.arm.type = de.voidplus.myo.Arm.Type.UNKNOWN;
                        break;
                }
            }

            int log = 1;
            this.main.dispatch("myoOnArmSync",
                    new Class[]{device.getClass(), long.class, de.voidplus.myo.Arm.class},
                    new Object[]{device, _timestamp, device.getArm()}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.ARM_SYNC, device, _timestamp}, log);
        }
    }

    @Override
    public void onArmUnsync(Myo _myo, long _timestamp) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            int log = 1;
            this.main.dispatch("myoOnArmUnsync",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.ARM_UNSYNC, device, _timestamp}, log);
        }
    }

    @Override
    public void onLock(Myo _myo, long _timestamp) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            int log = 2;
            this.main.dispatch("myoOnLock",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.LOCK, device, _timestamp}, log);
        }
    }

    @Override
    public void onUnlock(Myo _myo, long _timestamp) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            int log = 2;
            this.main.dispatch("myoOnUnlock",
                    new Class[]{device.getClass(), long.class},
                    new Object[]{device, _timestamp}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.UNLOCK, device, _timestamp}, log);
        }
    }


    //---------------------------------------------------------------------------------
    // Gestures or poses:

    @Override
    public void onPose(Myo _myo, long _timestamp, com.thalmic.myo.Pose _pose) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            if (_pose.getType() != com.thalmic.myo.enums.PoseType.UNKNOWN) {
                if (device.pose.type.asRaw() != _pose.getType()) {
                    switch (_pose.getType()) {
                        case REST:
                            device.pose.type = de.voidplus.myo.Pose.Type.REST;
                            break;
                        case FIST:
                            device.pose.type = de.voidplus.myo.Pose.Type.FIST;
                            break;
                        case WAVE_IN:
                            device.pose.type = de.voidplus.myo.Pose.Type.WAVE_IN;
                            break;
                        case WAVE_OUT:
                            device.pose.type = de.voidplus.myo.Pose.Type.WAVE_OUT;
                            break;
                        case FINGERS_SPREAD:
                            device.pose.type = de.voidplus.myo.Pose.Type.FINGERS_SPREAD;
                            break;
                        case DOUBLE_TAP:
                            device.pose.type = de.voidplus.myo.Pose.Type.DOUBLE_TAP;
                            break;
                    }
                }
            }

            int log = 2;
            this.main.dispatch("myoOnPose",
                    new Class[]{device.getClass(), long.class, device.pose.getClass()},
                    new Object[]{device, _timestamp, device.pose}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.POSE, device, _timestamp}, log);
        }
    }


    //---------------------------------------------------------------------------------
    // Additional information:

    @Override
    public void onRssi(Myo _myo, long _timestamp, int _rssi) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            device.rssi = _rssi;

            int log = 2;
            this.main.dispatch("myoOnRssi",
                    new Class[]{device.getClass(), long.class, int.class},
                    new Object[]{device, _timestamp, device.getRssi()}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.RSSI, device, _timestamp}, log);
        }
    }

    @Override
    public void onBatteryLevelReceived(Myo _myo, long _timestamp, int _level) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            device.batteryLevel = _level;

            int log = 2;
            this.main.dispatch("myoOnBatteryLevelReceived",
                    new Class[]{device.getClass(), long.class, int.class},
                    new Object[]{device, _timestamp, device.getBatteryLevel()}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.BATTERY_LEVEL, device, _timestamp}, log);
        }
    }


    //---------------------------------------------------------------------------------
    // Data streams:

    @Override
    public void onOrientationData(Myo _myo, long _timestamp, Quaternion _rotation) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            Quaternion normalized = _rotation.normalized();
            double roll = Math.atan2(2.0f * (normalized.getW() * normalized.getX() + normalized.getY() * normalized.getZ()), 1.0f - 2.0f * (normalized.getX() * normalized.getX() + normalized.getY() * normalized.getY()));
            double pitch = Math.asin(2.0f * (normalized.getW() * normalized.getY() - normalized.getZ() * normalized.getX()));
            double yaw = Math.atan2(2.0f * (normalized.getW() * normalized.getZ() + normalized.getX() * normalized.getY()), 1.0f - 2.0f * (normalized.getY() * normalized.getY() + normalized.getZ() * normalized.getZ()));

            device.orientation.x = (float) ((roll + Math.PI) / (Math.PI * 2.0));
            device.orientation.y = (float) ((pitch + Math.PI / 2.0) / Math.PI);
            device.orientation.z = (float) ((yaw + Math.PI) / (Math.PI * 2.0));

            int log = 3;
            this.main.dispatch("myoOnOrientationData",
                    new Class[]{device.getClass(), long.class, processing.core.PVector.class},
                    new Object[]{device, _timestamp, device.getOrientation()}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.ORIENTATION_DATA, device, _timestamp}, log);
        }
    }

    @Override
    public void onAccelerometerData(Myo _myo, long _timestamp, Vector3 _accelerometer) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            device.accelerometer.x = (float) _accelerometer.getX();
            device.accelerometer.y = (float) _accelerometer.getY();
            device.accelerometer.z = (float) _accelerometer.getZ();

            int log = 3;
            this.main.dispatch("myoOnAccelerometerData",
                    new Class[]{device.getClass(), long.class, processing.core.PVector.class},
                    new Object[]{device, _timestamp, device.getAccelerometer()}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.ACCELEROMETER_DATA, device, _timestamp}, log);
        }
    }

    @Override
    public void onGyroscopeData(Myo _myo, long _timestamp, Vector3 _gyroscope) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            device.gyroscope.x = (float) _gyroscope.getX();
            device.gyroscope.y = (float) _gyroscope.getY();
            device.gyroscope.z = (float) _gyroscope.getZ();

            int log = 3;
            this.main.dispatch("myoOnGyroscopeData",
                    new Class[]{device.getClass(), long.class, processing.core.PVector.class},
                    new Object[]{device, _timestamp, device.getGyroscope()}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.GYROSCOPE_DATA, device, _timestamp}, log);
        }
    }

    @Override
    public void onEmgData(Myo _myo, long _timestamp, byte[] _data) {
        if (_myo != null) {
            Device device = identifyDevice(_myo);

            if (device.withEmg && _data != null) {
                for (int i = 0; i < 8; i++) {
                    device.emg[i] = _data[i];
                }
            }

            int log = 3;
            this.main.dispatch("myoOnEmgData",
                    new Class[]{device.getClass(), long.class, int[].class},
                    new Object[]{device, _timestamp, device.emg}, log);
            this.main.dispatch(
                    new Class[]{Event.class, device.getClass(), long.class},
                    new Object[]{Event.EMG_DATA, device, _timestamp}, log);
        }
    }

}