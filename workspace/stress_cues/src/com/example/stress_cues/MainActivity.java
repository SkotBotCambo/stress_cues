package com.example.stress_cues;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends Activity{
	private boolean running = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.OnOffButton);
		explicitStart();
		//stopServices();
	}
	
	public void onToggleClicked(View view) {
	    // Is the toggle on?
		Log.d("test", "toggle used");
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        explicitStart();
	    } else {
	    	stopServices();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void explicitStart(){
		Intent intent = new Intent(this, ServiceController.class);
		startService(intent);
		running = true;
	}
	
	private void stopServices(){
		stopService(new Intent(this, ServiceController.class));
		running = false;
	}

}
