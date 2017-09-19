package jh.slm.calculations;

import jh.slm.interfaces.ISoundCard;

public class Simulator implements ISoundCard{

	public Object Output;
	private Random random;
	private int[] data;
	private int length;

	public Simulator(int length){
	this.length = length;
		Output = Allocator.Allocate(length);
		data = new int[length];
		random = new Random();
	}
	
	public void Free(){
		Allocator.Free(Output);
	}
	
public void Compute() {
		for (int i=0; i< length; i++){
			data[i] = (int)((1 << 15) * Math.sin(2 * Math.PI / length * 200  * i) + 2*random.RandomGauss());
		}
		Allocator.Copy(data, Output);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}
	}
	
	public Object Output(int channel){
		return Output;
	}
	
	public void SetIpAddress(String ipAddress){
	}
	
	public void Dispose(){
		
	}
}
