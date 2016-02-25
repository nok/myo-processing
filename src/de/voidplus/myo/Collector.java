package de.voidplus.myo;

import processing.core.PVector;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
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
	
	private de.voidplus.myo.Myo myo;

	
    //=================================================================================
    // 2 Constructors
    //=================================================================================
	
	public Collector(de.voidplus.myo.Myo myo) {
		this.myo = myo;
	}	


    //=================================================================================
    // 3 Callbacks
    //=================================================================================
	
	@Override
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
		Device device = this.myo.identifyDevice(myo);
		device.setFirmware(firmwareVersion);

		// EMG
		if (device.emg != null) {
			for (int i = 0; i < device.emg.length; i++) {
				device.emg[i] = 0;
			}
		} else {
			device.emg = new int[8];
		}

		// Local
		this.myo.dispatch("myoOnPair", new Class[] {
			this.myo.getClass(),
			long.class,
			String.class
		}, new Object[] {
			this.myo,
			timestamp,
			this.myo.getFirmware()
		});
		this.myo.dispatch("myoOnPair", new Class[] {
			device.getClass(),
			long.class,
			String.class
		}, new Object[] {
			device,
			timestamp,
			this.myo.getFirmware()
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.PAIR,
			this.myo,
			timestamp
		}, 1);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.PAIR,
			device,
			timestamp
		}, 1);
	}

	@Override
	public void onUnpair(Myo myo, long timestamp) {
		Device device = this.myo.identifyDevice(myo);
		
		// Local
		this.myo.dispatch("myoOnUnpair", new Class[] {
			this.myo.getClass(),
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.myo.dispatch("myoOnPair", new Class[] {
			device.getClass(),
			long.class
		}, new Object[] {
			device,
			timestamp
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.UNPAIR,
			this.myo,
			timestamp
		}, 1);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.UNPAIR,
			device,
			timestamp
		}, 1);
	}
	
	@Override
	public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
		Device device = this.myo.identifyDevice(myo);
		device.setFirmware(firmwareVersion);
		
		// Local
		this.myo.dispatch("myoOnConnect", new Class[] {
			this.myo.getClass(),
			long.class,
			String.class
		}, new Object[] {
			this.myo,
			timestamp,
			device.getFirmware()
		});
		this.myo.dispatch("myoOnConnect", new Class[] {
			device.getClass(),
			long.class,
			String.class
		}, new Object[] {
			device,
			timestamp,
			device.getFirmware()
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.CONNECT,
			this.myo,
			timestamp
		}, 1);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.CONNECT,
			device,
			timestamp
		}, 1);
		
	}

	@Override
	public void onDisconnect(Myo myo, long timestamp) {
		Device device = this.myo.identifyDevice(myo);
		
		// Local
		this.myo.dispatch("myoOnDisconnect", new Class[] {
			this.myo.getClass(),
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.myo.dispatch("myoOnDisconnect", new Class[] {
			device.getClass(),
			long.class
		}, new Object[] {
			device,
			timestamp
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.DISCONNECT,
			this.myo,
			timestamp
		}, 1);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.DISCONNECT,
			device,
			timestamp
		}, 1);
	}
	
	@Override
	public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
		Device device = this.myo.identifyDevice(myo);
		
		if (arm != com.thalmic.myo.enums.Arm.ARM_UNKNOWN) {
			if (device.arm.type.asRaw() != arm) {
				switch (arm) {
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

				// Local
				this.myo.dispatch("myoOnArmSync", new Class[] {
					this.myo.getClass(),
					long.class,
					de.voidplus.myo.Arm.class
				}, new Object[] {
					this.myo,
					timestamp,
					device.arm
				});
				this.myo.dispatch("myoOnArmSync", new Class[] {
					device.getClass(),
					long.class,
					de.voidplus.myo.Arm.class
				}, new Object[] {
					device,
					timestamp,
					device.arm
				});
				
				// Global
				this.myo.dispatch(new Class[] {
					Event.class,
					this.myo.getClass(),
					long.class
				}, new Object[] {
					Event.ARM_SYNC,
					this.myo,
					timestamp
				}, 1);
				this.myo.dispatch(new Class[] {
					Event.class,
					device.getClass(),
					long.class
				}, new Object[] {
					Event.ARM_SYNC,
					device,
					timestamp
				}, 1);
			}		
		}
	}	
	
	@Override
	public void onArmUnsync(Myo myo, long timestamp) {
		Device device = this.myo.identifyDevice(myo);

		device.arm.type = de.voidplus.myo.Arm.Type.UNKNOWN;
		device.pose.type = de.voidplus.myo.Pose.Type.UNKNOWN;
		
		// Local
		this.myo.dispatch("myoOnArmUnsync", new Class[] {
			this.myo.getClass(),
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.myo.dispatch("myoOnArmUnsync", new Class[] {
			device.getClass(),
			long.class
		}, new Object[] {
			device,
			timestamp
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.ARM_UNSYNC,
			this.myo,
			timestamp
		}, 1);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.ARM_UNSYNC,
			device,
			timestamp
		}, 1);
		
	}
	
	@Override
	public void onPose(Myo myo, long timestamp, com.thalmic.myo.Pose pose) { 
		Device device = this.myo.identifyDevice(myo);
		
		if (pose.getType() != com.thalmic.myo.enums.PoseType.UNKNOWN) {
			boolean newPoseChanged = device.pose.type.asRaw() != pose.getType();
			
			if (newPoseChanged) {
				switch (pose.getType()) {
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
				default:
					break;
				}
				
				// Local
				this.myo.dispatch("myoOnPose", new Class[] {
					this.myo.getClass(),
					long.class,
					device.pose.getClass()
				}, new Object[] {
					this.myo,
					timestamp,
					device.pose
				});
				this.myo.dispatch("myoOnPose", new Class[] {
					device.getClass(),
					long.class,
					device.pose.getClass()
				}, new Object[] {
					device,
					timestamp,
					device.pose
				});
				
				// Global
				this.myo.dispatch(new Class[] {
					Event.class,
					this.myo.getClass(),
					long.class
				}, new Object[] {
					Event.POSE,
					this.myo,
					timestamp
				}, 2);
				this.myo.dispatch(new Class[] {
					Event.class,
					device.getClass(),
					long.class
				}, new Object[] {
					Event.POSE,
					device,
					timestamp
				}, 2);
			}
		}
	}

	@Override
	public void onRssi(Myo myo, long timestamp, int rssi) {
		Device device = this.myo.identifyDevice(myo);
		device.rssi = rssi;
		
		// Local
		this.myo.dispatch("myoOnRssi", new Class[] {
			this.myo.getClass(),
			long.class,
			int.class
		}, new Object[] {
			this.myo,
			timestamp,
			device.rssi
		});
		this.myo.dispatch("myoOnRssi", new Class[] {
			device.getClass(),
			long.class,
			int.class
		}, new Object[] {
			device,
			timestamp,
			device.rssi
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.RSSI,
			this.myo,
			timestamp
		}, 3);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.RSSI,
			device,
			timestamp
		}, 3);
	}

	@Override
	public void onLock(Myo myo, long timestamp) {
		Device device = this.myo.identifyDevice(myo);
		
		// Local
		this.myo.dispatch("myoOnLock", new Class[] {
			this.myo.getClass(),
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.myo.dispatch("myoOnLock", new Class[] {
			device.getClass(),
			long.class
		}, new Object[] {
			device,
			timestamp
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.LOCK,
			this.myo,
			timestamp
		}, 3);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.LOCK,
			device,
			timestamp
		}, 3);
	}

	@Override
	public void onUnlock(Myo myo, long timestamp) {
		Device device = this.myo.identifyDevice(myo);
		
		// Local
		this.myo.dispatch("myoOnUnLock", new Class[] {
			this.myo.getClass(),
			long.class
		}, new Object[] {
			this.myo,
			timestamp
		});
		this.myo.dispatch("myoOnUnLock", new Class[] {
			device.getClass(),
			long.class
		}, new Object[] {
			device,
			timestamp
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.UNLOCK,
			this.myo,
			timestamp
		}, 3);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.UNLOCK,
			device,
			timestamp
		}, 3);
	}
	
	@Override
	public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
		Device device = this.myo.identifyDevice(myo);
		
		Quaternion normalized = rotation.normalized();
		double roll = Math.atan2(2.0f * (normalized.getW() * normalized.getX() + normalized.getY() * normalized.getZ()), 1.0f - 2.0f * (normalized.getX() * normalized.getX() + normalized.getY() * normalized.getY()));
		double pitch = Math.asin(2.0f * (normalized.getW() * normalized.getY() - normalized.getZ() * normalized.getX()));
		double yaw = Math.atan2(2.0f * (normalized.getW() * normalized.getZ() + normalized.getX() * normalized.getY()), 1.0f - 2.0f * (normalized.getY() * normalized.getY() + normalized.getZ() * normalized.getZ()));

		device.orientation = new PVector(
			(float) ((roll + Math.PI) / (Math.PI * 2.0)),
			(float) ((pitch + Math.PI / 2.0) / Math.PI),
			(float) ((yaw + Math.PI) / (Math.PI * 2.0))
		);
		
		// Local
		this.myo.dispatch("myoOnOrientation", new Class[] {
			this.myo.getClass(),
			long.class,
			processing.core.PVector.class
		}, new Object[] {
			this.myo,
			timestamp,
			device.orientation
		});
		this.myo.dispatch("myoOnOrientation", new Class[] {
			device.getClass(),
			long.class,
			processing.core.PVector.class
		}, new Object[] {
			device,
			timestamp,
			device.orientation
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.ORIENTATION,
			this.myo,
			timestamp
		}, 4);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.ORIENTATION,
			device,
			timestamp
		}, 4);
		
	}
	
	@Override
	public void onAccelerometerData(Myo myo, long timestamp, Vector3 accelerometer) {
		Device device = this.myo.identifyDevice(myo);
		
		device.accelerometer = new PVector(
			(float)accelerometer.getX(),
			(float)accelerometer.getY(),
			(float)accelerometer.getZ()
		);
		
		// Local
		this.myo.dispatch("myoOnAccelerometer", new Class[] {
			this.myo.getClass(),
			long.class,
			processing.core.PVector.class
		}, new Object[] {
			this.myo,
			timestamp,
			device.accelerometer
		});
		this.myo.dispatch("myoOnAccelerometer", new Class[] {
			device.getClass(),
			long.class,
			processing.core.PVector.class
		}, new Object[] {
			device,
			timestamp,
			device.accelerometer
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.ACCELEROMETER,
			this.myo,
			timestamp
		}, 4);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.ACCELEROMETER,
			device,
			timestamp
		}, 4);
	}

	@Override
	public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyroscope) {
		Device device = this.myo.identifyDevice(myo);
		
		device.gyroscope = new PVector(
			(float)gyroscope.getX(),
			(float)gyroscope.getY(),
			(float)gyroscope.getZ()
		);
		
		// Local
		this.myo.dispatch("myoOnGyroscope", new Class[] {
			this.myo.getClass(),
			long.class,
			processing.core.PVector.class
		}, new Object[] {
			this.myo,
			timestamp,
			device.gyroscope
		});
		this.myo.dispatch("myoOnGyroscope", new Class[] {
			device.getClass(),
			long.class,
			processing.core.PVector.class
		}, new Object[] {
			device,
			timestamp,
			device.gyroscope
		});
		
		// Global
		this.myo.dispatch(new Class[] {
			Event.class,
			this.myo.getClass(),
			long.class
		}, new Object[] {
			Event.GYROSCOPE,
			this.myo,
			timestamp
		}, 4);
		this.myo.dispatch(new Class[] {
			Event.class,
			device.getClass(),
			long.class
		}, new Object[] {
			Event.GYROSCOPE,
			device,
			timestamp
		}, 4);
	}

	@Override
	public void onEmgData(Myo myo, long timestamp, byte[] data) {
		Device device = this.myo.identifyDevice(myo);

		if (this.myo.withEmg && data != null) {
			for (int i = 0; i < 8; i++) {
				device.emg[i] = data[i];
			}
			
			// Local
			this.myo.dispatch("myoOnEmg", new Class[] {
				this.myo.getClass(),
				long.class,
				int[].class
			}, new Object[] {
				this.myo,
				timestamp,
				device.emg
			});
			this.myo.dispatch("myoOnEmg", new Class[] {
				device.getClass(),   // Device
				long.class,
				int[].class
			}, new Object[] {
				device,
				timestamp,
				device.emg
			});
			
			// Global
			this.myo.dispatch(new Class[] {
				Event.class,
				this.myo.getClass(),
				long.class
			}, new Object[] {
				Event.EMG,
				this.myo,
				timestamp
			}, 4);
			this.myo.dispatch(new Class[] {
				Event.class,
				device.getClass(),
				long.class
			}, new Object[] {
				Event.EMG,
				device,
				timestamp
			}, 4);
		}
	}

}
