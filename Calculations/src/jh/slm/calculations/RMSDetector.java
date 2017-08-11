package jh.slm.calculations;

public class RMSDetector {

	public enum Averaging
	{
		Fast,
		Slow
	}

	public Object Input;
	public Object Output;
	public Object rmsOutput;
	private BiQuad biQuad1;
	private BiQuad biQuad2;
	int[] stateInt;
	private Object state;
	private long[] dose;
	private long[] doseMax;
	private int[] rms;
	private int noOutputs;

	public RMSDetector(int noOutputs, int inputLength){
		this.noOutputs = noOutputs;
		Output = Allocator.Allocate(2 * noOutputs);
		rmsOutput = Allocator.Allocate(inputLength * 2);
		dose = new long[noOutputs];
		doseMax = new long[noOutputs];
		rms = new int[2 * noOutputs];
		biQuad1 = new BiQuad();
		biQuad2 = new BiQuad();

		biQuad1.a1 = -0.992;
		biQuad1.a2 =      0;
		biQuad1.b0 =  0.008;
		biQuad1.b1 =      0;
		biQuad1.b2 =      0;

		biQuad2.a1 = -0.999;
		biQuad2.a2 =      0;
		biQuad2.b0 =  0.001;
		biQuad2.b1 =      0;
		biQuad2.b2 =      0;

		int size = 1 + (5 + 2);
		stateInt = new int[size];

		stateInt[0] = 1;
		stateInt[6] = 0;
		stateInt[7] = 0;

		SetBiQuad(biQuad2);
	}

	public void Free(){
		Allocator.Free(Output);
		Allocator.Free(rmsOutput);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		Detectors.Rms(Input, rmsOutput, dose, doseMax, state, noOutputs);
		for (int i = 0; i < noOutputs; i++){
			rms[i] = (int)(100 * 10 * Math.log10(dose[i]));
		}
		for (int i = 0; i < noOutputs; i++){
			rms[i + noOutputs] = (int)(100 * 10 * Math.log10(doseMax[i]));
		}
		Allocator.Copy(rms, Output);
	}

	public void SetAveraging(Averaging averaging){
		if (averaging == Averaging.Fast)
			SetBiQuad(biQuad1);
		else
			SetBiQuad(biQuad2);
	}

	private void SetBiQuad(BiQuad biQuad) {

		int noBits = 16;
		stateInt[1] = (int)(biQuad.a1 * (1 << noBits)); 
		stateInt[2] = (int)(biQuad.a2 * (1 << noBits)); 
		stateInt[3] = (int)(biQuad.b0 * (1 << noBits)); 
		stateInt[4] = (int)(biQuad.b1 * (1 << noBits));
		stateInt[5] = (int)(biQuad.b2 * (1 << noBits));
		state = Iirfilter.BiQuads(stateInt);
	}
}
