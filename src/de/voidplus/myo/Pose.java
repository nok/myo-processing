package de.voidplus.myo;


public class Pose {


    protected Type type;

    protected Pose() {
        type = Type.UNKNOWN;
    }

    /**
     * Get the type of pose.
     *
     * @return
     */
    public de.voidplus.myo.Pose.Type getType() {
        return this.type;
    }

    public enum Type {
        REST(com.thalmic.myo.enums.PoseType.REST),
        FIST(com.thalmic.myo.enums.PoseType.FIST),
        WAVE_IN(com.thalmic.myo.enums.PoseType.WAVE_IN),
        WAVE_OUT(com.thalmic.myo.enums.PoseType.WAVE_OUT),
        FINGERS_SPREAD(com.thalmic.myo.enums.PoseType.FINGERS_SPREAD),
        DOUBLE_TAP(com.thalmic.myo.enums.PoseType.DOUBLE_TAP),
        UNKNOWN(com.thalmic.myo.enums.PoseType.UNKNOWN);

        private final com.thalmic.myo.enums.PoseType pose;

        Type(com.thalmic.myo.enums.PoseType pose) {
            this.pose = pose;
        }

        protected com.thalmic.myo.enums.PoseType asRaw() {
            return this.pose;
        }
    }

}
