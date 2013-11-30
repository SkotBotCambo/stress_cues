package com.example.stress_cues;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;

public class SMSMonitor {
   private ServiceController mainActivity;
   private ContentResolver contentResolver = null;
   private Handler smshandler = null;
   private ContentObserver smsObserver = null;
   public String smsNumber = null;
   public static boolean thCountStatus = false;
   public static int thIncreCount = 0;
   public boolean monitorStatus = false;
   private Context mainContext;
   private NotificationManager notificationManager;
   private int NOTIFICATION_REF = 1;
   private Long currTime;
	private static final String DATABASE_NAME = "timer.db";
	private static final String DATABASE_TABLE = "timerPartners";
	private static final int DATABASE_VERSION = 1;
	private static final String KEY_NUMBER_COLUMN = "KEY_NUMBER_COLUMN";
	private static final String KEY_TIME_COLUMN = "KEY_TIME_COLUMN";
   String code;
//   Feedmanager fm = null;
   static public String activationCode;
   int smsCount = 0;

   public SMSMonitor(final ServiceController mainActivity, final Context mainContext) {
      this.mainActivity = mainActivity;
      this.mainContext = mainContext;
      contentResolver = mainActivity.getContentResolver();
      smshandler = new SMSHandler();
      smsObserver = new SMSObserver(smshandler);
      smsNumber = "5086151289";
      String svc = Context.NOTIFICATION_SERVICE;
      notificationManager = (NotificationManager)mainActivity.getSystemService(svc);
   }

   @SuppressWarnings("deprecation")
   public void startSMSMonitoring() {
	  Log.d("test", "Starting SMSMonitoring()");
      try {
         monitorStatus = false;
         //set notification icon
         Log.d("test", "setting notification");
         showNotification();
        
         if (!monitorStatus) {
            contentResolver.registerContentObserver(Uri
                  .parse("content://sms"), true, smsObserver);            
         }
      } catch (Exception e) {
         Log.d("test","SMSMonitor :: startSMSMonitoring Exception == "+ e.getMessage());
      }
   }

   public void stopSMSMonitoring() {
	   Log.d("test", "SMSMonitoring() was stopped");
       notificationManager.cancel(NOTIFICATION_REF);
       
      try {
         monitorStatus = false;
         if (!monitorStatus) {
            contentResolver.unregisterContentObserver(smsObserver);
            //cancel notification
         }
      } catch (Exception e) {
         Log.e("test","SMSMonitor :: stopSMSMonitoring Exception == "+ e.getMessage());
      }
   }
   
   private void showNotification(){
	    Notification not = new Notification(R.drawable.ic_launcher, "Stress Cues is running", System.currentTimeMillis());
	    PendingIntent contentIntent = PendingIntent.getActivity(mainActivity, 0, new Intent(mainActivity, MainActivity.class), Notification.FLAG_ONGOING_EVENT);        
	    not.flags = Notification.FLAG_ONGOING_EVENT;
	    not.setLatestEventInfo(mainActivity, "Stress Cues", "Press here to turn off...", contentIntent);
	    notificationManager.notify(1, not);
	}

   class SMSHandler extends Handler {
      public void handleMessage(final Message msg) {
    	 // Log.d("test","")
      }
   }

   class SMSObserver extends ContentObserver {
      private Handler sms_handle = null;
      public SMSObserver(final Handler smshandle) {
         super(smshandle);
         sms_handle = smshandle;
      }

      public void onChange(final boolean bSelfChange) {
         super.onChange(bSelfChange);
         //Log.d("test", "message activity");
         Thread thread = new Thread() {
         public void run() {
                //Log.d("test", "running new thread");
               try {
                  monitorStatus = true;
                  // Send message to Activity
                  //Message msg = new Message();
                  //sms_handle.handleMessage(msg);
                  //sms_handle.sendMessage(msg);
                  Uri uriSMSURI = Uri.parse("content://sms");
                  Cursor cur = mainActivity.getContentResolver().query(
                        uriSMSURI, null, null, null, "_id");
                  if (cur.getCount() != smsCount) {
                	  //Log.d("test", "if statement reached");
                     smsCount = cur.getCount();

                     if (cur != null && cur.getCount() > 0) {
                        cur.moveToLast();
                        for (int i = 0; i < cur.getColumnCount(); i++) 
                        {
                           //Log.d("test","SMSMonitor :: incoming Column Name : " +
                            //  cur.getColumnName(i));
                           //Log.d("test", "SMSMonitor :: string : " + cur.getString(i));
                              //cur.getString(i);
                        }

                        smsNumber = cur.getString(cur.getColumnIndex("address"));
                        if (smsNumber == null || smsNumber.length() <= 0)
                        {
                           smsNumber = "Unknown";

                        }

                        int type = Integer.parseInt(cur.getString(cur.getColumnIndex("type")));
                        String message = cur.getString(cur.getColumnIndex("body"));
                        //Log.d("test","SMSMonitor :: SMS type == " + type);
                        Log.d("test","SMSMonitor :: Message Txt == " + message);
                        //Log.d("test","SMSMonitor :: Phone Number == " + smsNumber);

                        cur.close();
                    	Log.d("test","Type was " + type);

                        if (type == 1) {
                           onSMSReceive(message, smsNumber);
                        } else if (type != 2){
                           onSMSSend(message, smsNumber);
                        }
                     }
                  }

               } catch (Exception e) {
                  Log.d("test","SMSMonitor :: onChange Exception == "+ e.getMessage());
                  e.printStackTrace();
               }
            }
         };
         thread.start();
      }

      private void onSMSReceive(final String message, final String number) throws NoSuchAlgorithmException, UnsupportedEncodingException {
         synchronized (this) {
            Log.d("test", "Message Received");
            //Log.d("test", "Number="+number);
            Context context = MainActivity.getAppContext();
            String partnerNumber = MainActivity.SHA1(number);
            Intent intent = new Intent(context,HttpService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", message);
            currTime = System.currentTimeMillis() / 1000L;
            intent.putExtra("time", currTime);
            intent.putExtra("sent", MainActivity.getNumber());
            intent.putExtra("type", "received");
            intent.putExtra("partnerNumber", partnerNumber);
            context.startService(intent);
         }
      }

      private void onSMSSend(final String message, final String number) throws NoSuchAlgorithmException, UnsupportedEncodingException {
         synchronized (this) {
             String hashedNumber = MainActivity.SHA1(number);
             currTime = System.currentTimeMillis() / 1000L;
            //check if likert has been used recently
             boolean likertNeeded = MainActivity.needLikert(hashedNumber);
            if (likertNeeded){
                Log.d("test", "Sent Message : "+message);
                Log.d("test", "Message sent to : "+number);
                Log.d("test", "Hashed as : " + hashedNumber);
                
                //Launch Likert activity
                Intent intent = new Intent(mainActivity,Likert_task.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Boolean sent = true;
                intent.putExtra("message", message);
                intent.putExtra("partnerNumber", hashedNumber);
                intent.putExtra("sent", sent);
                intent.putExtra("time", currTime);
            	mainActivity.startActivity(intent);
            } else {
                Log.d("test", "Message Sent, Likert too soon");
                Log.d("test", "Number="+number);
                Context context = MainActivity.getAppContext();
                Intent intent = new Intent(context,HttpService.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("message", message);
                currTime = System.currentTimeMillis() / 1000L;
                intent.putExtra("time", currTime);
                intent.putExtra("partnerNumber", hashedNumber);
                intent.putExtra("sent", MainActivity.getNumber());
                intent.putExtra("type", "sentWOlikert");
                intent.putExtra("likert", "None");
                context.startService(intent);
            }
            }
        }

      }
	
   

}

