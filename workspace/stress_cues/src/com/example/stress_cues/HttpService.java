package com.example.stress_cues;

/**
 * Listing 9-1: A skeleton Service class
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class HttpService extends Service {
	public String message = null;
	public String time = null;
	public String likert = null;
	private String urlString = "http://www.scottallencambo.com/stress_study/stress.php";
	public String thisNumber = null;
	public String sent = null;
	public String partnerNumber = null;
	private String callType = null;
	
  @Override
  public void onCreate() {
	  Log.d("test","httpService created");
	  Context context = MainActivity.getAppContext();
	  thisNumber = MainActivity.getNumber();
	  Log.d("test", "This number = " + thisNumber);
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Replace with service binding implementation.
    return null;
  }
  
  /**
   * Listing 9-3: Overriding Service restart behavior
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
	  Log.d("test", "HttpService onStartCommand called");
    startBackgroundTask(intent, startId);
    return Service.START_STICKY;
  }
  
  private void  startBackgroundTask(Intent intent, int startId) {
    // Start a background thread and begin the processing.
		Log.d("test", "HttpService is called");
		Bundle extras = intent.getExtras();
		message = extras.getString("message");
		time = String.valueOf(extras.getLong("time"));
		likert = extras.getString("likert");
		partnerNumber = extras.getString("partnerNumber");
		callType = extras.getString("type");
		
		Log.d("test","Message : " + message);
		Log.d("test", "Time : " + time);
		Log.d("test","Likert : " + likert);
		Log.d("test", "partnerNumer : " + partnerNumber);
		Log.d("test", "This number : " + thisNumber);
		backgroundExecution();
  }
  
  /**
   * Listing 9-14: Moving processing to a background Thread
   */
  //This method is called on the main GUI thread.
  private void backgroundExecution() {
	  Log.d("test", "bgExectuion called");
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
	  //Log.d("test", "background thread has message " + message);
	  ///Log.d("test", "background thread has time " + time);
	  //Log.d("test", "background thread has likert " + likert);
	  Log.d("test", "backgroundThreadProcessing : Type is " + callType);
	  if (callType.equals("sentWlikert")){
		  Log.d("test", postData_sentWlikert());
	  } else if (callType.equals("sentWOlikert")){
		  Log.d("test", postData_sentWOlikert());
	  } else if (callType.equals("received")){
		  Log.d("test", postData_received());
	  }

  }
  
  public String postData_sentWlikert() {
	  Log.d("test","postData_sentWlikert() was called");
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(urlString);
	    String fromPage = "nothing";
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("thisNumber", thisNumber));
	        nameValuePairs.add(new BasicNameValuePair("time", time));
	        nameValuePairs.add(new BasicNameValuePair("message", message));
	        nameValuePairs.add(new BasicNameValuePair("partnerNumber", partnerNumber));
	        nameValuePairs.add(new BasicNameValuePair("sent", "sent"));
	        nameValuePairs.add(new BasicNameValuePair("likert", likert));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        fromPage = inputStreamToString(response.getEntity().getContent());
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block	
	    	Log.d("test", "ClienProtocol Exception" + e);
	    } catch (IOException e) {
	    	Log.d("test","IOException " + e);
	        // TODO Auto-generated catch block
	    }
	    return fromPage;
	}
  public String postData_sentWOlikert() {
	  Log.d("test","postData_sentWOlikert() was called");
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(urlString);
	    String fromPage = "nothing";
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("thisNumber", thisNumber));
	        nameValuePairs.add(new BasicNameValuePair("time", time));
	        nameValuePairs.add(new BasicNameValuePair("message", message));
	        nameValuePairs.add(new BasicNameValuePair("partnerNumber", partnerNumber));
	        nameValuePairs.add(new BasicNameValuePair("sent", "sent"));
	        if (!(likert.equals("None"))){
		        nameValuePairs.add(new BasicNameValuePair("likert", likert));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        fromPage = inputStreamToString(response.getEntity().getContent());
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block	
	    	Log.d("test", "ClienProtocol Exception" + e);
	    } catch (IOException e) {
	    	Log.d("test","IOException " + e);
	        // TODO Auto-generated catch block
	    }
	    return fromPage;
	} 
  
  public String postData_received() {
	  Log.d("test","postreceived() was called");
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(urlString);
	    String fromPage = "nothing";
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("thisNumber", thisNumber));
	        nameValuePairs.add(new BasicNameValuePair("time", time));
	        nameValuePairs.add(new BasicNameValuePair("message", message));
	        nameValuePairs.add(new BasicNameValuePair("partnerNumber", partnerNumber));
	        nameValuePairs.add(new BasicNameValuePair("received", "received"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        fromPage = inputStreamToString(response.getEntity().getContent());
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block	
	    	Log.d("test", "ClienProtocol Exception" + e);
	    } catch (IOException e) {
	    	Log.d("test","IOException " + e);
	        // TODO Auto-generated catch block
	    }
	    return fromPage;
	} 
	// see http://androidsnippets.com/executing-a-http-post-request-with-httpclient
  
  private String inputStreamToString(InputStream is) throws IOException {
	    String line = "";
	    Log.d("test","inputStreamToString() called");
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    
	    // Return full string
	    return total.toString();
	}
  


}