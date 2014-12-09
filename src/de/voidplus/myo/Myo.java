package de.voidplus.myo;

import com.thalmic.myo.FirmwareVersion;

import processing.core.PApplet;
import processing.core.PVector;

public class Myo {

	private final static String NAME = "Myo";
	private final static String VERSION = "0.7.0b";
	private final static String MYO_SDK_VERSION = "0.7.0b";
	private final static String MYO_FIRMWARE_VERSION = "1.1.4";
	private final static String REPOSITORY = "https://github.com/voidplus/myo-processing";
	
	private PApplet parent;
	private boolean verbose;
	private int verboseLevel;
	private int frequency;
	
	private com.thalmic.myo.Myo myo;
	private com.thalmic.myo.Hub hub;
	private Collector collector;
	
	private String firmware;
	protected Arm arm;
	protected Pose pose;
	protected PVector orientation, accelerometer, gyroscope;
	protected int rssi;
	
	public Myo(PApplet parent) {
		PApplet.println("# "+Myo.NAME+" v"+Myo.VERSION+" - Support: Myo SDK v"+Myo.MYO_SDK_VERSION+", Firmware v"+Myo.MYO_FIRMWARE_VERSION+" - "+Myo.REPOSITORY);
		
		parent.registerMethod("pre", this);
//		parent.registerMethod("post", this);
		parent.registerMethod("dispose", this);
		
		this.parent = parent;
		this.setVerbose(false)
			.setVerboseLevel(1)
			.setFrequency(50);

		this.hub = new com.thalmic.myo.Hub();
		this.myo = hub.waitForMyo(10000);
		
		if (this.myo == null) {
			throw new RuntimeException("Unable to find a Myo!");
		}
		this.log("Connected to a Myo armband.");
		
		this.collector = new Collector(this);
		this.hub.addListener(this.collector);
		
		this.arm = new Arm();
		this.pose = new Pose();
		this.orientation = new PVector();
		this.accelerometer = new PVector();
		this.gyroscope = new PVector();
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
	 * 
	 * @param level Set the level of vibration duration [1,2,3].
	 */
	public void vibrate(int level) {
		switch (level) {
		case 1:
			this.log("Vibrating short ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_SHORT);
			return;
		case 2:
			this.log("Vibrating medium ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_MEDIUM);
			return;
		case 3:
			this.log("Vibrating long ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_LONG);
			return;
		}
	}
	public void vibrate(){
		this.vibrate(2);
	}
	
	
	// ------------------------------------------------------------------------------
	// Setters & getters
	
	/**
	 * Set the duration to access data. 
	 * 
	 * @param frequency Time in milliseconds.
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
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
		return this.pose.getTypeAsStr();
	}
	
	/**
	 * Get the type of recognized arm.
	 * 
	 * @return Type of recognized arm.
	 */
	public String getArm() {
		return this.arm.getTypeAsStr();
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
	 */
	protected void log(String message, int verboseLevel){
		if (this.verbose == true && verboseLevel <= this.verboseLevel) {
			PApplet.println("# " + Myo.NAME + ": LOG (" + verboseLevel + "): " + message);
		}
	}
	protected void log(String message){
		this.log(message, 1);
	}

	
	// ------------------------------------------------------------------------------
	// Verbose & logging
	
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
		LOCK,
		UNLOCK
	}
	
}
