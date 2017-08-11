package jh.slm.calculations;

public class Allocator {
	static { System.loadLibrary("allocator"); }
	public static native Object Allocate(int size);
	public static native void Free(Object data);
	public static native int[] DeRefInt(Object data);
	public static native long[] DeRefLong(Object data);
	public static native void Copy(int[] dataIn, Object dataOut);
	public static native void CopyBytes(byte[] dataIn, Object dataOut);
	public static native void Copy16To32(byte[] dataIn, Object dataOut);
	public static native void Copy24To32(byte[] dataIn, Object dataOut);
}
