package jh.slm.calculations;

import jh.slm.calculations.RMSDetector.Averaging;

public class RMSDetectorBank {

	private RMSDetector[] rmsDetectors;
	public Object[] Output;
	public Object [] rmsOutput;
	int noDetectors;

	public RMSDetectorBank(int noOutputs, int inputLength, int numberOfRmsDetectors) {
		this.noDetectors = numberOfRmsDetectors;
		rmsDetectors = new RMSDetector[numberOfRmsDetectors];
		Output = new Object[numberOfRmsDetectors];
		rmsOutput = new Object[numberOfRmsDetectors];
		for (int i = 0; i< numberOfRmsDetectors; i++){
			rmsDetectors[i] = new RMSDetector(noOutputs, inputLength);
			Output[i] = rmsDetectors[i].Output;
			rmsOutput[i] = rmsDetectors[i].rmsOutput;
		}
	}

	public void Free(){
		for (int i = 0; i< noDetectors; i++){
			rmsDetectors[i].Free();
		}
	}
	
	public void Connect(Object[] input) {
		for (int i = 0; i< noDetectors; i++){
			rmsDetectors[i].Connect(input[i]);
		}
	}

	public void Compute() {
		for (int i = 0; i< noDetectors; i++){
			rmsDetectors[i].Compute();
		}
	}

	public void SetAveraging(Averaging averaging){
		for (int i = 0; i< noDetectors; i++){
			rmsDetectors[i].SetAveraging(averaging);
		}
		
	}

}
