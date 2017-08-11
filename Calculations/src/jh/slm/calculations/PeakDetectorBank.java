package jh.slm.calculations;

public class PeakDetectorBank {

	private PeakDetector[] peakDetectors;
	public Object[] Output;
	int noDetectors;

	public PeakDetectorBank(int numberOfDetectors) {
		this.noDetectors = numberOfDetectors;
		peakDetectors = new PeakDetector[numberOfDetectors];
		Output = new Object[numberOfDetectors];
		for (int i = 0; i< numberOfDetectors; i++){
			peakDetectors[i] = new PeakDetector();
			Output[i] = peakDetectors[i].Output;
		}
	}

	public void Free() {
		for (int i = 0; i< noDetectors; i++){
			peakDetectors[i].Free();
		}
	}

	public void Connect(Object[] input) {
		for (int i = 0; i< noDetectors; i++){
			peakDetectors[i].Connect(input[i]);
		}
	}

	public void Compute() {
		for (int i = 0; i< noDetectors; i++){
			peakDetectors[i].Compute();
		}
	}
}
