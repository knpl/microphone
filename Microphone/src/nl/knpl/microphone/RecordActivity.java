package nl.knpl.microphone;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordActivity extends ActionBarActivity {
	
	private Button recordButton,
				   playButton;
	
	private float[] samples;
	private int samplecnt;
	
	private Default defaultState;
	private Recording recordingState;
	private Playing playingState;
	private State state;
	
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);
		
		defaultState = new Default();
		recordingState = new Recording();
		playingState = new Playing();
		state = defaultState;
		
		recordButton = (Button) findViewById(R.id.record_button);
		playButton = (Button) findViewById(R.id.play_button);
		
		recordButton.setText("record");
		playButton.setText("play");
        
        recordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				state.record();
			}
		});
        
        playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				state.play();
			}
		});
        
        samples = new float[RecordMic.SAMPLE_RATE * 10];
        samplecnt = 0;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	public interface State {
		public void record();
		public void play();
	}
	
	public class Default implements State {
		@Override
		public void record() {
			recordingState.init();
			recordButton.setText("stop recording");
			playButton.setEnabled(false);
			state = recordingState;
		}

		@Override
		public void play() {
			playingState.init();
			playButton.setText("stop playing");
			recordButton.setEnabled(false);
			state = playingState;
		}
	}
	
	public class Recording implements State {
		
		private Thread thread;
		private RecordMic recordMic;
		private Handler handler = new Handler() {
			@Override public void handleMessage(Message msg) {
				android.util.Log.d("mytag", "stop recording message: "+msg.what);
				samplecnt = msg.what;
				if (state == recordingState) {
					state.record();
				}
			}
		};
		
		public void init() {
			recordMic = new RecordMic(handler, samples);
			thread = new Thread(recordMic);
			thread.start();
			
			/* Wait until recording started */
			synchronized (recordMic) {
				try {
					while (!recordMic.recordingStarted()) {
						recordMic.wait();
					}
				}
				catch (InterruptedException ex) {
					throw new RuntimeException("Interrupted.");
				}
			}
		}
		
		@Override
		public void record() {
			synchronized (recordMic) {
				recordMic.stopRecording();
				/* Wait until recording finalized */
				try {
					while (!recordMic.recordingFinalized()) {
						recordMic.wait();
					}
				}
				catch (InterruptedException ex) {
					throw new RuntimeException("Interrupted.");
				}
			}
			
			try {
				thread.join();
				thread = null;
				recordMic = null;
			}
			catch (InterruptedException ex) {
				throw new RuntimeException("Interrupted.");
			}
			
			recordButton.setText("record");
			playButton.setEnabled(true);
			state = defaultState;
		}

		@Override
		public void play() {
		}
	}
	
	public class Playing implements State {
		
		private Thread thread;
		private Player player;
		private Handler handler = new Handler() {
			@Override 
			public void handleMessage(Message msg) {
				android.util.Log.d("mytag", "stop playing message: "+msg.what);
        		if (state == playingState) {
        			state.play();
        		}
			}
		};
		
		public void init() {
			player = new Player(handler, samples, samplecnt);
			thread = new Thread(player);
			thread.start();
			
			/* Wait until player started */
			synchronized (player) {
				try {
					while (!player.playingStarted()) {
						player.wait();
					}
				}
				catch (InterruptedException ex) {
					throw new RuntimeException("Interrupted.");
				}
			}
		}
		
		@Override
		public void record() {
		}

		@Override
		public void play() {
			synchronized (player) {
				player.stopPlaying();
				/* Wait until player finalized */
				try {
					while (!player.playingFinalized()) {
						player.wait();
					}
				}
				catch (InterruptedException ex) {
					throw new RuntimeException("Interrupted.");
				}
			}
			
			try {
				thread.join();
				thread = null;
				player = null;
			}
			catch (InterruptedException ex) {
				throw new RuntimeException("Interrupted.");
			}
			
			playButton.setText("play");
			recordButton.setEnabled(true);
			state = defaultState;
		}
	}
}
