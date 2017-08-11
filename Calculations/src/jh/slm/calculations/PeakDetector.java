package jh.slm.calculations;

public class PeakDetector {

	public Object Input;
	public Object Output;
	int[] peak;
	int max = 0;
	
	public PeakDetector(){
		Output = Allocator.Allocate(1);
		peak = new int[1];
	}

	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		max = Detectors.Peak(Input, 0);
		peak[0] = (int)(100 * 20 * Math.log10(max));
		Allocator.Copy(peak, Output);
		//System.out.println(peak[0]);
	}
}
