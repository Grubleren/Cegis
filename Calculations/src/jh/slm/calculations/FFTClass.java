package jh.slm.calculations;

import jh.slm.calculations.Fft.State;


public class FFTClass{

	public Object Input;
	public Object Output;
	State state;

	public FFTClass(int length){
		state = (new Fft()).new State();
		state.M = 10;
		state.N = 1 << state.M;
		state.W = Allocator.Allocate(state.N);
		state.delayLine = Allocator.Allocate(state.N);
		state.running = true;
		Fft.Init(state);
		Output = Allocator.Allocate((int)Math.round((state.N / 2.56) + 1) * 2 + 2);
		Fft.Reset(Output, state);
		state.count = 0;
		state.numberOfAverages = 20;
	}

	public void Free(){
		Allocator.Free(state.W);
		Allocator.Free(state.delayLine);
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		Fft.AutoSpectrum(Input, Output, state);
	}

	public void Reset() {
		Fft.Reset(Output, state);
	}

	public void PC() {
		Fft.PC(state);
	}

	public void SetAveraging(int numberOfAverages) {
		state.numberOfAverages = numberOfAverages;
	}
}
