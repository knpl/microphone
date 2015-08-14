package nl.knpl.microphone;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

public class RecordMic implements Runnable {
	
	public static final int SAMPLE_RATE = 44100;
	public static final int N_SAMPLES_MIN = 4096;
	
	private boolean recordingStarted,
					recordingStopped,
					recordingFinalized;
	
	private float[] samples;
	
	private final Handler handler;
	
	public RecordMic(Handler handler, float[] samples) {
		this.handler = handler;
		this.samples = samples;
		recordingStarted = recordingStopped = recordingFinalized = false;
	}
		
	@Override
	public void run() {
		int minBufSize = AudioRecord.getMinBufferSize(
				SAMPLE_RATE, 
				AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		short[] shortBuffer = new short[minBufSize / 2];
		
		AudioRecord recorder = new AudioRecord(
				MediaRecorder.AudioSource.MIC, 
				SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, 2 * minBufSize);

		synchronized (this) {
			recordingStarted = true;
			notifyAll();
		}
		
		if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
			android.util.Log.d("mytag", "AudioRecord not initialized.");
			stopRecording();
		} 
		else if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
			android.util.Log.d("mytag", "AudioRecord recording.");
			stopRecording();
		}
		else {
			recorder.startRecording();
			int start = 0;
			while (!recordingStopped()) {
				int nframes = recorder.read(shortBuffer, 0, shortBuffer.length);
				if (nframes < 0) { // error
					stopRecording();
					break;
				}
				
				int framesleft = samples.length - start;
				if (framesleft < nframes) {
					stopRecording();
					nframes = framesleft;
				}
				
				final float norm = 1/32768f; // 2^-15
				for (int i = 0; i < nframes; i++) {
					samples[start + i] = (float) (shortBuffer[i] * norm);
				}
				start += nframes;
			}		
			recorder.release();
			handler.sendEmptyMessage(start);
		}
		
		synchronized (this) {
			recordingFinalized = true;
			notifyAll();
		}
	}
	
	public synchronized boolean recordingStarted() {
		return recordingStarted;
	}
	
	public synchronized boolean recordingFinalized() {
		return recordingFinalized;
	}
	
	public synchronized boolean recordingStopped() {
		return recordingStopped;
	}

	public synchronized void stopRecording() {
		recordingStopped = true;
	}
}