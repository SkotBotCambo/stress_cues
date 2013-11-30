package com.example.stress_cues;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;

public class Likert_task extends Activity {
	public String message = null;
	public String partnerNumber = null;
	public Long currTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_likert_task);
		Log.d("test", "Likert Q is called");
		Bundle extras = getIntent().getExtras();
		message = extras.getString("message");
		partnerNumber = extras.getString("partnerNumber");
		currTime = extras.getLong("time");
		Log.d("test","Message from likert_task is " + message);
		long unixTime = System.currentTimeMillis() / 1000L;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_likert_task, menu);
		return true;
	}
	
	public void onSubmitClick(View v){
		Log.d("test", "Submit was clicked");
		RadioGroup rg = (RadioGroup) findViewById(R.id.likertRadioGroup);
		
		int checkedRadio = rg.getCheckedRadioButtonId();
		String selected = "";
		switch(checkedRadio){
		case R.id.radio_2 : selected = "-2";
			break;
		case R.id.radio_1 : selected = "-1";
			break;
		case R.id.radio0 : selected = "0";
			break;
		case R.id.radio1 : selected = "1";
			break;
		case R.id.radio2 : selected = "2";
			break;
			
		}
		Log.d("test", "radio button was " + selected);
		//Store message, time, and likert value
		Log.d("test", "timeOfMessage : " + currTime);
		//This stuff should go in a new method
		
        Intent intent = new Intent(this,HttpService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);
        intent.putExtra("time", currTime);
        intent.putExtra("likert", selected);
        intent.putExtra("partnerNumber", partnerNumber);
        intent.putExtra("type", "sentWlikert");
        startService(intent);
        
		this.finish();
	}
	
	public void onSkipClick(View v){
		//destroy activity
		Log.d("test", "skip was clicked");
        Intent intent = new Intent(this,HttpService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);
        intent.putExtra("time", currTime);
        intent.putExtra("likert", "");
        intent.putExtra("partnerNumber", partnerNumber);
        intent.putExtra("type", "sentWOlikert");
        startService(intent);
		this.finish();
	}

}
