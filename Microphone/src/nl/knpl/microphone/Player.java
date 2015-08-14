package nl.knpl.microphone;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Handler;

public class Player implements Runnable {

	public static final int SAMPLE_RATE = 44100;
	
	private Handler handler;
	private float[] samples;
	private final int samplecnt;
	
	private boolean playingStarted,
					playingStopped,
					playingFinalized;
	
	public Player(Handler handler, float[] samples, int samplecnt) {
		this.handler = handler;
		this.samples = samples;
		this.samplecnt = samplecnt;
		playingStarted = playingStopped = playingFinalized = false;
	}
	
	@Override
	public void run() {
		
		int minBufSize = AudioTrack.getMinBufferSize(
				SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		short[] shortBuffer = new short[minBufSize / 2];
		
		AudioTrack track = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				SAMPLE_RATE, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				2 * minBufSize, 
				AudioTrack.MODE_STREAM);
		
		synchronized (this) {
			playingStarted = true;
			notifyAll();
		}
		
		if (track.getState() != AudioTrack.STATE_INITIALIZED) {
			android.util.Log.d("mytag", "AudioTrack not initialized.");
			stopPlaying();
		}
		else if (track.getPlayState() != AudioRecord.RECORDSTATE_STOPPED) {
			android.util.Log.d("mytag", "AudioTrack stopped.");
			stopPlaying();
		}
		else {
			track.play();
			int start = 0;
			while (!playingStopped()) {
				
				int nframes = shortBuffer.length;
				if (samplecnt - start < nframes) {
					nframes = samplecnt - start;
					stopPlaying();
				}
				
				final float norm = 32768f;
				for (int i = 0; i < nframes; ++i) {
					shortBuffer[i] = (short) (samples[start + i] * norm);
				}
				start += nframes;
				
				
				int written = writeAll(track, shortBuffer, nframes);
				if (written < 0) {
					stopPlaying();
					break;
				}
			}
			track.stop();
			handler.sendEmptyMessage(start);
		}
		
		synchronized (this) {
			playingFinalized = true;
			notifyAll();
		}
	}
	
	private static int writeAll(AudioTrack track, short[] shortBuffer, final int nframes) {
		int written,
			total = 0;
		
		while (total != nframes) {
			written = track.write(shortBuffer, total, nframes - total);
			if (written < 0) {
				return -1;
			}
			total += written;
		}
		
		return total;
	}
	
	public synchronized boolean playingStarted() {
		return playingStarted;
	}
	
	public synchronized boolean playingStopped() {
		return playingStopped;
	}
	
	public synchronized boolean playingFinalized() {
		return playingFinalized;
	}
	
	public synchronized void stopPlaying() {
		playingStopped = true;
	}

}