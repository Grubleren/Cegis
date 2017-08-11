package jh.slm.calculations;

public class Generator {
	static { System.loadLibrary("Generator"); }
	public static native void sin(Object input, double samplingFrequency, double frequency, double scale);
	public static native void random(Object input, double scale);

}
