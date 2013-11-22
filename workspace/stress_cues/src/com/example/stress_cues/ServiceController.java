package com.example.stress_cues;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

//for starting monitor create a service like below
public class ServiceController extends Service
{
   private Activity mainActivity;
   SMSMonitor sms;

   @Override
   public IBinder onBind(Intent intent) {

      return null;
   }

   public void onCreate() 
   {
      super.onCreate();

      /**** Start Listen to Outgoing SMS ****/
      Log.d("test","###### ServiceController :: CallSMS Monitor method ######");
      sms = new SMSMonitor(this , mainActivity);
      sms.startSMSMonitoring();
   }

   @Override
     public void onDestroy() {
        super.onDestroy();

        /**** Stop Listen to Outgoing SMS ****/
        Log.d("test","###### ServiceController :: StopSMS Monitor method ######");
        sms.stopSMSMonitoring();
    }
   
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
     startBackgroundTask(intent, startId);
     return Service.START_STICKY;
   }
   
   private void  startBackgroundTask(Intent intent, int startId) {
     // Start a background thread and begin the processing.
     backgroundExecution();
   }
   
   /**
    * Listing 9-14: Moving processing to a background Thread
    */
   //This method is called on the main GUI thread.
   private void backgroundExecution() {
    // This moves the time consuming operation to a child thread.
    Thread thread = new Thread(null, doBackgroundThreadProcessing,
                               "Background");
    thread.start();
   }
   
   //Runnable that executes the background processing method.
   private Runnable doBackgroundThreadProcessing = new Runnable() {
    public void run() {
      backgroundThreadProcessing();
    }
   };
   
   //Method which does some processing in the background.
   private void backgroundThreadProcessing() {
    // [ ... Time consuming operations ... ]
	      /**** Start Listen to Outgoing SMS ****/
	      Log.d("test","That background thread");
	      //sms = new SMSMonitor(this , mainActivity);
	      //sms.startSMSMonitoring();
   }
}