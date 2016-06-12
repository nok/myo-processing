package de.voidplus.myo;


public class Arm {


    protected Type type;

    protected Arm() {
        type = Type.UNKNOWN;
    }

    /**
     * Get the type of arm.
     *
     * @return
     */
    public de.voidplus.myo.Arm.Type getType() {
        return this.type;
    }

    /**
     * Left arm?
     *
     * @return
     */
    public Boolean isLeft() {
        switch (this.type) {
            case LEFT:
                return true;
            case RIGHT:
                return false;
        }
        return false;
    }

    /**
     * Right arm?
     * <p/>
     * For humans with more than two arms. ;-)
     *
     * @return
     */
    public Boolean isRight() {
        return !this.isLeft();
    }

    public enum Type {
        LEFT(com.thalmic.myo.enums.Arm.ARM_LEFT),
        RIGHT(com.thalmic.myo.enums.Arm.ARM_RIGHT),
        UNKNOWN(com.thalmic.myo.enums.Arm.ARM_UNKNOWN);

        private final com.thalmic.myo.enums.Arm arm;

        Type(com.thalmic.myo.enums.Arm arm) {
            this.arm = arm;
        }

        protected com.thalmic.myo.enums.Arm asRaw() {
            return this.arm;
        }
    }

}
