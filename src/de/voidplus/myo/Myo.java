package de.voidplus.myo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.enums.StreamEmgType;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * 
 * @author Darius Morawiec
 * @version 0.8.1.2
 *
 */
public class Myo {
	
	// Processing
	private PApplet parent;
//	private PrintStream stream;
	
	// Logging
	private boolean verbose;
	private int verboseLevel;
	
	// Myo-Lifecycle
	private com.thalmic.myo.Myo myo;
	private com.thalmic.myo.Hub hub;
	private Collector collector;
	private int frequency;
	
	// Myo-Basics
	private String firmware;
	protected Arm arm;
	protected Pose pose;
	protected LockingPolicy lockingPolicy;
	
	protected PVector orientation, accelerometer, gyroscope;
	protected int rssi;
	protected int[] emg;
	protected boolean withEmg;
	
	private final static String NAME = "Myo";
	private final static String VERSION = "0.8.1.2";
	private final static String MYO_SDK_VERSION = "0.8.1";
	private final static String MYO_FIRMWARE_VERSION = "1.1.755";
	private final static String MYO_FIRMWARE_VERSION_ALPHA = "1.1.5";
	private final static String REPOSITORY = "https://github.com/nok/myo-processing";
	
	public Myo(PApplet parent) {
		PApplet.println("# "+Myo.NAME+" v"+Myo.VERSION+" - Support: Myo SDK v"+Myo.MYO_SDK_VERSION+", Firmware v"+Myo.MYO_FIRMWARE_VERSION+", Alpha Firmware v"+Myo.MYO_FIRMWARE_VERSION_ALPHA+" - "+Myo.REPOSITORY);
		this.checkDependencies();
		
		parent.registerMethod("pre", this);
//		parent.registerMethod("post", this);
		parent.registerMethod("dispose", this);
		
		this.parent = parent;
		this.setVerbose(false)
			.setVerboseLevel(1)
			.setFrequency(30);

		this.hub = new com.thalmic.myo.Hub();
		this.myo = hub.waitForMyo(10000);
		
//		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
//			System.setErr(stream);
//		}
		
		if (this.myo == null) {
			throw new RuntimeException("Unable to find a Myo!");
		}
		this.log("Connected to a Myo armband.");
		
		if (this.myo != null) {
			new Thread() {
				public void run() {
					while (true) {
						hub.run(frequency);
					}
				}
			}.start();
		}
		
		this.collector = new Collector(this);
		this.hub.addListener(this.collector);
		this.withEmg = false;

		this.arm = new Arm();
		this.pose = new Pose();
		this.setLockingPolicy(Myo.LockingPolicy.STANDARD);
		this.orientation = new PVector();
		this.accelerometer = new PVector();
		this.gyroscope = new PVector();
	}

	
	// ------------------------------------------------------------------------------
	// Dependencies
	
	private void checkDependencies() {
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
	
	
	// ------------------------------------------------------------------------------
	// Lifecycle of PApplet sketch
	
	public void pre(){
		if (this.myo != null) {
			this.hub.runOnce(this.frequency);
		}
	}
	
	public void dispose(){
		if (this.myo != null) {
			this.hub.removeListener(this.collector);
		}
	}


	// ------------------------------------------------------------------------------
	// Raw access
	
	/**
	 * Get access to the raw instance of class Myo.
	 * @return Active instance of class com.thalmic.myo.Myo.
	 */
	public com.thalmic.myo.Myo getRawMyo() {
		return this.myo;
	}

	/**
	 * Get access to the raw instance of class Hub.
	 * @return Active instance of class com.thalmic.myo.Hub.
	 */
	public com.thalmic.myo.Hub getRawHub() {
		return this.hub;
	}
	
	
	// ------------------------------------------------------------------------------
	// Interface
	
	/**
	 * Intern method to route a specific callback with dynamic data.
	 * 
	 * @param object Object, which has to implement the method.
	 * @param method Name of method, which will be called.
	 * @param classes Array of classes, which the method has to implement as signature.
	 * @param objects Array of objects, which stores valuable data for callback.
	 */
	protected void dispatch(Object object, String method, Class[] classes, Object[] objects, int logLevel){
		boolean success = false;
		if (method == null) {
			method = "myoOn";
		}
		if (classes.length == objects.length) {
			try {
				object.getClass().getMethod(
					method,
					classes
				).invoke(
					this.parent,
					objects
				);
				success = true;
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				if(success){
					this.log("Method: "+method+"(...); has been called.", logLevel);
				}
			}
		}
	}
	protected void dispatch(String method, Class[] classes, Object[] objects, int logLevel){
		this.dispatch(this.parent, method, classes, objects, logLevel);
	}
	protected void dispatch(String method, Class[] classes, Object[] objects){
		this.dispatch(this.parent, method, classes, objects, 1);
	}
	protected void dispatch(Class[] classes, Object[] objects, int logLevel){
		this.dispatch(this.parent, null, classes, objects, logLevel);
	}
	protected void dispatch(Class[] classes, Object[] objects){
		this.dispatch(this.parent, null, classes, objects, 1);
	}
	
	
	// ------------------------------------------------------------------------------
	// Commands
	
	/**
	 * The device will vibrate for haptic feedback.
	 * @param level Set the level of vibration duration [1,2,3].
	 * @return
	 */
	public Myo vibrate(int level) {
		switch (level) {
		case 1:
			this.log("Vibrating short ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_SHORT);
		case 2:
			this.log("Vibrating medium ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_MEDIUM);
		case 3:
			this.log("Vibrating long ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_LONG);
		}
		return this;
	}
	public Myo vibrate(){
		return this.vibrate(2);
	}
	
	
	// ------------------------------------------------------------------------------
	// Locking
	
	/**
	 * Force the Myo to lock immediately.
	 * @return
	 */
	public Myo lock() {
		this.myo.lock();
		return this;
	}
	
	/**
	 * Unlock the Myo.
	 * @param mode
	 * @return
	 */
	public Myo unlock(Unlock mode) {
		switch (mode) {
		case HOLD:
			this.myo.unlock(com.thalmic.myo.enums.UnlockType.UNLOCK_HOLD);
			break;
		case TIMED:
		default:
			this.myo.unlock(com.thalmic.myo.enums.UnlockType.UNLOCK_TIMED);
			break;
		}
		return this;
	}
	
	/**
	 * Set the locking policy for Myos connected to the Hub.
	 * @param policy
	 * @return
	 */
	public Myo setLockingPolicy(LockingPolicy policy){
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
	
	
	// ------------------------------------------------------------------------------
	// EMG

	/**
	 * Enable EMG mode.
	 * @return
	 */
	public Myo withEmg() {
		this.emg = new int[8];
		this.myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
		this.withEmg = true;
		return this;
	}
	
	/**
	 * Disable EMG mode.
	 * @return
	 */
	public Myo withoutEmg() {
		this.myo.setStreamEmg(StreamEmgType.STREAM_EMG_DISABLED);
		this.withEmg = false;
		return this;
	}
	
	/**
	 * Get raw data of EMG sensors.
	 * 
	 * @return
	 */
	public int[] getEmg() {
		return this.emg;
	}
	
	
	// ------------------------------------------------------------------------------
	// Setters & getters
	
	/**
	 * Set the duration to access data. 
	 * 
	 * @param frequency Time in milliseconds.
	 * @return
	 */
	public Myo setFrequency(int frequency) {
		this.frequency = frequency;
		return this;
	}
	
	/**
	 * Set the firmware of device.
	 * 
	 * @param firmwareVersion
	 * @return
	 */
	protected Myo setFirmware(FirmwareVersion firmwareVersion) {
		if (firmwareVersion == null) {
			this.firmware = "";
		} else {
			this.firmware = firmwareVersion.getFirmwareVersionMajor()
				+"."+firmwareVersion.getFirmwareVersionMinor()
				+"."+firmwareVersion.getFirmwareVersionPath();
		}
		return this;
	}
	
	/**
	 * Get the firmware of device.
	 * 
	 * @return
	 */
	public String getFirmware() {
		if (this.firmware == null) {
			return "";
		}
		return this.firmware;
	}
	
	/**
	 * Get the name of the latest pose.
	 * 
	 * @return Name of latest pose.
	 */
	public String getPose() {
		return this.pose.getType().toString().toUpperCase();
	}
	
	/**
	 * Get the type of recognized arm.
	 * 
	 * @return Type of recognized arm.
	 */
	public String getArm() {
		return this.arm.getType().toString().toUpperCase();
	}
	
	/**
	 * Arm recognized?
	 * 
	 * @return
	 */
	public boolean hasArm(){
		return this.arm.hasArm();
	}
	
	/**
	 * Left arm?
	 * 
	 * @return
	 */
	public Boolean isArmLeft(){
		return this.arm.isLeft();
	}
	
	/**
	 * Right arm?
	 * 
	 * @return
	 */
	public Boolean isArmRight(){
		return !this.isArmLeft();
	}
	
	/**
	 * Get orientation values of device.
	 * 
	 * @return Orientation as PVector, where 'x' is the 'roll' value, 'y' is the 'pitch' value and 'z' the 'yaw' value. 
	 */
	public PVector getOrientation(){
		return this.orientation;
	}
	
	/**
	 * Get gyroscope values of device.
	 * 
	 * @return
	 */
	public PVector getAccelerometer(){
		return this.accelerometer;
	}
	
	/**
	 * Get gyroscope values of device.
	 * 
	 * @return
	 */
	public PVector getGyroscope(){
		return this.gyroscope;
	}
	
	
	// ------------------------------------------------------------------------------
	// Verbose & logging

	/**
	 * Print debug information to the console.
	 * 
	 * @param 	verbose
	 * @return
	 */
	public Myo setVerbose(boolean verbose){
		this.verbose = verbose;
		return this;
	}
	
	/**
	 * Set the level of the log level.
	 * 
	 * @param level Set the level of the log level [1,2,3]. Three (3) will print lightweight events, too.
	 */
	public Myo setVerboseLevel(int level) {
		if (level > 0 && level < 4) {
			this.verboseLevel = level;
		} else {
			this.verboseLevel = 1;
		}
		return this;
	}
	
	/**
	 * Print log messages to the console.
	 * 
	 * @param message Set the readable message of that log.
	 * @param verboseLevel Set the priority level of that log.
	 * @return
	 */
	protected Myo log(String message, int verboseLevel){
		if (this.verbose == true && verboseLevel <= this.verboseLevel) {
			PApplet.println("# " + Myo.NAME + ": LOG (" + verboseLevel + "): " + message);
		}
		return this;
	}
	protected Myo log(String message){
		return this.log(message, 1);
	}

	
	// ------------------------------------------------------------------------------
	// Enums
	
	public enum Event {
		PAIR,
		UNPAIR,
		CONNECT,
		DISCONNECT,
		ARM_SYNC,
		ARM_UNSYNC,
		POSE,
		ORIENTATION,
		ACCELEROMETER,
		GYROSCOPE,
		RSSI,
		EMG,
		LOCK,
		UNLOCK
	}

	public enum LockingPolicy {
		NONE,
		STANDARD
	}

	public enum Unlock {
	    HOLD,
	    TIMED
	}
	
}
