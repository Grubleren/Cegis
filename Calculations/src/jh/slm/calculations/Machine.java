package jh.slm.calculations;

import jh.slm.interfaces.ILANCom;
import jh.slm.interfaces.ISoundCard;

public class Machine  implements Runnable {

	ISoundCard soundCard;
	ISoundCard actualSoundCard;
	AWeighting aWeighting; 
	CWeighting cWeighting;
	Resampling2000 cpbResampling;
	CPBFilterbank cpbFilters3;
	SquarerBank squarersSpectra3;
	RMSDetectorBank rmsDetectorsSpectra3;
	LeqDetectorBank leqDetectorsSpectra3;
	CPBFilterbank cpbFilters1;
	SquarerBank squarersSpectra1;
	RMSDetectorBank rmsDetectorsSpectra1;
	LeqDetectorBank leqDetectorsSpectra1;
	CPBFilterbank cpbFilters3_2000;
	SquarerBank squarersSpectra3_2000;
	RMSDetectorBank rmsDetectorsSpectra3_2000;
	LeqDetectorBank leqDetectorsSpectra3_2000;
	CPBFilterbank cpbFilters1_2000;
	SquarerBank squarersSpectra1_2000;
	RMSDetectorBank rmsDetectorsSpectra1_2000;
	LeqDetectorBank leqDetectorsSpectra1_2000;
	Squarer squarersBroadBand;
	RMSDetector rmsDetectorsBroadBand;
	StatisticsDetector statisticsDetector;
	LeqDetector leqDetectorsBroadBand;
	PeakDetector peakDetectorsBroadBand;
	Resampling16_75 upsampling;
	FFTClass fft;
	ILANCom lanCom;
	Object[] filterOutputs;
	int fs;
	int recordLength;
	int length;
	public boolean running;
	long[] ret = new long[4];
	int nAverages;
	double gain;

	public Machine(ILANCom lanCom, ISoundCard soundCard, int fs, int recordLength, int length, int nAverages, double gain){
		this.lanCom = lanCom;
		this.soundCard = soundCard;
		this.fs = fs;
		this.recordLength = recordLength;
		this.length = length;
		this.nAverages = nAverages;
		this.gain = gain;
	}

	public void run(){
		double fl = 400;
		double fu = 10000;
		double fl_2000 = 25;
		double fu_2000 = 315;

		cpbFilters3 = new CPBFilterbank(OctaveFilterType.ThirdOctave, fl, fu, fs);
		cpbFilters3.Initialize(3, length);
		squarersSpectra3 = new SquarerBank(recordLength, cpbFilters3.noFilters);
		rmsDetectorsSpectra3 = new RMSDetectorBank(1, recordLength, cpbFilters3.noFilters);
		leqDetectorsSpectra3 = new LeqDetectorBank(cpbFilters3.noFilters);
		cpbFilters1 = new CPBFilterbank(OctaveFilterType.Octave, fl, fu, fs);
		cpbFilters1.Initialize(3, length);
		squarersSpectra1 = new SquarerBank(recordLength, cpbFilters1.noFilters);
		rmsDetectorsSpectra1 = new RMSDetectorBank(1, recordLength, cpbFilters1.noFilters);
		leqDetectorsSpectra1 = new LeqDetectorBank(cpbFilters1.noFilters);
		cpbResampling = new Resampling2000(length); 
		cpbFilters3_2000 = new CPBFilterbank(OctaveFilterType.ThirdOctave, fl_2000, fu_2000, fs/24);
		cpbFilters3_2000.Initialize(3, length/24);
		squarersSpectra3_2000 = new SquarerBank(recordLength, cpbFilters3_2000.noFilters);
		rmsDetectorsSpectra3_2000 = new RMSDetectorBank(1, recordLength, cpbFilters3_2000.noFilters);
		leqDetectorsSpectra3_2000 = new LeqDetectorBank(cpbFilters3_2000.noFilters);
		cpbFilters1_2000 = new CPBFilterbank(OctaveFilterType.Octave, fl_2000, fu_2000, fs/24);
		cpbFilters1_2000.Initialize(3, length/24);
		squarersSpectra1_2000 = new SquarerBank(recordLength, cpbFilters1_2000.noFilters);
		rmsDetectorsSpectra1_2000 = new RMSDetectorBank(1, recordLength, cpbFilters1_2000.noFilters);
		leqDetectorsSpectra1_2000 = new LeqDetectorBank(cpbFilters1_2000.noFilters);
		aWeighting = new AWeighting(length); 
		cWeighting = new CWeighting(length);
		squarersBroadBand = new Squarer(recordLength);
		statisticsDetector = new StatisticsDetector();
		rmsDetectorsBroadBand = new RMSDetector(1, recordLength);
		leqDetectorsBroadBand = new LeqDetector();
		peakDetectorsBroadBand = new PeakDetector();
		upsampling = new Resampling16_75(length);
		fft  = new FFTClass(length * 16 / 15);

		actualSoundCard = soundCard;

		fft.SetAveraging(nAverages);
		lanCom.SetSystemGain(gain);
		Connect();
		Compute();
	}

	private void Connect() {
		aWeighting.Connect(actualSoundCard.Output(1));
		cWeighting.Connect(actualSoundCard.Output(1));
		cpbResampling.Connect(actualSoundCard.Output(1));
		cpbFilters3.Connect(actualSoundCard.Output(1));
		cpbFilters1.Connect(actualSoundCard.Output(1));
		cpbFilters3_2000.Connect(cpbResampling.Output);
		cpbFilters1_2000.Connect(cpbResampling.Output);
		upsampling.Connect(actualSoundCard.Output(1));
		fft.Connect(upsampling.Output);
		squarersSpectra3.Connect(cpbFilters3.Output);
		rmsDetectorsSpectra3.Connect(squarersSpectra3.Output); 
		leqDetectorsSpectra3.Connect(squarersSpectra3.Output);
		squarersSpectra1.Connect(cpbFilters1.Output);
		rmsDetectorsSpectra1.Connect(squarersSpectra1.Output); 
		leqDetectorsSpectra1.Connect(squarersSpectra1.Output);
		squarersSpectra3_2000.Connect(cpbFilters3_2000.Output);
		rmsDetectorsSpectra3_2000.Connect(squarersSpectra3_2000.Output); 
		leqDetectorsSpectra3_2000.Connect(squarersSpectra3_2000.Output);
		squarersSpectra1_2000.Connect(cpbFilters1_2000.Output);
		rmsDetectorsSpectra1_2000.Connect(squarersSpectra1_2000.Output); 
		leqDetectorsSpectra1_2000.Connect(squarersSpectra1_2000.Output);
		squarersBroadBand.Connect(aWeighting.Output);
		rmsDetectorsBroadBand.Connect(squarersBroadBand.Output);
		statisticsDetector.Connect(rmsDetectorsBroadBand.rmsOutput);
		leqDetectorsBroadBand.Connect(squarersBroadBand.Output);
		peakDetectorsBroadBand.Connect(cWeighting.Output);
		lanCom.Connect(rmsDetectorsSpectra3.Output, 1);
		lanCom.Connect(rmsDetectorsSpectra3_2000.Output, 0);
		lanCom.Connect(leqDetectorsSpectra3.Output, 3);
		lanCom.Connect(leqDetectorsSpectra3_2000.Output, 2);
		lanCom.Connect(rmsDetectorsSpectra1.Output, 5);
		lanCom.Connect(rmsDetectorsSpectra1_2000.Output, 4);
		lanCom.Connect(leqDetectorsSpectra1.Output, 7);
		lanCom.Connect(leqDetectorsSpectra1_2000.Output, 6);
		lanCom.Connect(rmsDetectorsBroadBand.Output, 8);
		lanCom.Connect(statisticsDetector.Output, 9);
		lanCom.Connect(leqDetectorsBroadBand.Output, 10);
		lanCom.Connect(peakDetectorsBroadBand.Output, 11);
		lanCom.Connect(fft.Output, 12);
	}

	private void Compute(){
		System.out.println("Start");
		running = true;
		while (running){ 
			if (lanCom.ReadyForConnection())
				lanCom.WaitForConnection();
			if (lanCom.Connected()){
				actualSoundCard.Compute();
				ret[0] = System.currentTimeMillis();
				aWeighting.Compute();
				cWeighting.Compute();
				cpbFilters3.Compute();
				cpbFilters1.Compute();
				cpbResampling.Compute();
				cpbFilters3_2000.Compute();
				cpbFilters1_2000.Compute();
				upsampling.Compute();
				fft.Compute();
				squarersSpectra3.Compute();
				rmsDetectorsSpectra3.Compute();
				leqDetectorsSpectra3.Compute();
				squarersSpectra1.Compute();
				rmsDetectorsSpectra1.Compute();
				leqDetectorsSpectra1.Compute();
				squarersSpectra3_2000.Compute();
				rmsDetectorsSpectra3_2000.Compute();
				leqDetectorsSpectra3_2000.Compute();
				squarersSpectra1_2000.Compute();
				rmsDetectorsSpectra1_2000.Compute();
				leqDetectorsSpectra1_2000.Compute();
				squarersBroadBand.Compute();
				rmsDetectorsBroadBand.Compute();
				statisticsDetector.Compute();
				leqDetectorsBroadBand.Compute();
				peakDetectorsBroadBand.Compute();
				lanCom.Compute();
				ret[1] = System.currentTimeMillis();
				ret[2] = ret[1]-ret[0];
				ret[3]++;
			}
			else 
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		soundCard.Dispose();
		Free();
	}

	public void Reset()
	{
		fft.Reset();
		statisticsDetector.Reset();
	}

	public void PC()
	{
		fft.PC();
		statisticsDetector.PC();
	}

	public void SetFftAveraging(int nAverages)
	{
		fft.SetAveraging(nAverages);
	}

	public void SetRmsAveraging(RMSDetector.Averaging averaging)
	{
		rmsDetectorsBroadBand.SetAveraging(averaging);
		rmsDetectorsSpectra1.SetAveraging(averaging);
		rmsDetectorsSpectra3.SetAveraging(averaging);
		rmsDetectorsSpectra1_2000.SetAveraging(averaging);
		rmsDetectorsSpectra3_2000.SetAveraging(averaging);
	}

	public void SetSystemGain(double gain)
	{
		lanCom.SetSystemGain(gain);
	}

	public void SetIpAddress(String ipAddress){
		soundCard.SetIpAddress(ipAddress);
	}
	
	public long[] Timing()
	{
		return ret;
	}
	
	public void Free(){
		cpbFilters3.Free();
		squarersSpectra3.Free();
		rmsDetectorsSpectra3.Free();
		leqDetectorsSpectra3.Free();
		cpbFilters1.Free();
		squarersSpectra1.Free();
		rmsDetectorsSpectra1.Free();
		leqDetectorsSpectra1.Free();
		cpbResampling.Free(); 
		cpbFilters3_2000.Free();
		squarersSpectra3_2000.Free();
		rmsDetectorsSpectra3_2000.Free();
		leqDetectorsSpectra3_2000.Free();
		cpbFilters1_2000.Free();
		squarersSpectra1_2000.Free();
		rmsDetectorsSpectra1_2000.Free();
		leqDetectorsSpectra1_2000.Free();
		aWeighting.Free(); 
		cWeighting.Free();
		squarersBroadBand.Free();
		statisticsDetector.Free();
		rmsDetectorsBroadBand.Free();
		leqDetectorsBroadBand.Free();
		peakDetectorsBroadBand.Free();
		upsampling.Free();
		fft.Free();

		actualSoundCard.Free();
	}
}