package com.example.stress_cues;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class PhoneStateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent intent) {
    	Log.d("test","PhoneStateReceiver called()");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent launch = new Intent(context, MainActivity.class);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launch);
        }
    }
}