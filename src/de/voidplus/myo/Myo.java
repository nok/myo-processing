package de.voidplus.myo;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.enums.StreamEmgType;
import com.thalmic.myo.enums.XDirection;

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
 * 
 * @author Darius Morawiec
 * @version 0.8.1.5
 *
 */
public class Myo {

	
	// ================================================================================
	// 1 Properties
	// ================================================================================
	
	// Processing
	private PApplet parent;
	
	// Logging
	private static boolean verbose = false;
	private static int verboseLevel = 1;
	
	// Myo
	private de.voidplus.myo.Myo self;
	private ArrayList<Device> devices;
	private com.thalmic.myo.Hub hub;
	private int frequency;
	private DeviceListener collector;
	
	private final static String NAME = "Myo";
	private final static String VERSION = "0.8.1.5";
	private final static String MYO_SDK_VERSION = "0.8.1";
	private final static String MYO_FIRMWARE_VERSION = "1.1.755";
	private final static String MYO_FIRMWARE_VERSION_ALPHA = "1.1.5";
	private final static String REPOSITORY = "https://github.com/nok/myo-processing";
	
	
    //================================================================================
    // 2 Constructors
    //================================================================================
	
	public Myo(final PApplet parent) {
		PApplet.println("# "+Myo.NAME+" v"+Myo.VERSION+" - Support: Myo SDK v"+Myo.MYO_SDK_VERSION+", Firmware v"+Myo.MYO_FIRMWARE_VERSION+", Alpha Firmware v"+Myo.MYO_FIRMWARE_VERSION_ALPHA+" - "+Myo.REPOSITORY);
		self = this;
		
		// Processing
		this.parent = parent;
		parent.registerMethod("pre", this);
//		parent.registerMethod("post", this);
		parent.registerMethod("dispose", this);
		
		// Myo
		this.checkLibraryDependencies();
		this.devices = new ArrayList<Device>();
		this.hub = new com.thalmic.myo.Hub();
		this.setHubFrequency(30);
		this.setHubLockingPolicy(Myo.LockingPolicy.STANDARD);
		
		com.thalmic.myo.Myo myo = hub.waitForMyo(10000);
		if (myo == null) {
			throw new RuntimeException("Unable to find a Myo!");
		} else {
			this.addDevice(myo);
			new Thread() {
				public void run() {
					while (true) {
						hub.run(frequency);
					}
				}
			}.start();
			Myo.log("Connected to a Myo armband.");
		}
		
		this.collector = new Collector(self);
		this.hub.addListener(this.collector);
	}
	
	public void pre() {
		if (this.hasDevices()) {
			this.hub.runOnce(this.frequency);
		}
	}

	public void dispose() {
		if (this.hasDevices()) {
			this.hub.removeListener(this.collector);
		}
	}
	
	
    //================================================================================
    // 3 Dependencies
    //================================================================================
	
	private void checkLibraryDependencies() {
		// MAC
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			// Add 'libraries/macosx' path to the 'java.library.path' to load 'myo.framework' manually
			try {
				String pLibPath = new File(Myo.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI()).getParentFile()
						.toString()
						+ File.separator
						+ "macosx"
						+ File.separator;
				File pLibDir = new File(pLibPath);
				if (pLibDir.exists() && pLibDir.isDirectory()) {
					File myoLibFile = new File(pLibDir.getAbsoluteFile()
							+ File.separator + "myo.framework");
					if (myoLibFile.exists() && myoLibFile.isDirectory()) {
						String libPath = System
								.getProperty("java.library.path")
								+ ":"
								+ pLibDir.getAbsolutePath();
						System.setProperty("java.library.path", libPath);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		return deviceId > 0 && deviceId <= this.devices.size();
	}

	public ArrayList<Device> getDevices() {
		return this.devices;
	}

	public Device getDevice(int deviceId) {
		return this.devices.get(deviceId);
	}

	protected Device addDevice(com.thalmic.myo.Myo myo) {
		Device device = new Device(myo);
		Integer deviceId = device.setId(devices.size());
		this.devices.add(device);
		return this.devices.get(deviceId);
	}

	protected Device identifyDevice(com.thalmic.myo.Myo myo) {
		if (this.devices.isEmpty()) {
			return this.addDevice(myo);
		} else {
			for (int i = 0; i < this.devices.size(); i++) {
				if (this.devices.get(i).getMyo() == myo) {
					return this.devices.get(i);
				}
			}
			return this.addDevice(myo);
		}
	}
	
	
    //================================================================================
    // 5 Interfaces
    //================================================================================
	
	/**
	 * Intern method to route a specific callback with dynamic data.
	 * 
	 * @param object Object, which has to implement the method.
	 * @param method Name of method, which will be called.
	 * @param classes Array of classes, which the method has to implement as signature.
	 * @param objects Array of objects, which stores valuable data for callback.
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
				// e.printStackTrace();
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
	 * @param level Set the level of vibration duration [1,2,3].
	 * @return
	 */
	public Myo vibrate(int level) {
		this.vibrate(level, 0);
		return this;
	}

	/**
	 * The device vibrates with medium strength.
	 * @return
	 */
	public Myo vibrate() {
		return this.vibrate(2);
	}

	/**
	 * An myoOnRssi(Myo, long, int) event will likely be generated with the value of the RSSI.
	 * @param deviceId ID of the Myo.
	 * @return
	 */
	public Myo requestRssi(int deviceId) {
		if (this.hasDevice(deviceId)) {
			this.getDevice(deviceId).requestRssi();
		}
		return this;
	}
	
	/**
	 * An myoOnRssi(Myo, long, int) event will likely be generated with the value of the RSSI.
	 * @return
	 */
	public Myo requestRssi() {
		return this.requestRssi(0);
	}
	
	//--------------------------------------------------------------------------------
	// 6.2 Locking	

	/**
	 * Force the Myo locking immediately.
	 * @param deviceId
	 * @return
	 */
	public Myo lock(int deviceId) {
		if (this.hasDevice(deviceId)) {
			this.getDevice(deviceId).lock();
		}
		return this;
	}
	
	/**
	 * Force the Myo locking immediately.
	 * @return
	 */
	public Myo lock() {
		return this.lock(0);
	}

	/**
	 * Unlock the Myo.
	 * @param mode
	 * @param deviceId
	 * @return
	 */
	public Myo unlock(Unlock mode, int deviceId) {
		if (this.hasDevice(deviceId)) {
			this.getDevice(deviceId).unlock(mode);
		}
		return this;
	}

	/**
	 * Unlock the Myo.
	 * @param mode
	 * @return
	 */
	public Myo unlock(Unlock mode) {
		return this.unlock(mode, 0);
	}
	

    //================================================================================
    // 7 Getters
    //================================================================================
	
	//--------------------------------------------------------------------------------
	// 7.1 Raw or original objects
	
	/**
	 * Get access to the raw instance of class Myo.
	 * @return Active instance of class com.thalmic.myo.Myo.
	 */
	public com.thalmic.myo.Myo getRawMyo() {
		return this.getRawMyo(0);
	}
	
	/**
	 * Get access to the raw instance of a specific class Myo.
	 * @param deviceId
	 * @return
	 */
	public com.thalmic.myo.Myo getRawMyo(int deviceId) {
		return this.getDevice(deviceId).getMyo();
	}

	/**
	 * Get access to the raw instance of class Hub.
	 * @return Active instance of class com.thalmic.myo.Hub.
	 */
	public com.thalmic.myo.Hub getRawHub() {
		return this.hub;
	}
	
	//--------------------------------------------------------------------------------
	// 7.2 Device
	
	/**
	 * Get the firmware of specific device.
	 * @param deviceId
	 * @return
	 */
	public String getFirmware(int deviceId) {
		if(this.hasDevice(deviceId)){
			return this.getDevice(deviceId).getFirmware();
		}
		return "";
	}
	
	/**
	 * Get the firmware of device.
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
	 * @param deviceId
	 * @return Name of latest pose.
	 */
	public String getPose(int deviceId) {
		if (this.hasDevice(deviceId)) {
			return this.getDevice(deviceId).getPose();
		}
		return "";
	}

	/**
	 * Get the name of the latest pose.
	 * @return Name of latest pose.
	 */
	public String getPose() {
		return this.getPose(0);
	}
	
	//----------------------------------------
	// 7.3.2 Arm

	/**
	 * Get the type of recognized arm.
	 * @param deviceId
	 * @return
	 */
	public String getArm(int deviceId) {
		if (this.hasDevice(deviceId)) {
			return this.getDevice(deviceId).getArm();
		}
		return "";
	}

	/**
	 * Get the type of recognized arm.
	 * @return
	 */
	public String getArm() {
		return this.getArm(0);
	}

	/**
	 * Arm recognized?
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
	 * @return
	 */
	public Boolean hasArm() {
		return this.hasArm(0);
	}

	/**
	 * Left arm?
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
	 * @return
	 */
	public Boolean isArmLeft() {
		return this.hasArm(0);
	}

	/**
	 * Right arm?
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
	 * @return
	 */
	public Boolean isArmRight() {
		return this.hasArm(0);
	}
	
	//--------------------------------------------------------------------------------
	// 7.4 Sensors
	
	/**
	 * Get orientation values of device.
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
	 * @return Orientation as PVector, where 'x' is the 'roll' value, 'y' is the 'pitch' value and 'z' the 'yaw' value.
	 */
	public PVector getOrientation() {
		return this.getOrientation(0);
	}

	/**
	 * Get gyroscope values of device.
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
	 * @return
	 */
	public PVector getAccelerometer() {
		return this.getAccelerometer(0);
	}
	
	/**
	 * Get gyroscope values of device.
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
	 * @return
	 */
	public PVector getGyroscope() {
		return this.getGyroscope(0);
	}

	/**
	 * Get raw data of EMG sensors.
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
	 * @param frequency Time in milliseconds.
	 * @return
	 */
	public Myo setFrequency(int frequency) {
		this.frequency = frequency;
		return this;
	}
	
	/**
	 * Set the duration accessing data. 
	 * @param frequency
	 * @see setFrequency
	 * @return
	 */
	public Myo setHubFrequency(int frequency) {
		this.setFrequency(frequency);
		return this;
	}
	
	/**
	 * Set the locking policy for Myos connected to the Hub.
	 * @param policy
	 * @return
	 */
	public Myo setLockingPolicy(LockingPolicy policy) {
		switch (policy) {
		case NONE:
			this.hub.setLockingPolicy(com.thalmic.myo.enums.LockingPolicy.LOCKING_POLICY_NONE);
			break;
		case STANDARD:
		default:
			this.hub.setLockingPolicy(com.thalmic.myo.enums.LockingPolicy.LOCKING_POLICY_STANDARD);
			break;
		}
		return this;
	}

	/**
	 * Set the locking policy for Myos connected to the Hub.
	 * @param policy
	 * @see setLockingPolicy
	 * @return
	 */
	public Myo setHubLockingPolicy(LockingPolicy policy) {
		this.setLockingPolicy(policy);
		return this;
	}

	/**
	 * Enable EMG mode.
	 * @param deviceId
	 * @return
	 */
	public Myo withEmg(int deviceId) {
		if (this.hasDevice(deviceId)) {
			this.getDevice(deviceId).withEmg();
		}
		return this;
	}

	/**
	 * Enable EMG mode.
	 * @return
	 */
	public Myo withEmg() {
		return this.withEmg(0);
	}
	
	/**
	 * Disable EMG mode.
	 * @param deviceId
	 * @return
	 */
	public Myo withoutEmg(int deviceId) {
		if (this.hasDevice(deviceId)) {
			this.getDevice(deviceId).withoutEmg();
		}
		return this;
	}

	/**
	 * Disable EMG mode.
	 * @return
	 */
	public Myo withoutEmg() {
		return this.withEmg(0);
	}
	
	//========================================
	
	/**
	 * Set the firmware of device.
	 * @param firmwareVersion
	 * @param deviceId
	 * @return
	 */
	protected Myo setFirmware(FirmwareVersion firmwareVersion, int deviceId) {
		if (this.hasDevice(deviceId)) {
			this.getDevice(deviceId).setFirmware(firmwareVersion);
		}
		return this;
	}

	/**
	 * Set the firmware of device.
	 * @param firmwareVersion
	 * @return
	 */
	protected Myo setFirmware(FirmwareVersion firmwareVersion) {
		return this.setFirmware(firmwareVersion, 0);
	}
	
	
    //================================================================================
    // 9 Enums
    //================================================================================
	
	public enum Event {
		PAIR, UNPAIR, CONNECT, DISCONNECT, ARM_SYNC, ARM_UNSYNC, POSE, ORIENTATION, ACCELEROMETER, GYROSCOPE, RSSI, EMG, LOCK, UNLOCK
	}

	public enum LockingPolicy {
		NONE, STANDARD
	}

	public enum Unlock {
		HOLD, TIMED
	}
	
	
    //================================================================================
    // 10 Verbose & logging
    //================================================================================

	/**
	 * Print debug information to the console. 
	 * @param 	verbose
	 * @return
	 */
	public Myo setVerbose(boolean verbose) {
		Myo.verbose = verbose;
		return this;
	}
	
	/**
	 * Set the level of the log level. 
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
	 * @param message Set the readable message of that log.
	 * @param verboseLevel Set the priority level of that log.
	 * @return
	 */
	protected static void log(String message, int verboseLevel) {
		if (Myo.verbose == true && verboseLevel <= Myo.verboseLevel) {
			PApplet.println("# " + Myo.NAME + ": LOG (" + verboseLevel + "): " + message);
		}
	}

	/**
	 * Print log messages to the console.
	 * @param message
	 */
	protected static void log(String message) {
		Myo.log(message, 1);
	}

}
