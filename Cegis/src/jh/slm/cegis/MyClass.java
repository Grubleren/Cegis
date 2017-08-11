package jh.slm.cegis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import jh.slm.calculations.RMSDetector.Averaging;
import jh.slm.commonaclasses.MyBaseClass;
import jh.slm.commonaclasses.MyEditText;
import jh.slm.commonaclasses.MyEditText.MyEditTextListener;
import jh.slm.commonaclasses.SpectrumView;
import jh.slm.commonaclasses.Xaxis;
import jh.slm.commonaclasses.Xaxis.XaxisType;
import jh.slm.commonaclasses.Yaxis;
import jh.slm.commonclasses.Machine;
import jh.slm.commonclasses.Simulator;
import jh.slm.interfaces.ILANCom;
import jh.slm.interfaces.ISoundCard;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
// Cegis
public class MyClass extends MyBaseClass{

	Machine machine;
	private ArrayBlockingQueue<short[]> queue;
	File setupFile;
	int nFftAverages = 20;
	double sysGain = 0.0;
	Thread machineThread;
	Thread connectThread;
	
	static private MyClass instance = null;

	static public MyClass Instance(MainActivity activity){
		if (instance == null)
		{
			instance = new MyClass();
			instance.activity = activity;
			instance.InitViews(activity);
		}
		return instance;
	}

	@Override
	protected void Startup(){
		textView11.setText(activity.getString(R.string.las));

		File setupDir = activity.getFilesDir();
		setupFile = new File(setupDir, "Setup.dat");
		if (!setupFile.exists()){	
			try {
				setupFile.createNewFile();
				FileOutputStream stream = new FileOutputStream(setupFile);
				DataOutputStream  writer = new DataOutputStream(stream);
				writer.writeInt(nFftAverages);
				writer.writeDouble(sysGain);
				writer.writeFloat(yAxis3.lowLevel);
				writer.writeFloat(yAxis3.range);
				writer.writeFloat(yAxis1.lowLevel);
				writer.writeFloat(yAxis1.range);
				writer.writeFloat(yAxisFft.lowLevel);
				writer.writeFloat(yAxisFft.range);
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			FileInputStream stream = new FileInputStream(setupFile);
			DataInputStream reader = new DataInputStream(stream);
			nFftAverages = reader.readInt();
			sysGain = reader.readDouble();
			float l = reader.readFloat();
			float r = reader.readFloat();
			float h = l + r;
			int t = Math.round(r / 10 + 1);
			yAxis3.highLevel = h;
			yAxis3.nTics = t;
			yAxis3.invalidate();
			spectrumView3.ScalingHappened((int)l * 100,  (int)r * 100);
			l = reader.readFloat();
			r = reader.readFloat();
			h = l + r;
			t = Math.round(r / 10 + 1);
			yAxis1.highLevel = h;
			yAxis1.nTics = t;
			yAxis1.invalidate();
			spectrumView1.ScalingHappened((int)l * 100,  (int)r * 100);
			l = reader.readFloat();
			r = reader.readFloat();
			h = l + r;
			t = Math.round(r / 10 + 1);
			yAxisFft.highLevel = h;
			yAxisFft.nTics = t;
			yAxisFft.invalidate();
			fftSpectrumView.ScalingHappened((int)l * 100,  (int)r * 100);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.Startup();

		int fs = 48000;
		int recordLength = 200;
		int length = fs * recordLength / 1000;

		ILANCom lanCom = new LANCom();
		queue = lanCom.GetQueue();

		ISoundCard soundCard = new SoundCard(fs, length);

		fftAveraging.setText(String.valueOf(nFftAverages));
		systemGain.setText(String.valueOf(sysGain));
		machine = new Machine(lanCom, soundCard, fs, recordLength, length, nFftAverages, sysGain);

		machineThread = new Thread(machine);
		machineThread.setName("Ma");
		machineThread.start();

		while (!machine.running)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		running = true;
		connectThread = new Thread(new Connect());
		connectThread.setName("CO");
		connectThread.start();

		StartTimer();

	}

	@Override
	protected void Shutdown(){
		File setupDir = activity.getFilesDir();
		setupFile = new File(setupDir, "Setup.dat");
		try {
			FileOutputStream stream = new FileOutputStream(setupFile);
			DataOutputStream  writer = new DataOutputStream(stream);
			writer.writeInt(nFftAverages);
			writer.writeDouble(sysGain);
			writer.writeFloat(yAxis3.lowLevel);
			writer.writeFloat(yAxis3.range);
			writer.writeFloat(yAxis1.lowLevel);
			writer.writeFloat(yAxis1.range);
			writer.writeFloat(yAxisFft.lowLevel);
			writer.writeFloat(yAxisFft.range);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			running = false;
			connectThread.join();
			machine.running = false;
			machineThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.Shutdown();

	}

	@Override
	protected void Reset(){
		if (machine == null)
			return;
		machine.Reset();
	}

	@Override
	protected void PC(){
		if (machine == null)
			return;

		machine.PC();
	}

	public class Keyboard_Listener implements MyEditTextListener{
		@Override
		public void onKey(final MyEditText sender, String event) {
			if (machine == null)
				return;
			try{
				if (sender == fftAveraging){
					nFftAverages = Integer.parseInt(event);
					machine.SetFftAveraging(nFftAverages);
				}
				else if (sender == systemGain){
					sysGain = Double.parseDouble(event);
					machine.SetSystemGain(sysGain);
					onReset();
				}
				machine.Reset();
					}
			catch(Exception e){

			}
		}
	}

	@Override
	public FS onFS(FS fs)
	{
		if (machine == null)
			return fs;

		switch(fs)
		{
		case Fast:
			machine.SetRmsAveraging(Averaging.Fast);
			textView11.setText("LAF");
			return FS.Slow;
		case Slow:
			machine.SetRmsAveraging(Averaging.Slow);
			textView11.setText("LAS");
			return FS.Fast;
		}

		return FS.Slow;
	}

	public MLN onMLN(MLN mln)
	{
		switch(mln)
		{
		case Max:
			textView31.setText("LAN1");
			return MLN.Ln1;
		case Ln1:
			textView31.setText("LAN50");
			return MLN.Ln2;
		case Ln2:
			textView31.setText("LAN99");
			return MLN.Ln3;
		case Ln3:
			textView31.setText("LAMax");
			return MLN.Max;
		}

		return MLN.Max;
	}


	public void InitViews(MainActivity activity) {

		this.activity = activity;

		textView11 = (TextView)activity.findViewById(R.id.textView11);
		textView31 = (TextView)activity.findViewById(R.id.textView31);
		textView12 = (TextView)activity.findViewById(R.id.textView12);
		textView22 = (TextView)activity.findViewById(R.id.textView22);
		textView32 = (TextView)activity.findViewById(R.id.textView32);
		textView42 = (TextView)activity.findViewById(R.id.textView42);
		textView5 = (TextView)activity.findViewById(R.id.textView5);
		textView6 = (TextView)activity.findViewById(R.id.textView6);

		button1 = (Button)activity.findViewById(R.id.button1);
		button2 = (Button)activity.findViewById(R.id.button2);

		yAxis3 = (Yaxis)activity.findViewById(R.id.view01);
		yAxis1 = (Yaxis)activity.findViewById(R.id.view02);
		yAxisFft = (Yaxis)activity.findViewById(R.id.view03);

		yAxis1.setVisibility(View.INVISIBLE);
		yAxisFft.setVisibility(View.INVISIBLE);
		
		yAxis3.Init(2000, 6000);
		yAxis1.Init(2000, 6000);
		yAxisFft.Init(1000, 6000);

		xAxis3 = (Xaxis)activity.findViewById(R.id.view11);
		xAxis1 = (Xaxis)activity.findViewById(R.id.view12);
		xAxisFft = (Xaxis)activity.findViewById(R.id.view13);

		xAxis1.setVisibility(View.INVISIBLE);
		xAxisFft.setVisibility(View.INVISIBLE);
		
		xAxis3.Init(XaxisType.Cpb, 2000, 6000);
		xAxis1.Init(XaxisType.Cpb, 2000, 6000);
		xAxisFft.Init(XaxisType.Lin, 1000, 6000);

		spectrumView3 = (SpectrumView)activity.findViewById(R.id.view1);
		spectrumView1 = (SpectrumView)activity.findViewById(R.id.view2);
		fftSpectrumView = (SpectrumView)activity.findViewById(R.id.view3);
		
		spectrumView1.setVisibility(View.INVISIBLE);
		fftSpectrumView.setVisibility(View.INVISIBLE);

		fftAveraging = (MyEditText)activity.findViewById(R.id.editText5);
		systemGain = (MyEditText)activity.findViewById(R.id.editText6);

		spectrumView3.Connect(this, Outputs.rmsSpectrum3, Outputs.leqSpectrum3, noSpectralLines3, 2000, 6000);
		spectrumView1.Connect(this, Outputs.rmsSpectrum1, Outputs.leqSpectrum1, noSpectralLines1, 2000, 6000);
		fftSpectrumView.Connect(this, null, Outputs.fftSpectrum, noFftSpectralLines, 1000, 6000);

		Keyboard_Listener klFft = new Keyboard_Listener();
		fftAveraging.setOnMyEditTextListener(klFft);

		Keyboard_Listener klSysGain = new Keyboard_Listener();
		systemGain.setOnMyEditTextListener(klSysGain);

		yAxis3.addListener(spectrumView3);
		yAxis1.addListener(spectrumView1);
		yAxisFft.addListener(fftSpectrumView);

	}


	private class Connect implements Runnable{
		public Connect(){
		}
		public void run(){
			while (running){
				short[] buffer = null;
				try {
					buffer = queue.take();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				int count = 0;
				if (buffer != null){
					lock.lock();
					for (int i = 0; i < noSpectralLines3; i++){
						Outputs.rmsSpectrum3[i] = buffer[count];
						count++;
						Inputs.maxSpectrum3[i] = buffer[count];
						count++;
					}

					for (int i = 0; i < noSpectralLines3; i++){
						Inputs.leqSpectrum3[i] = buffer[count];
						count++;
					}

					for (int i = 0; i < noSpectralLines1; i++){
						Outputs.rmsSpectrum1[i] = buffer[count];
						count++;
						Inputs.maxSpectrum1[i] = buffer[count];
						count++;
					}

					for (int i = 0; i < noSpectralLines1; i++){
						Inputs.leqSpectrum1[i] = buffer[count];
						count++;
					}

					Outputs.rms = buffer[count];
					count++;
					Inputs.max = buffer[count];
					count++;
					Outputs.ln1 = buffer[count];
					count++;
					Outputs.ln2 = buffer[count];
					count++;
					Outputs.ln3 = buffer[count];
					count++;
					Inputs.leq = buffer[count];
					count++;
					Inputs.peak = buffer[count];
					count++;

					for (int i = 0; i < noFftSpectralLines; i++){
						Outputs.fftSpectrum[i] = buffer[count];
						count++;
					}
					
					functionBlock.Compute();

					//					long[] t = machine.Timing();
					//					Log.v("Jens", String.valueOf(t[2]));

					lock.unlock();

					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Log.v("jens","Connect Not Running");

		}
	}
}

