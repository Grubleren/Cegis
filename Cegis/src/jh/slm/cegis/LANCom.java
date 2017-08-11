package jh.slm.cegis;



import java.util.concurrent.ArrayBlockingQueue;

import jh.slm.calculations.Allocator;
import jh.slm.interfaces.ILANCom;


public class LANCom implements ILANCom{

	private Object[][] Input;
	public boolean connected;
	public boolean readyForConnection;
	private double systemGain;
	ArrayBlockingQueue<short[]> queue;

	public LANCom(){
		Input = new Object[13][];
		connected = false;
		System.out.println("Not connected");
		readyForConnection = true;
		queue = new ArrayBlockingQueue<short[]>(5);
	}


	public Boolean ReadyForConnection(){
		return readyForConnection;
	}

	public void WaitForConnection(){
		readyForConnection = false;
		Thread thread = new Thread(new  ConnectToHost(), "StartServer");
		thread.start();
	}

	public Boolean Connected(){
		return connected;
	}

	private class ConnectToHost implements Runnable{
		public void run(){
			System.out.println("Waiting for connection");
			connected = true;
			System.out.println("Connected");
		}
	}

	public void Connect(Object input, int i) {
		Object[] inp;
		if (input instanceof Object[])
			inp = (Object[])input;
		else
		{
			inp = new Object[1];
			inp[0] = input;
		}
		Input[i] = new Object[inp.length];
		for (int j = 0; j < inp.length; j++){
			Input[i][j] = inp[j];
		}
	}

	public void Compute() {
		short[] buffer = new short[600];
		int count = 0;
		for (int i = 0; i < Input.length; i++){
			for (int j = 0; j < Input[i].length; j++){
				if ( i == 12)
				{
					long[] dataL = Allocator.DeRefLong(Input[i][j]);
					for (int k = 0; k < dataL.length - 1; k++){
						short data = (short)(1000 *  (Math.log10(dataL[k]) + Math.log10(2) * dataL[dataL.length - 1]));
						if (data == Short.MIN_VALUE)
							data = 0;
						else
							data += -3000 + systemGain + 2709 - 2408; // 16 * sqrt(2) / 16(upfactor) 
						buffer[count] = data;
						count++;
					}
					dataL = null;
				}
				else
				{
					int[] dataI = Allocator.DeRefInt(Input[i][j]);
					for (int k = 0; k < dataI.length; k++){
						int data = dataI[k];
						if (data == Integer.MIN_VALUE)
							data = Short.MIN_VALUE;
						else
							data += -3000 + systemGain;

						buffer[count] = (short)data;
						count++;
					}
					dataI = null;
				}
			}
		}
		queue.offer(buffer);
	}


	@Override
	public ArrayBlockingQueue<short[]> GetQueue() {
		return queue;
	}

	@Override
	public void SetSystemGain(double gain){
		systemGain = (int)(100 * gain);
	}
}

