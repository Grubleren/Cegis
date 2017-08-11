package jh.slm.interfaces;

public interface ISoundCard{
	Object Output(int channel);
	void Compute();
	void Dispose();
	void SetIpAddress(String ipAddress);
	void Free();
}

