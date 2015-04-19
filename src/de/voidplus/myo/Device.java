package de.voidplus.myo;

import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.enums.StreamEmgType;
import de.voidplus.myo.Myo.Unlock;
import processing.core.PVector;


//=====================================================================================
// Table of Content
//=====================================================================================
//
// 1 Properties
// 2 Constructors
// 3 Commands
// 4 Setters
// 5 Getters


/**
 * Class which represents a single Myo device.
 */
public class Device {

	
    //=================================================================================
	// 1 Properties
	//=================================================================================
	
	protected com.thalmic.myo.Myo myo;
	protected Integer id;
	protected Arm arm;
	protected Pose pose;
	protected String firmware;
	protected PVector orientation, accelerometer, gyroscope;
	protected int rssi;
	protected int[] emg;
	protected boolean withEmg;

	
    //=================================================================================
    // 2 Constructors
    //=================================================================================
	
	public Device(com.thalmic.myo.Myo myo) {
		this.myo = myo;
		this.arm = new Arm();
		this.pose = new Pose();
		this.orientation = new PVector();
		this.accelerometer = new PVector();
		this.gyroscope = new PVector();

		this.id = 0;
	}

    //================================================================================
    // 3 Commands
    //================================================================================
	
	/**
	 * Vibrate device.
	 * @param level
	 * @return
	 */
	public Device vibrate(int level) {
		switch (level) {
		case 1:
			de.voidplus.myo.Myo.log("Vibrating short ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_SHORT);
		case 2:
			de.voidplus.myo.Myo.log("Vibrating medium ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_MEDIUM);
		case 3:
			de.voidplus.myo.Myo.log("Vibrating long ...");
			this.myo.vibrate(com.thalmic.myo.enums.VibrationType.VIBRATION_LONG);
		}
		return this;
	}
	
	/**
	 * Vibrate device with medium strength.
	 * @return
	 */
	public Device vibrate() {
		return this.vibrate(2);
	}

	/**
	 * An myoOnRssi(Myo, long, int) event will likely be generated with the value of the RSSI.
	 * @return
	 */
	public Device requestRssi() {
		this.myo.requestRssi();
		return this;
	}
	
	/**
	 * Force the Myo locking immediately.
	 * @return
	 */
	public Device lock() {
		this.myo.lock();
		return this;
	}

	/**
	 * Unlock the Myo.
	 * @param mode
	 * @return
	 */
	public Device unlock(Unlock mode) {
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
	
	
    //================================================================================
    // 4 Setters
    //================================================================================

	/**
	 * Enable EMG mode.
	 * @return
	 */
	public Device withEmg() {
		this.emg = new int[8];
		this.myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
		this.withEmg = true;
		return this;
	}

	/**
	 * Disable EMG mode.
	 * @return
	 */
	public Device withoutEmg() {
		this.myo.setStreamEmg(StreamEmgType.STREAM_EMG_DISABLED);
		this.withEmg = false;
		return this;
	}
	
	/**
	 * Set firmware of device.
	 * @param firmwareVersion
	 * @return
	 */
	protected Device setFirmware(FirmwareVersion firmwareVersion) {
		if (firmwareVersion == null) {
			this.firmware = "";
		} else {
			this.firmware = firmwareVersion.getFirmwareVersionMajor() + "."
					+ firmwareVersion.getFirmwareVersionMinor() + "."
					+ firmwareVersion.getFirmwareVersionPath();
		}
		return this;
	}

    //================================================================================
    // 5 Getters
    //================================================================================

	//--------------------------------------------------------------------------------
	// 5.1 Raw or original objects
	
	public com.thalmic.myo.Myo getMyo() {
		return this.myo;
	}

	//--------------------------------------------------------------------------------
	// 5.2 Device

	/**
	 * Get ID of device.
	 * @return
	 */
	public Integer getId() {
		return this.id;
	}
	
	/**
	 * Get the firmware of device.
	 * @return
	 */
	public String getFirmware() {
		if (this.firmware == null) {
			return "";
		}
		return this.firmware;
	}
	
	/**
	 * Set ID of device.
	 * @param deviceId
	 * @return
	 */
	protected Integer setId(int deviceId) {
		return this.id = deviceId;
	}

	//--------------------------------------------------------------------------------
	// 5.3 Objects

	//----------------------------------------
	// 5.3.1 Pose

	/**
	 * Get the name of the latest pose.
	 * @return Name of latest pose.
	 */
	public String getPose() {
		return this.pose.getType().toString().toUpperCase();
	}

	//----------------------------------------
	// 5.3.2 Arm

	/**
	 * Get the type of recognized arm.
	 * @return Type of recognized arm.
	 */
	public String getArm() {
		return this.arm.getType().toString().toUpperCase();
	}

	/**
	 * Arm recognized?
	 * @return
	 */
	public boolean hasArm() {
		return this.arm.hasArm();
	}

	/**
	 * Left arm?
	 * @return
	 */
	public Boolean isArmLeft() {
		return this.arm.isLeft();
	}

	/**
	 * Right arm?
	 * @return
	 */
	public Boolean isArmRight() {
		return !this.isArmLeft();
	}

	//--------------------------------------------------------------------------------
	// 5.4 Sensors

	/**
	 * Get orientation values of device.
	 * @return Orientation as PVector, where 'x' is the 'roll' value, 'y' is the
	 *         'pitch' value and 'z' the 'yaw' value.
	 */
	public PVector getOrientation() {
		return this.orientation;
	}

	/**
	 * Get gyroscope values of device.
	 * @return
	 */
	public PVector getAccelerometer() {
		return this.accelerometer;
	}

	/**
	 * Get gyroscope values of device.
	 * @return
	 */
	public PVector getGyroscope() {
		return this.gyroscope;
	}

	/**
	 * Get raw data of EMG sensors.
	 * @return
	 */
	public int[] getEmg() {
		return this.emg;
	}

}
