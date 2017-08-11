package jh.slm.calculations;

import jh.slm.calculations.Fft.State;


public class Intensity{

	public Object Input1;
	public Object Input2;
	public Object Output;
	State state;

	public Intensity(int length){
		state = (new Fft()).new State();
		state.M = 10;
		state.N = 1 << state.M;
		state.W = Allocator.Allocate(state.N);
		state.delayLine = Allocator.Allocate(state.N*2);
		state.running = true;
		Fft.Init(state);
		Output = Allocator.Allocate((int)Math.round((state.N / 2.56) + 1) * 4 + 2);
		Fft.ResetIntensity(Output, state);
		state.count = 0;
		state.numberOfAverages = 20;
	}

	public void Free(){
		Allocator.Free(state.W);
		Allocator.Free(state.delayLine);
		Allocator.Free(Output);
	}
	
	
public void Connect(Object input1, Object input2) {
		Input1 = input1;
		Input2 = input2;
	}

	public void Compute() {
		Fft.Intensity(Input1, Input2, Output, state);
	}

	public void Reset() {
		Fft.ResetIntensity(Output, state);
	}

	public void PC() {
		Fft.PC(state);
	}

	public void SetAveraging(int numberOfAverages) {
		state.numberOfAverages = numberOfAverages;
	}
}
