package jh.slm.calculations;

public class SquarerBank {

	private Squarer[] squarers;
	public Object[] Output;
	int noSquarers;

	public SquarerBank(int outputLength, int noSquarers) {
		this.noSquarers = noSquarers;
		squarers = new Squarer[noSquarers];
		Output = new Object[noSquarers];
		for (int i = 0; i< noSquarers; i++){
			squarers[i] = new Squarer(outputLength);
			Output[i] = squarers[i].Output;
		}
	}

	public void Free(){
		for (int i = 0; i< noSquarers; i++){
			squarers[i].Free();
		}
	}
	
	public void Connect(Object[] input) {
		for (int i = 0; i< noSquarers; i++){
			squarers[i].Connect(input[i]);
		}
	}

	public void Compute() {
		for (int i = 0; i< noSquarers; i++){
			squarers[i].Compute();
		}
	}
}
