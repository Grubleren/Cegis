package jh.slm.calculations;

public class Fft {
	static { System.loadLibrary("fft"); }
	public static native void Init(State state);
	public static native void AutoSpectrum(Object input, Object output, State state);
	public static native void FftForward(Object input, Object output, State state);
	public static native void TwoChSpectra(Object input1, Object input2, Object output1, Object output2, Object output21, State state);
	public static native void Auto(Object input, Object instSpectrumOut, Object autoSpectrumOut, State state);
	public static native void Cross(Object instSpectrum1, Object instSpectrum2, Object autoSpectrum1, Object autoSpectrum2, Object crossSpectrumOut, State state1, State state2);
	public static native void Reset(Object output, State state);
	public static native void ResetTwoCh(Object output1, Object output2, Object output21, State state);
	public static native void ResetCross(Object crossSpectrum, State state);
	public static native void PC(State state);
	public static native void Intensity(Object input1, Object input2, Object output, State state);
	public static native void ResetIntensity(Object output, State state);
	public static native void AutoSpectrumOverlap(Object input, Object output, State state);
	public static native void CrossSpectrumOverlap(Object input1, Object input2, Object output, State state);
	public static native void FrfImpCoh(Object input1, Object input2, Object input3, Object output1, Object output2, Object output3, State state);
	
	public class State
	{
		public int M;
		public int N;
		public Object W;
		public Object delayLine;
		public Object delayLineX;
		public int numberOfAverages;
		public int count;
		public int blockExponent;
		public boolean running;
		public int overlap;
		public int step;
		public int saved;
	}
}
