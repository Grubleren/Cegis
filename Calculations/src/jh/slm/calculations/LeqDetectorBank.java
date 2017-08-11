package jh.slm.calculations;

public class LeqDetectorBank {

	private LeqDetector[] leqDetectors;
	public Object[] Output;
	int noDetectors;

	public LeqDetectorBank(int numberOfLeqDetectors) {
		this.noDetectors = numberOfLeqDetectors;
		leqDetectors = new LeqDetector[numberOfLeqDetectors];
		Output = new Object[numberOfLeqDetectors];
		for (int i = 0; i< numberOfLeqDetectors; i++){
			leqDetectors[i] = new LeqDetector();
			Output[i] = leqDetectors[i].Output;
		}
	}

	public void Free() {
		for (int i = 0; i< noDetectors; i++){
			leqDetectors[i].Free();
		}
	}

	public void Connect(Object[] input) {
		for (int i = 0; i< noDetectors; i++){
			leqDetectors[i].Connect(input[i]);
		}
	}

	public void Compute() {
		for (int i = 0; i< noDetectors; i++){
			leqDetectors[i].Compute();
		}
	}
}
