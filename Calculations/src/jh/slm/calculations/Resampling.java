package jh.slm.calculations;

public class Resampling {
	static { System.loadLibrary("resampling"); }
	public static native Object Init(int down, int up, int[] coef);
	public static native void Resample(int length, Object input, Object output, Object state);

}
