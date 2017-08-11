package jh.slm.calculations;

public class Squarer {

	public Object Input;
	public Object Output;

	public Squarer(int outputLength){
		Output = Allocator.Allocate(outputLength * 2);
	}

	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		Detectors.Square(Input, Output);
	}
}
