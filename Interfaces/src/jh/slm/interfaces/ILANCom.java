package jh.slm.interfaces;

import java.util.concurrent.ArrayBlockingQueue;

public interface ILANCom {
	void Connect(Object input, int i);
	void Compute();
	Boolean ReadyForConnection();
	void WaitForConnection();
	Boolean Connected();
	ArrayBlockingQueue<short[]> GetQueue();
	void SetSystemGain(double gain);
}