package jh.slm.calculations;

import jh.slm.calculations.Fft.State;


public class AutoSpectrumOverlap{

	public Object Input;
	public Object Output;
	public State state;

	public AutoSpectrumOverlap(int length, int nFftSpectralLines, int M){
		state = (new Fft()).new State();
		state.M = M;
		state.N = 1 << state.M;
		state.W = Allocator.Allocate(state.N);
		state.delayLine = Allocator.Allocate(state.N + length);
		state.running = true;
		state.overlap = 67;
		state.step = state.N * (100 - state.overlap) / 100;
		state.saved = 0;
		Fft.Init(state);
		Output = Allocator.Allocate((nFftSpectralLines + 1) * 2);
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
		Fft.AutoSpectrumOverlap(Input, Output, state);
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
