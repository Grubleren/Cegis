package jh.slm.calculations;

public class LeqDetector {

	public Object Input;
	public Object Output;
	private int[] leq;

	public LeqDetector(){
		Output = Allocator.Allocate(1);
		leq = new int[1];
	}

	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		leq[0] = (int)(100 * 10 * Math.log10(Detectors.Leq(Input)));
		Allocator.Copy(leq, Output);
		//System.out.println(leq[0]);
	}
}
