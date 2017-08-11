package jh.slm.calculations;

import jh.slm.calculations.Statistics.State;

public class StatisticsDetector {

	public Object Input;
	public Object Output;
	private int[] distribution;
	private int[] lnValues;
	private float[] lnPercentages;
	private int[] range;
	int nOutputs = 3;
	State state;

	public StatisticsDetector(){
		state = (new Statistics()).new State();
		state.running = true;
		int lower = 6000;
		int upper = 14000;
		int classWidth = 100;
		int nClasses = (upper - lower) / classWidth;
		range = new int[3];
		range[0] = lower;
		range[1]= upper;
		range[2]= classWidth;
		Output = Allocator.Allocate(3);
		lnValues = new int[3];
		lnPercentages = new float[3];
		lnPercentages[0] = 0.01f;
		lnPercentages[1] = 0.5f;
		lnPercentages[2] = 0.99f;
		distribution = new int[nClasses + 3];
		Statistics.Reset(distribution, range);
	}
	
	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		Statistics.Distribution(Input, distribution, lnValues, lnPercentages, range, state);
		
		Allocator.Copy(lnValues, Output);
	}
	
	public void Reset()
	{
		Statistics.Reset(distribution, range);
	}

	public void PC()
	{
		Statistics.PC(state);
	}
}
