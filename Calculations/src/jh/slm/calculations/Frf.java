package jh.slm.calculations;

import jh.slm.calculations.Fft.State;

public class Frf {
	public Object Input1, Input2, Input3;
	public Object Output1, Output2, Output3;
	State state;

	public Frf(int nFftSpectralLines, int M){
		state = (new Fft()).new State();
		state.M = M;
		state.N = 1 << state.M;
		state.W = Allocator.Allocate(state.N);
		Fft.Init(state);
		Output1 = Allocator.Allocate(nFftSpectralLines * 2*2 + 2);
		Output2 = Allocator.Allocate(state.N*2 + 2);
		Output3 = Allocator.Allocate(nFftSpectralLines*2 + 2);
	}

	public void Free(){
		Allocator.Free(state.W);
		Allocator.Free(Output1);
		Allocator.Free(Output2);
		Allocator.Free(Output3);
	}
	
	public void Connect(Object input1, Object input2, Object input3) {
		Input1 = input1;
		Input2 = input2;
		Input3 = input3;
	}

	public void Compute() {
		Fft.FrfImpCoh(Input1, Input2, Input3, Output1, Output2, Output3, state);
	}

}
