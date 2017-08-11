package jh.slm.calculations;

public class Statistics {
	static { System.loadLibrary("statistics"); }
	public static native void Reset(int[] distribution, int[] range);
	public static native void Distribution(Object input, int[] distribution, int[] lnValues, float[] lnPercentages, int[]range, State state);
	public static native void PC(State state);

	public class State
	{
		public boolean running;
	}
}

