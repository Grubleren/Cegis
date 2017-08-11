package jh.slm.calculations;

public class Resampling4_5 {

	public Object Input;
	public Object Output;
	Object state;
	int[] coef;
	int length;

	public Resampling4_5(int length){
		this.length = length;
		int up = 4;
		int down = 5;
		Output = Allocator.Allocate(length * up / down);
		coef = Coef();
		state = Resampling.Init(down, up, coef);
	}

	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		Resampling.Resample(length, Input, Output, state);
	}

	public int[] GetOutput()
	{
		return Allocator.DeRefInt(Output);
	}

	private int[] Coef(){
		return new int[]{
				-188369,
				-577964,
				-1217410,
				-1948012,
				-2398092,
				-2041237,
				-403564,
				2623279,
				6486676,
				9870605,
				10937983,
				7956123,
				166081,
				-11413503,
				-23630832,
				-31595110,
				-29880403,
				-14272916,
				16485744,
				59611772,
				108358653,
				153403894,
				185216952,
				196696028,
				185216952,
				153403894,
				108358653,
				59611772,
				16485744,
				-14272916,
				-29880403,
				-31595110,
				-23630832,
				-11413503,
				166081,
				7956123,
				10937983,
				9870605,
				6486676,
				2623279,
				-403564,
				-2041237,
				-2398092,
				-1948012,
				-1217410,
				-577964,
				-188369,
				0
		};
	}

}
