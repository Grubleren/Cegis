package jh.slm.calculations;

import jh.slm.calculations.Fft.State;


public class CrossSpectrumOverlap{

	public Object Input1;
	public Object Input2;
	public Object Output;
	State state;

	public CrossSpectrumOverlap(int length, int nFftSpectralLines, int M){
		state = (new Fft()).new State();
		state.M = M;
		state.N = 1 << state.M;
		state.W = Allocator.Allocate(state.N);
		state.delayLine = Allocator.Allocate((state.N + length) * 2);
		state.running = true;
		state.overlap = 67;
		state.step = state.N * (100 - state.overlap) / 100;
		state.saved = 0;
		Fft.Init(state);
		Output = Allocator.Allocate((nFftSpectralLines * 2 + 1) * 2);
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
		Fft.CrossSpectrumOverlap(Input1, Input2, Output, state);
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
