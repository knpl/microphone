package nl.knpl.microphone;


import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.media.MediaPlayer;
import android.media.MediaRecorder;


public class MainActivity extends ActionBarActivity {
	
	private MediaRecorder recorder;
	private MediaPlayer player;
	
	private Button playButton;
	private Button recordButton;
	
	private String path;
	
	private boolean isPlaying;
	private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.3gp";
        
        setContentView(R.layout.activity_main);
        recordButton = (Button) findViewById(R.id.record_button);
        playButton= (Button) findViewById(R.id.play_button);
        
        recordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRecording) {
					stopRecording();
					recordButton.setText("record");
				}
				else {
					record();
					recordButton.setText("stop recording");
				}
				isRecording = !isRecording;
			}
		});
        recordButton.setText("record");
        
        playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPlaying) {
					stopPlaying();
					playButton.setText("play");
				}
				else {
					play();
					playButton.setText("stop playing");
				}
				isPlaying = !isPlaying;
			}
		});
        playButton.setText("play");
        
        isRecording = isPlaying = false;
        recorder = null;
        player = null;
    }
    
    private void record() {
    	recorder = new MediaRecorder();
    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    	recorder.setOutputFile(path);
    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	
    	try {
    		recorder.prepare();
    		recorder.start();
    	}
    	catch (IOException ex) {
    		android.util.Log.d("mytag", "Failed to prepare audio recorder.");
    	}
    }
    
    private void stopRecording() {
    	if (recorder != null) {
	    	recorder.stop();
	    	recorder.release();
	    	recorder = null;
    	}
    }
    
    private void play() {
    	player = new MediaPlayer();
    	try {
    		player.setDataSource(path);
    		player.prepare();
    		player.start();
    	}
    	catch (IOException ex) {
    		android.util.Log.d("mytag", "Failed to play audio.");
    	}
    }
    
    private void stopPlaying() {
    	if (player != null) {
    		player.release();
    		player = null;
    	}
    }

    @Override
	protected void onPause() {
		super.onPause();
		
		if (recorder != null) {
			recorder.release();
			recorder = null;
			
			isRecording = false;
			recordButton.setText("record");
		}
		
		if (player != null) {
			player.release();
			player = null;
			
			isPlaying = false;
			playButton.setText("play");
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
