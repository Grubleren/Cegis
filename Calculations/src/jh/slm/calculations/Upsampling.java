package jh.slm.calculations;

public class Upsampling {

	static { System.loadLibrary("upsampling"); }
	public static native Object Init(int down, int up, int[] coef);
	public static native void Resample(Object input, Object output, Object state);
}
