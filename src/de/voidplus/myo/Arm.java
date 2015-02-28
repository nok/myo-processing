package de.voidplus.myo;

public class Arm {
	
	protected Type type;
	
	protected Arm(){
		type = Type.UNKNOWN;
	}
	
	/**
	 * Get the type of arm.
	 * 
	 * @return
	 */
	public de.voidplus.myo.Arm.Type getType(){
		return this.type;
	}
	
	/**
	 * Arm recognized?
	 * 
	 * @return
	 */
	protected boolean hasArm(){
		return this.getType() != Type.UNKNOWN;
	}
	
	/**
	 * Left arm?
	 * 
	 * @return
	 */
	public Boolean isLeft(){
		switch (this.type) {
		case LEFT:
			return true;
		case RIGHT:
			return false;
		default:
			return null;
		}
	}
	
	/**
	 * Right arm?
	 * 
	 * For humans with more than two arms. ;-)
	 * 
	 * @return
	 */
	public Boolean isRight(){
		return !this.isLeft();
	}
	
	public static enum Type {
		LEFT(com.thalmic.myo.enums.Arm.ARM_LEFT),
		RIGHT(com.thalmic.myo.enums.Arm.ARM_RIGHT),
		UNKNOWN(com.thalmic.myo.enums.Arm.ARM_UNKNOWN);
		
	    private final com.thalmic.myo.enums.Arm arm;
		private Type(com.thalmic.myo.enums.Arm arm) { this.arm = arm; }
		protected com.thalmic.myo.enums.Arm asRaw() { return this.arm; }
	}
	
}
