package de.voidplus.myo;

import processing.core.PVector;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;

public class Collector implements DeviceListener {

	private de.voidplus.myo.Myo myo;
    private static final int SCALE = 18;
    
	public Collector(de.voidplus.myo.Myo myo) {
		this.myo = myo;
	}

	
	// ------------------------------------------------------------------------------
	// Interface
	
	private void dispatchGlobalEvent(de.voidplus.myo.Myo.Event event, de.voidplus.myo.Myo myo, long timestamp, int logLevel){
		this.myo.dispatch(new Class[] {
			de.voidplus.myo.Myo.Event.class,
			de.voidplus.myo.Myo.class,
			long.class
		}, new Object[] {
			event,
			this.myo,
			timestamp
		}, logLevel);
	}
	private void dispatchGlobalEvent(de.voidplus.myo.Myo.Event event, de.voidplus.myo.Myo myo, long timestamp){
		this.dispatchGlobalEvent(event, myo, timestamp, 1);
	}
	
	private void dispatchLocalEvent(String method, Class[] classes, Object[] objects, int logLevel){
		this.myo.dispatch(method, classes, objects, logLevel);
	}
	private void dispatchLocalEvent(String method, Class[] classes, Object[] objects){
		this.dispatchLocalEvent(method, classes, objects, 1);
	}
	
	
	// ------------------------------------------------------------------------------
	// Callbacks

	@Override
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
		this.myo.setFirmware(firmwareVersion);
		
		if(this.myo.withEmg){
			if (this.myo.emg != null) {
				for (int i = 0; i < this.myo.emg.length; i++) {
					this.myo.emg[i] = 0;
				}
			} else {
				this.myo.emg = new int[8];
			}			
		}

		this.dispatchLocalEvent("myoOnPair", new Class[] {
			de.voidplus.myo.Myo.class,
			long.class,
			String.class
		}, new Object[] {
			this.myo,
			timestamp,
			this.myo.getFirmware()
		});
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.PAIR, this.myo, timestamp);
	}

	@Override
	public void onUnpair(Myo myo, long timestamp) {
		this.dispatchLocalEvent("myoOnUnpair", new Class[] {
			de.voidplus.myo.Myo.class,
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.UNPAIR, this.myo, timestamp);
	}
	
	@Override
	public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
		this.myo.setFirmware(firmwareVersion);
		
		this.dispatchLocalEvent("myoOnConnect", new Class[] {
			de.voidplus.myo.Myo.class,
			long.class,
			String.class
		}, new Object[] {
			this.myo,
			timestamp,
			this.myo.getFirmware()
		});
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.CONNECT, this.myo, timestamp);
	}

	@Override
	public void onDisconnect(Myo myo, long timestamp) {
		this.myo.setFirmware(null);
		
		this.dispatchLocalEvent("myoOnDisconnect", new Class[] {
			de.voidplus.myo.Myo.class,
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.DISCONNECT, this.myo, timestamp);
	}
	
	@Override
	public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
		if(arm != com.thalmic.myo.enums.Arm.ARM_UNKNOWN){
			if(this.myo.arm.type.asRaw() != arm){
				switch(arm){
				case ARM_LEFT:
					this.myo.arm.type = de.voidplus.myo.Arm.Type.LEFT;
					break;
				case ARM_RIGHT:
					this.myo.arm.type = de.voidplus.myo.Arm.Type.RIGHT;
					break;
				case ARM_UNKNOWN:
					this.myo.arm.type = de.voidplus.myo.Arm.Type.UNKNOWN;
					break;
				}
				
				this.dispatchLocalEvent("myoOnArmSync", new Class[] {
					de.voidplus.myo.Myo.class,
					long.class,
					de.voidplus.myo.Arm.class
				}, new Object[] {
					this.myo,
					timestamp,
					this.myo.arm
				}, 2);
				this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.ARM_SYNC, this.myo, timestamp);
			}		
		}
	}	
	
	@Override
	public void onArmUnsync(Myo myo, long timestamp) {
		this.myo.arm.type = de.voidplus.myo.Arm.Type.UNKNOWN;
		this.myo.pose.type = de.voidplus.myo.Pose.Type.UNKNOWN;
		
		this.dispatchLocalEvent("myoOnArmUnsync", new Class[] {
			de.voidplus.myo.Myo.class,
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.ARM_UNSYNC, this.myo, timestamp);
	}
	
	@Override
	public void onPose(Myo myo, long timestamp, com.thalmic.myo.Pose pose) { 
		if (pose.getType() != com.thalmic.myo.enums.PoseType.UNKNOWN) {
			boolean oldPoseIsRest = this.myo.pose.type.asRaw() == com.thalmic.myo.enums.PoseType.REST;
			boolean newPoseChanged = this.myo.pose.type.asRaw() != pose.getType();
			
			switch (pose.getType()) {
			case REST:
				this.myo.pose.type = de.voidplus.myo.Pose.Type.REST;
				break;
			case FIST:
				this.myo.pose.type = de.voidplus.myo.Pose.Type.FIST;
				break;
			case WAVE_IN:
				this.myo.pose.type = de.voidplus.myo.Pose.Type.WAVE_IN;
				break;
			case WAVE_OUT:
				this.myo.pose.type = de.voidplus.myo.Pose.Type.WAVE_OUT;
				break;
			case FINGERS_SPREAD:
				this.myo.pose.type = de.voidplus.myo.Pose.Type.FINGERS_SPREAD;
				break;
//			case RESERVED_1:
//				this.myo.pose = de.voidplus.myo.Myo.Pose.RESERVED_1;
//				break;
			case DOUBLE_TAP:
				this.myo.pose.type = de.voidplus.myo.Pose.Type.DOUBLE_TAP;
				break;
//			case UNKNOWN:
//				this.myo.pose = de.voidplus.myo.Myo.Pose.UNKNOWN;
//				break;
			}
			
			if (oldPoseIsRest && newPoseChanged) {
				this.dispatchLocalEvent("myoOnPose", new Class[] {
					de.voidplus.myo.Myo.class,
					long.class,
					de.voidplus.myo.Pose.class
				}, new Object[] {
					this.myo,
					timestamp,
					this.myo.pose
				}, 2);
			}
			this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.POSE, this.myo, timestamp, 2);
		}
	}

	@Override
	public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
		Quaternion normalized = rotation.normalized();
		
		double roll = Math.atan2(2.0f * (normalized.getW() * normalized.getX() + normalized.getY() * normalized.getZ()), 1.0f - 2.0f * (normalized.getX() * normalized.getX() + normalized.getY() * normalized.getY()));
		double pitch = Math.asin(2.0f * (normalized.getW() * normalized.getY() - normalized.getZ() * normalized.getX()));
		double yaw = Math.atan2(2.0f * (normalized.getW() * normalized.getZ() + normalized.getX() * normalized.getY()), 1.0f - 2.0f * (normalized.getY() * normalized.getY() + normalized.getZ() * normalized.getZ()));

		this.myo.orientation = new PVector(
			(float)((roll + Math.PI) / (Math.PI * 2.0) * Collector.SCALE),
			(float)((pitch + Math.PI / 2.0) / Math.PI * Collector.SCALE),
			(float)((yaw + Math.PI) / (Math.PI * 2.0) * Collector.SCALE)
		);	
		
		this.dispatchLocalEvent("myoOnOrientation", new Class[]{
			de.voidplus.myo.Myo.class,
			long.class,
			processing.core.PVector.class
		}, new Object[]{
			this.myo,
			timestamp,
			this.myo.orientation
		}, 4);
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.ORIENTATION, this.myo, timestamp, 4);
	}
	
	@Override
	public void onAccelerometerData(Myo myo, long timestamp, Vector3 accelerometer) {
		this.myo.accelerometer = new PVector(
			(float)accelerometer.getX(),
			(float)accelerometer.getY(),
			(float)accelerometer.getZ()
		);	
			
		this.dispatchLocalEvent("myoOnAccelerometer", new Class[]{
			de.voidplus.myo.Myo.class,
			long.class,
			processing.core.PVector.class
		}, new Object[]{
			this.myo,
			timestamp,
			this.myo.accelerometer
		}, 4);
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.ACCELEROMETER, this.myo, timestamp, 4);
	}

	@Override
	public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyroscope) {
		this.myo.gyroscope = new PVector(
			(float)gyroscope.getX(),
			(float)gyroscope.getY(),
			(float)gyroscope.getZ()
		);
				
		this.dispatchLocalEvent("myoOnGyroscope", new Class[]{
			de.voidplus.myo.Myo.class,
			long.class,
			processing.core.PVector.class
		}, new Object[]{
			this.myo,
			timestamp,
			this.myo.gyroscope
		}, 4);
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.GYROSCOPE, this.myo, timestamp, 4);
	}

	@Override
	public void onRssi(Myo myo, long timestamp, int rssi) {
		this.myo.rssi = rssi;
		
		this.dispatchLocalEvent("myoOnRssi", new Class[]{
			de.voidplus.myo.Myo.class,
			long.class,
			int.class
		}, new Object[]{
			this.myo,
			timestamp,
			rssi
		}, 3);
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.RSSI, this.myo, timestamp, 3);
	}

	@Override
	public void onLock(Myo myo, long timestamp) {
		this.dispatchLocalEvent("myoOnLock", new Class[]{
			de.voidplus.myo.Myo.class,
			long.class
		}, new Object[]{
			this.myo,
			timestamp
		}, 3);
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.LOCK, this.myo, timestamp, 3);
	}

	@Override
	public void onUnlock(Myo arg0, long timestamp) {
		this.dispatchLocalEvent("myoOnUnLock", new Class[]{
			de.voidplus.myo.Myo.class,
			long.class
		}, new Object[]{
			this.myo,
			timestamp
		}, 3);
		this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.UNLOCK, this.myo, timestamp, 3);
	}

	@Override
	public void onEmgData(Myo myo, long timestamp, byte[] data) {
		if(this.myo.withEmg && data!=null){
//			System.out.println(Arrays.toString(data));
			for (int i = 0; i < 8; i++) {
				this.myo.emg[i] = data[i];
			}
//			System.out.println(Arrays.toString(this.myo.emg));
			this.dispatchLocalEvent("myoOnEmg", new Class[]{
				de.voidplus.myo.Myo.class,
				long.class,
				int[].class
			}, new Object[]{
				this.myo,
				timestamp,
				this.myo.emg
			}, 4);
			this.dispatchGlobalEvent(de.voidplus.myo.Myo.Event.EMG, this.myo, timestamp, 4);
		}
	}

}
