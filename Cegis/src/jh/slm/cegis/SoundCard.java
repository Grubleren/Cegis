package jh.slm.cegis;

import jh.slm.calculations.Allocator;
import jh.slm.interfaces.ISoundCard;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;



public class SoundCard implements ISoundCard{

	AudioRecord audio;
	int fs;
	public Object Output;
	private byte[] buffer16;
	private int[] buffer;

	public SoundCard(int fs, int length) {
		Output = Allocator.Allocate(length);
		buffer16 = new byte[2 * length];
		buffer = new int[length];
		fs = 48000;
		int numberOfChannels = AudioFormat.CHANNEL_IN_MONO; 
		int bitsPerSample = AudioFormat.ENCODING_PCM_16BIT;
		audio = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, fs, numberOfChannels, bitsPerSample, 2 * 2 * length);
		audio.startRecording();
		audio.read(buffer16, 0, buffer16.length);
		audio.read(buffer16, 0, buffer16.length);
		audio.read(buffer16, 0, buffer16.length);
		audio.read(buffer16, 0, buffer16.length);
		audio.read(buffer16, 0, buffer16.length);
		audio.read(buffer16, 0, buffer16.length);
	}

	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Compute() {
		audio.read(buffer16, 0, buffer16.length);

		for (int i = 0; i < buffer.length; i++)
			buffer[i] = (int)(((((int)buffer16[2 * i] &0xff) << 16 | ((int)buffer16[2 * i + 1] &0xff) << 24) >> 16));

		Allocator.Copy(buffer, Output);
	}

	public Object Output(int channel){
		return Output;
	}

	public void SetIpAddress(String ipAddress){
	}
	
	public void Dispose(){
		audio.stop();
		audio.release();
	}
}
