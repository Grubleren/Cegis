package jh.slm.commonaclasses;



import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyBaseClass {

	public enum Selection{
		ThirdOctave,
		Octave,
		Fft
	}

	public enum On_Off{
		On,
		Off
	}

	public enum FS{
		Fast,
		Slow
	}

	public enum MLN{
		Max,
		Ln1,
		Ln2,
		Ln3
	}

	protected Activity activity;
	protected TextView textView11;
	protected TextView textView31;
	protected TextView textView12;
	protected TextView textView22;
	protected TextView textView32;
	protected TextView textView42;
	protected TextView textView5;
	protected TextView textView6;
	protected Button button1;
	protected Button button2;
	protected SpectrumView spectrumView3;
	protected SpectrumView spectrumView1;
	protected SpectrumView fftSpectrumView;
	protected Yaxis yAxis3;
	protected Yaxis yAxis1;
	protected Yaxis yAxisFft;
	protected Xaxis xAxis3;
	protected Xaxis xAxis1;
	protected Xaxis xAxisFft;
	protected MyEditText fftAveraging;
	protected MyEditText systemGain;;


	protected class DisplayData{
		public int[]rmsSpectrum3;
		public int[]maxSpectrum3;
		public int[]leqSpectrum3;
		public int[]rmsSpectrum1;
		public int[]maxSpectrum1;
		public int[]leqSpectrum1;
		public int rms;
		public int max;
		public int ln1;
		public int ln2;
		public int ln3;
		public int leq;
		public int peak;
		public int common;
		public int[]fftSpectrum;
	}

	protected DisplayData Inputs;
	protected DisplayData Outputs;

	protected FunctionBlock functionBlock;

	protected int noSpectralLines3;
	protected int noSpectralLines1;
	protected int noFftSpectralLines;
	protected ReentrantLock lock;

	private String cursorFrequency = "- - -";
	private int cursorLeqValue = Short.MIN_VALUE;

	protected boolean running;
	private Timer timer;
	
	public Selection selection = Selection.Octave;
	public On_Off on_off = On_Off.On;
	public FS fs = FS.Slow;
	public MLN mln = MLN.Max;
	
	protected MyBaseClass() {
		noSpectralLines3 = 27;
		noSpectralLines1 = 9;
		noFftSpectralLines = 401;

		Inputs = new DisplayData();
		Inputs.maxSpectrum3 = new int[noSpectralLines3];
		Inputs.maxSpectrum1 = new int[noSpectralLines1];
		Inputs.leqSpectrum3 = new int[noSpectralLines3];
		Inputs.leqSpectrum1 = new int[noSpectralLines1];

		Outputs = new DisplayData();
		Outputs.rmsSpectrum3 = new int[noSpectralLines3];
		Outputs.maxSpectrum3 = new int[noSpectralLines3];
		Outputs.leqSpectrum3 = new int[noSpectralLines3];
		Outputs.rmsSpectrum1 = new int[noSpectralLines1];
		Outputs.maxSpectrum1 = new int[noSpectralLines1];
		Outputs.leqSpectrum1 = new int[noSpectralLines1];
		Outputs.fftSpectrum = new int[noFftSpectralLines];
		functionBlock = new FunctionBlock(Inputs, Outputs);
	}

	protected void StartTimer(){
		lock = new ReentrantLock();
		MyTimerTask myTimerTask = new MyTimerTask(new Handler(), lock);
		timer = new Timer();
		timer.schedule(myTimerTask, 0, 200);
	}
	
	protected void Reset(){
	}
	
	protected void PC(){
	}
	
	public void onReset(){
		functionBlock.Reset();
		Reset();
	}

	public void onPC(){
		functionBlock.setPause(!functionBlock.getPause());
		PC();
	}

	public MyBaseClass.Selection onSwap(Selection selection){
		switch(selection)
		{
		case ThirdOctave:
			yAxis3.setVisibility(View.VISIBLE);
			yAxis1.setVisibility(View.INVISIBLE);
			yAxisFft.setVisibility(View.INVISIBLE);
			xAxis3.setVisibility(View.VISIBLE);
			xAxis1.setVisibility(View.INVISIBLE);
			xAxisFft.setVisibility(View.INVISIBLE);
			spectrumView3.setVisibility(View.VISIBLE);
			spectrumView1.setVisibility(View.INVISIBLE);
			fftSpectrumView.setVisibility(View.INVISIBLE);
			button2.setText("CPB1");
			return Selection.Octave;
		case Octave:
			yAxis3.setVisibility(View.INVISIBLE);
			yAxis1.setVisibility(View.VISIBLE);
			yAxisFft.setVisibility(View.INVISIBLE);
			xAxis3.setVisibility(View.INVISIBLE);
			xAxis1.setVisibility(View.VISIBLE);
			xAxisFft.setVisibility(View.INVISIBLE);
			spectrumView3.setVisibility(View.INVISIBLE);
			spectrumView1.setVisibility(View.VISIBLE);
			fftSpectrumView.setVisibility(View.INVISIBLE);
			button2.setText("FFT");
			return Selection.Fft;
		case Fft:
			yAxis3.setVisibility(View.INVISIBLE);
			yAxis1.setVisibility(View.INVISIBLE);
			yAxisFft.setVisibility(View.VISIBLE);
			xAxis3.setVisibility(View.INVISIBLE);
			xAxis1.setVisibility(View.INVISIBLE);
			xAxisFft.setVisibility(View.VISIBLE);
			spectrumView3.setVisibility(View.INVISIBLE);
			spectrumView1.setVisibility(View.INVISIBLE);
			fftSpectrumView.setVisibility(View.VISIBLE);
			button2.setText("CPB3");
			return Selection.ThirdOctave;
		}
		return Selection.ThirdOctave;
	}

	protected void Startup()
	{
		functionBlock.Reset();
		functionBlock.setPause(true);
	}
	
	protected void Shutdown()
	{
		timer.cancel();
	}
	
	public On_Off onOn_Off(On_Off on_off)
	{
		switch(on_off)
		{
		case On:
			Startup();
			button1.setText("Stop");
			return On_Off.Off;
		case Off:
			Shutdown();
			button1.setText("Start");
			return On_Off.On;
		}
		
		return On_Off.Off;
	}
	
	public FS onFS(FS fs)
	{
		return FS.Slow;
	}
	
	public MLN onMLN(MLN mln)
	{
		return MLN.Max;
	}
	
	public void SetCursorFrequency(String cursorFrequency)
	{
		this.cursorFrequency = cursorFrequency;
	}

	public void SetCursorLeqValue(int cursorLeqValue)
	{
		this.cursorLeqValue = cursorLeqValue;
	}

	public class BBRunnable implements Runnable{
		TextView textView;
		String data;
		public BBRunnable(TextView textView, String data){
			this.textView = textView;
			this.data = data;
		}
		public void run()    	{
			textView.setText(data);
		}
	}

	private class SpectrumRunnable implements Runnable{
		public void run(){
			spectrumView3.invalidate();
			spectrumView1.invalidate();
			fftSpectrumView.invalidate();
		}
	}

	private String DataToString(int data)
	{
		String text;
		if (data != Short.MIN_VALUE)
			text = String.format("%.1f", data / 100.0)+ " dB";
		else
			text = "- - -";
		return text;
	}

	private class MyTimerTask extends TimerTask{
		Handler handler;
		ReentrantLock lock;
		int count = 0;

		public MyTimerTask(Handler handler, ReentrantLock lock){
			this.handler = handler;
			this.lock = lock;
		}

		@Override
		public void run() {
			lock.lock();

			handler.post(new SpectrumRunnable());
			if (count == 0){
				handler.post(new BBRunnable(textView12, DataToString(Outputs.rms)));
				handler.post(new BBRunnable(textView22, DataToString(Outputs.leq)));
				handler.post(new BBRunnable(textView32, DataToString(Outputs.common)));
				handler.post(new BBRunnable(textView42, DataToString(Outputs.peak)));
				handler.post(new BBRunnable(textView5, cursorFrequency));
				handler.post(new BBRunnable(textView6, DataToString(cursorLeqValue)));
			}
			count++;
			count %= 5;

			lock.unlock();
		}
	}


	protected class FunctionBlock{
		public FunctionBlock(DisplayData dataIn, DisplayData dataOut){
			this.dataIn = dataIn;
			this.dataOut = dataOut;
			noSpectralLines3 = dataIn.leqSpectrum3.length;
			doseSpectrum3 = new double[noSpectralLines3];
			noSpectralLines1 = dataIn.leqSpectrum1.length;
			doseSpectrum1 = new double[noSpectralLines1];
		}

		public void Compute(){
			if (running)    		{
				dose += Math.pow(10, dataIn.leq / 1000.0);
				for (int i = 0; i < noSpectralLines3; i++)
					doseSpectrum3[i] += Math.pow(10, dataIn.leqSpectrum3[i] / 1000.0);

				for (int i = 0; i < noSpectralLines1; i++)
					doseSpectrum1[i] += Math.pow(10, dataIn.leqSpectrum1[i] / 1000.0);

				rmsMax = Math.max(rmsMax, dataIn.max);
				peakMax = Math.max(peakMax, dataIn.peak);
				count++;

				if (count % 5 == 0)
				{
					dataOut.peak = peakMax;
					switch(mln)
					{
					case Max:
						dataOut.common = rmsMax;
						break;
					case Ln1:
						dataOut.common = dataOut.ln1;
						break;
					case Ln2:
						dataOut.common = dataOut.ln2;
						break;
					case Ln3:
						dataOut.common = dataOut.ln3;
						break;
					
					}
					dataOut.max = rmsMax;
					dataOut.leq = (int)(100 * 10 * Math.log10(dose / count));
					for (int i = 0; i < noSpectralLines3; i++)
						dataOut.leqSpectrum3[i] = (int)(100 * 10 * Math.log10(doseSpectrum3[i] / count));

					for (int i = 0; i < noSpectralLines1; i++)
						dataOut.leqSpectrum1[i] = (int)(100 * 10 * Math.log10(doseSpectrum1[i] / count));

				}
			}
		}

		public void Reset(){
			dose = 0;
			for (int i = 0; i < noSpectralLines3; i++)
				doseSpectrum3[i] = 0;
			for (int i = 0; i < noSpectralLines1; i++)
				doseSpectrum1[i] = 0;
			count = 0;
			rmsMax = Short.MIN_VALUE;
			peakMax = Short.MIN_VALUE;
			dataOut.leq = Short.MIN_VALUE;
			dataOut.common = Short.MIN_VALUE;
			dataOut.peak = Short.MIN_VALUE;
			for (int i = 0; i < noSpectralLines3; i++)
				dataOut.leqSpectrum3[i] = Short.MIN_VALUE;
			for (int i = 0; i < noSpectralLines1; i++)
				dataOut.leqSpectrum1[i] = Short.MIN_VALUE;
		}

		public Boolean getPause(){
			return running;
		}

		public void setPause(Boolean value){
			running = value;
		}

		DisplayData dataIn;
		public DisplayData dataOut;
		Boolean running = true;
		double dose;
		int count;
		double[] doseSpectrum3;
		double[] doseSpectrum1;

		int rmsMax;
		int peakMax;
		int noSpectralLines3;
		int noSpectralLines1;
	}
}
