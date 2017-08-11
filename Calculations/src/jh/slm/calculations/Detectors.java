package jh.slm.calculations;

public class Detectors {
	static { System.loadLibrary("detectors"); }
	public static native void Square(Object input, Object output);
	public static native int Peak(Object input, int max);
	public static native long Leq(Object input);
	public static native void Rms(Object input, Object rmsOutput, long[] dose, long[] doseMax, Object state, int noOutputs);
}
 