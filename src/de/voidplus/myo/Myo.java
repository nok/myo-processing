package de.voidplus.myo;


import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import com.thalmic.myo.DeviceListener;
import processing.core.PApplet;
import processing.core.PVector;


//=====================================================================================
// Table of Content
//=====================================================================================
//
// 1 Properties
// 2 Constructors
// 3 Dependencies
// 4 Device handling
// 5 Interfaces
// 6 Commands
//   6.1 Hardware
//   6.2 Locking
// 7 Getters
//   7.1 Raw or original objects
//   7.2 Device
//   7.3 Objects
//     7.3.1 Pose
//     7.3.2 Arm
//   7.4 Sensors
// 8 Setters
//   8.1 Settings
// 9 Enums
// 10 Verbose & logging


/**
 * Myo for Processing
 *
 * @author Darius Morawiec
 * @version 0.9.0.3
 */
public class Myo {


    // ================================================================================
    // 1 Properties
    // ================================================================================

    private final static String NAME = "Myo for Processing";
    private final static String VERSION = "0.9.0.3";
    private final static String MYO_SDK_VERSION = "0.9.0";
    private final static String MYO_FIRMWARE_VERSION = "1.5.1970";
    private final static String REPOSITORY = "https://github.com/nok/myo-processing";

    // Processing:
    private PApplet parent;

    // Global flags:
    private static boolean verbose = false;
    private static int verboseLevel = 1;

    // Myo:
    protected ArrayList<Device> devices;
    protected com.thalmic.myo.Hub _hub;
    protected DeviceListener _collector;
    protected Thread thread;
    protected int frequency;
    protected boolean withEmg;


    //================================================================================
    // 2 Constructors
    //================================================================================

    /**
     * Myo constructor to initialize the controller and listener.
     *
     * @param parent Instance (this) of the used sketch
     */
    public Myo(final PApplet parent, boolean withEmg) {
        this.println(
                String.format("# %s v%s - Myo SDK v%s, firmware v%s - %s",
                        Myo.NAME,
                        Myo.VERSION,
                        Myo.MYO_SDK_VERSION,
                        Myo.MYO_FIRMWARE_VERSION,
                        Myo.REPOSITORY
                ), false
        );

        // Processing:
        this.parent = parent;
        this.withEmg = withEmg;
        this.parent.registerMethod("dispose", this);

        // Dependencies:
        this.checkLibraryDependencies();

        // Myo:
        this.devices = new ArrayList<Device>();
        this._hub = new com.thalmic.myo.Hub(this.getClass().getName() + ".java");
        this.setHubLockingPolicy(Myo.LockingPolicy.STANDARD);
        this.setHubFrequency(10);
        this._collector = new Collector(this);
        this._hub.addListener(this._collector);
        this.thread = new Thread() {
            public void run() {
                while (true) {
                    _hub.run(frequency);
                }
            }
        };
        this.thread.start();
    }

    public Myo(final PApplet parent) {
        this(parent, false);
    }

    public void dispose() {
        if (this.thread.isAlive()) {
            this.thread.interrupt();
        }
        this._hub.removeListener(this._collector);
    }


    //================================================================================
    // 3 Dependencies
    //================================================================================

    private void checkLibraryDependencies() {
        // MAC
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            try {
                System.loadLibrary("myo");
            } catch (UnsatisfiedLinkError linkEx) {
                try {
                    String pathOfDepends = null;
                    URI uri = null;
                    try {
                        uri = Myo.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                    } catch (URISyntaxException e) {
                        // e.printStackTrace();
                        // TODO: RM PATH
                        // pathOfDepends = "/Users/darius/code/java/workspaces/idea/myo-processing/lib/macosx";
                    }
                    if (uri != null) {
                        pathOfDepends = new File(uri).getParentFile().toString() + File.separator + "macosx" + File.separator;
                    }
                    if (pathOfDepends != null) {
                        File dirOfDepends = new File(pathOfDepends);
                        if (dirOfDepends.exists() && dirOfDepends.isDirectory()) {
                            System.setProperty("java.library.path", dirOfDepends.getAbsolutePath());
                            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                            fieldSysPath.setAccessible(true);
                            fieldSysPath.set(null, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //================================================================================
    // 4 Device handling
    //================================================================================

    public boolean hasDevices() {
        return !this.devices.isEmpty();
    }

    public boolean hasDevice(int deviceId) {
        for (Device device : this.devices) {
            if (device.getId() == deviceId) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Device> getDevices() {
        return this.devices;
    }

    public Device getDevice(int deviceId) {
        return this.devices.get(deviceId);
    }


    //================================================================================
    // 5 Interfaces
    //================================================================================

    /**
     * Intern method to route a specific callback with dynamic data.
     *
     * @param object     Object, which has to implement the method.
     * @param methodName Name of method, which will be called.
     * @param classes
     * @param objects    Array of objects, which stores valuable data for callback.
     * @param logLevel
     */
    protected void dispatch(Object object, String methodName, Class[] classes, Object[] objects, int logLevel) {
        boolean success = false;
        if (methodName == null) {
            methodName = "myoOn";
        }
        if (classes.length == objects.length) {
            try {
                object.getClass().getMethod(
                        methodName,
                        classes
                ).invoke(
                        this.parent,
                        objects
                );
                success = true;
            } catch (Exception e) {
//				e.printStackTrace();
//              PApplet.println(e.getMessage());
            } finally {
                if (success) {
                    Myo.log("Method: " + methodName + "(...); has been called.", logLevel);
                }
            }
        }
    }

    // For example: "myOnPair"
    protected void dispatch(String methodName, Class[] classes, Object[] objects, int logLevel) {
        this.dispatch(this.parent, methodName, classes, objects, logLevel);
    }

    protected void dispatch(String methodName, Class[] classes, Object[] objects) {
        this.dispatch(this.parent, methodName, classes, objects, 1);
    }

    // Always: "myOn"
    protected void dispatch(Class[] classes, Object[] objects, int logLevel) {
        this.dispatch(this.parent, null, classes, objects, logLevel);
    }

    protected void dispatch(Class[] classes, Object[] objects) {
        this.dispatch(this.parent, null, classes, objects, 1);
    }


    //================================================================================
    // 6 Commands
    //================================================================================

    //--------------------------------------------------------------------------------
    // 6.1 Hardware

    /**
     * The device vibrates.
     *
     * @param level
     * @param deviceId
     * @return
     */
    public Myo vibrate(int level, int deviceId) {
        if (this.hasDevice(deviceId)) {
            this.getDevice(deviceId).vibrate(level);
        }
        return this;
    }

    /**
     * The device vibrates.
     *
     * @param level Set the level of vibration duration [1,2,3].
     * @return
     */
    public Myo vibrate(int level) {
        for (Device device : this.devices) {
            device.vibrate(level);
        }
        return this;
    }

    /**
     * The device vibrates with medium strength.
     *
     * @return
     */
    public Myo vibrate() {
        for (Device device : this.devices) {
            device.vibrate(2);
        }
        return this;
    }

    /**
     * An myoOnRssi(Myo, long, int) event will likely be generated with the value of the RSSI.
     *
     * @param deviceId ID of the Myo.
     * @return
     */
    public Myo requestRssi(int deviceId) {
        if (this.hasDevice(deviceId)) {
            this.devices.get(deviceId).requestRssi();
        }
        return this;
    }

    /**
     * An myoOnRssi(Myo, long, int) event will likely be generated with the value of the RSSI.
     *
     * @return
     */
    public Myo requestRssi() {
        for (Device device : this.devices) {
            device.requestRssi();
        }
        return this;
    }

    public Myo requestBatteryLevel(int deviceId) {
        if (this.hasDevice(deviceId)) {
            this.devices.get(deviceId).requestBatteryLevel();
        }
        return this;
    }

    public Myo requestBatteryLevel() {
        for (Device device : this.devices) {
            device.requestBatteryLevel();
        }
        return this;
    }

    //--------------------------------------------------------------------------------
    // 6.2 Locking

    /**
     * Force the Myo locking immediately.
     *
     * @param deviceId
     * @return
     */
    public Myo lock(int deviceId) {
        if (deviceId < this.devices.size()) {
            this.devices.get(deviceId).lock();
        }
        return this;
    }

    /**
     * Force the Myo locking immediately.
     *
     * @return
     */
    public Myo lock() {
        for (Device device : this.devices) {
            device.lock();
        }
        return this;
    }

    /**
     * Unlock the Myo.
     *
     * @param mode
     * @param deviceId
     * @return
     */
    public Myo unlock(Unlock mode, int deviceId) {
        if (deviceId < this.devices.size()) {
            this.devices.get(deviceId).unlock(mode);
        }
        return this;
    }

    /**
     * Unlock the Myo.
     *
     * @param mode
     * @return
     */
    public Myo unlock(Unlock mode) {
        for (Device device : this.devices) {
            device.unlock(mode);
        }
        return this;
    }


    //================================================================================
    // 7 Getters
    //================================================================================

    //--------------------------------------------------------------------------------
    // 7.1 Raw or original objects

    /**
     * Get access to the raw instance of class Myo.
     *
     * @return Active instance of class com.thalmic.myo.Myo.
     */
    public com.thalmic.myo.Myo getRawMyo() {
        return this.getRawMyo(0);
    }

    /**
     * Get access to the raw instance of a specific class Myo.
     *
     * @param deviceId
     * @return
     */
    public com.thalmic.myo.Myo getRawMyo(int deviceId) {
        return this.getDevice(deviceId).getMyo();
    }

    /**
     * Get access to the raw instance of class Hub.
     *
     * @return Active instance of class com.thalmic.myo.Hub.
     */
    public com.thalmic.myo.Hub getRawHub() {
        return this._hub;
    }

    //--------------------------------------------------------------------------------
    // 7.2 Device

    /**
     * Get the firmware of specific device.
     *
     * @param deviceId
     * @return
     */
    public String getFirmware(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getFirmware();
        }
        return "";
    }

    /**
     * Get the firmware of device.
     *
     * @return
     */
    public String getFirmware() {
        return this.getFirmware(0);
    }

    //--------------------------------------------------------------------------------
    // 7.3 Objects

    //----------------------------------------
    // 7.3.1 Pose

    /**
     * Get the name of the latest pose.
     *
     * @param deviceId
     * @return Name of latest pose.
     */
    public Pose getPose(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getPose();
        }
        return null;
    }

    /**
     * Get the name of the latest pose.
     *
     * @return Name of latest pose.
     */
    public Pose getPose() {
        return this.getPose(0);
    }

    //----------------------------------------
    // 7.3.2 Arm

    /**
     * Get the type of recognized arm.
     *
     * @param deviceId
     * @return
     */
    public Arm getArm(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getArm();
        }
        return null;
    }

    /**
     * Get the type of recognized arm.
     *
     * @return
     */
    public Arm getArm() {
        return this.getArm(0);
    }

    /**
     * Arm recognized?
     *
     * @param deviceId
     * @return
     */
    public Boolean hasArm(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).hasArm();
        }
        return false;
    }

    /**
     * Arm recognized?
     *
     * @return
     */
    public Boolean hasArm() {
        return this.hasArm(0);
    }

    /**
     * Left arm?
     *
     * @param deviceId
     * @return
     */
    public Boolean isArmLeft(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).isArmLeft();
        }
        return null;
    }

    /**
     * Left arm?
     *
     * @return
     */
    public Boolean isArmLeft() {
        return this.isArmLeft(0);
    }

    /**
     * Right arm?
     *
     * @param deviceId
     * @return
     */
    public Boolean isArmRight(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).isArmRight();
        }
        return null;
    }

    /**
     * Right arm?
     *
     * @return
     */
    public Boolean isArmRight() {
        return this.isArmRight(0);
    }

    //--------------------------------------------------------------------------------
    // 7.4 Sensors

    /**
     * Get orientation values of device.
     *
     * @param deviceId
     * @return Orientation as PVector, where 'x' is the 'roll' value, 'y' is the 'pitch' value and 'z' the 'yaw' value.
     */
    public PVector getOrientation(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getOrientation();
        }
        return null;
    }

    /**
     * Get orientation values of device.
     *
     * @return Orientation as PVector, where 'x' is the 'roll' value, 'y' is the 'pitch' value and 'z' the 'yaw' value.
     */
    public PVector getOrientation() {
        return this.getOrientation(0);
    }

    /**
     * Get gyroscope values of device.
     *
     * @param deviceId
     * @return
     */
    public PVector getAccelerometer(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getAccelerometer();
        }
        return null;
    }

    /**
     * Get gyroscope values of device.
     *
     * @return
     */
    public PVector getAccelerometer() {
        return this.getAccelerometer(0);
    }

    /**
     * Get gyroscope values of device.
     *
     * @param deviceId
     * @return
     */
    public PVector getGyroscope(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getGyroscope();
        }
        return null;
    }

    /**
     * Get gyroscope values of device.
     *
     * @return
     */
    public PVector getGyroscope() {
        return this.getGyroscope(0);
    }

    /**
     * Get raw data of EMG sensors.
     *
     * @param deviceId
     * @return
     */
    public int[] getEmg(int deviceId) {
        if (this.hasDevice(deviceId)) {
            return this.getDevice(deviceId).getEmg();
        }
        return null;
    }

    /**
     * Get raw data of EMG sensors.
     *
     * @return
     */
    public int[] getEmg() {
        return this.getEmg(0);
    }


    //================================================================================
    // 8 Setters
    //================================================================================

    //--------------------------------------------------------------------------------
    // 8.1 Settings

    /**
     * Set the duration accessing data.
     *
     * @param frequency Time in milliseconds.
     * @return
     */
    public Myo setFrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    /**
     * Set the duration accessing data.
     *
     * @param frequency
     * @return
     */
    public Myo setHubFrequency(int frequency) {
        this.setFrequency(frequency);
        return this;
    }

    /**
     * Set the locking policy for Myos connected to the Hub.
     *
     * @param policy
     * @return
     */
    public Myo setLockingPolicy(LockingPolicy policy) {
        switch (policy) {
            case NONE:
                this._hub.setLockingPolicy(com.thalmic.myo.enums.LockingPolicy.LOCKING_POLICY_NONE);
                break;
            case STANDARD:
            default:
                this._hub.setLockingPolicy(com.thalmic.myo.enums.LockingPolicy.LOCKING_POLICY_STANDARD);
                break;
        }
        return this;
    }

    /**
     * Set the locking policy for Myos connected to the Hub.
     *
     * @param policy
     * @return
     */
    public Myo setHubLockingPolicy(LockingPolicy policy) {
        this.setLockingPolicy(policy);
        return this;
    }

    /**
     * Enable EMG mode.
     *
     * @return
     */
    public Myo withEmg() {
        this.withEmg = true;
        for(Device device : this.devices){
            device.withEmg();
        }
        return this;
    }

    /**
     * Disable EMG mode.
     *
     * @return
     */
    public Myo withoutEmg() {
        for(Device device : this.devices){
            device.withoutEmg();
        }
        this.withEmg = false;
        return this;
    }


    //================================================================================
    // 9 Enums
    //================================================================================

    public enum Event {
        PAIR,
        UNPAIR,
        CONNECT,
        DISCONNECT,
        ARM_SYNC,
        ARM_UNSYNC,
        WARMUP_COMPLETED,
        POSE,
        LOCK,
        UNLOCK,
        RSSI,
        BATTERY_LEVEL,
        ORIENTATION_DATA,
        ACCELEROMETER_DATA,
        GYROSCOPE_DATA,
        EMG_DATA;
    }

    public enum LockingPolicy {
        NONE,
        STANDARD
    }

    public enum Unlock {
        HOLD,
        TIMED
    }


    //================================================================================
    // 10 Verbose & logging
    //================================================================================

    /**
     * Print debug information to the console.
     *
     * @param verbose
     * @return
     */
    public Myo setVerbose(boolean verbose) {
        Myo.verbose = verbose;
        return this;
    }

    /**
     * Set the level of the log level.
     *
     * @param level Set the level of the log level [1,2,3]. Three (3) will print lightweight events, too.
     */
    public Myo setVerboseLevel(int level) {
        if (level > 0 && level < 4) {
            Myo.verboseLevel = level;
        } else {
            Myo.verboseLevel = 1;
        }
        return this;
    }

    /**
     * Print log messages to the console.
     *
     * @param message      Set the readable message of that log.
     * @param verboseLevel Set the priority level of that log.
     * @return
     */
    protected static void log(String message, int verboseLevel) {
        if (Myo.verbose && verboseLevel <= Myo.verboseLevel) {
            PApplet.println("# " + Myo.NAME + ": LOG (" + verboseLevel + "): " + message);
        }
    }

    /**
     * Print log messages to the console.
     *
     * @param message
     */
    protected static void log(String message) {
        Myo.log(message, 1);
    }

    /**
     * Log message to console.
     *
     * @param msg Message
     * @param ns  Namespace
     */
    private void println(String msg, boolean ns) {
        if (ns) {
            PApplet.println(String.format("# %s: %s", Myo.NAME, msg));
        } else {
            PApplet.println(msg);
        }
    }

    /**
     * Print message to console.
     *
     * @param msg Message
     */
    private void println(String msg) {
        this.println(msg, true);
    }

}
