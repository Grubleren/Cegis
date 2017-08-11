package jh.slm.calculations;

public class Iirfilter {
	static { System.loadLibrary("iirfilter"); }
	public static native Object BiQuads(int[] biQuads);
	public static native void Filter(Object input, Object output, Object state);
}
