package jh.slm.interfaces;

import java.util.concurrent.ArrayBlockingQueue;

public interface ILANComF {
	void Connect(Object input, int i);
	void Compute();
	Boolean ReadyForConnection();
	void WaitForConnection();
	Boolean Connected();
	void SetSystemGain(double gain);
}