package com.example.stress_cues;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends Activity{
	private boolean running = false;
	private static Context context;
	private static String thisNumber;
	private static final String DATABASE_NAME = "timer.db";
	private static final String DATABASE_TABLE = "timerPartners";
	private static final int DATABASE_VERSION = 1;
	private static final String KEY_NUMBER_COLUMN = "KEY_NUMBER_COLUMN";
	private static final String KEY_TIME_COLUMN = "KEY_TIME_COLUMN";
	private static dbHelper dbh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.OnOffButton);
		MainActivity.context = getApplicationContext();
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
	    dbh = new dbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

		try {
			thisNumber = SHA1(tMgr.getLine1Number());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		explicitStart();
		//stopServices();
	}
    public static Context getAppContext() {
        return MainActivity.context;
    }
    public static String getNumber(){
    	return thisNumber;
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
	

		

	public static boolean needLikert(String partnerNumber){
		String [] result_columns = new String [] {KEY_NUMBER_COLUMN, KEY_TIME_COLUMN};
		String where = KEY_NUMBER_COLUMN + "='" + partnerNumber+"'";
		Log.d("test", "WHERE arg = " + where);
		String whereArgs[] = null;
		String groupBy = null;
		String having = null;
		String order = null;
		Long currTime = System.currentTimeMillis() / 1000L;
		Log.d("test", "needLikert called, currTime : " + currTime);
		Log.d("test", "is this called?");
		Log.d("test", "partnerNumber : " + partnerNumber);
		SQLiteDatabase db = dbh.getWritableDatabase();
		Cursor cursor = db.query(DATABASE_TABLE, result_columns, where, whereArgs, groupBy, having, order);
		Log.d("test", "cursor.getCount() = " + cursor.getCount());
		if (cursor.getCount() == 0){
			//add partner and current time
			addPartner(partnerNumber, currTime);
			Log.d("test", "partner not in db");
			return true;
		} else {
			//get time entry, return true if old, return false otherwise
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(KEY_TIME_COLUMN);
			Long previousLikertTime = (long) 0000000;
			if (columnIndex > -1){
				previousLikertTime = cursor.getLong(columnIndex);
			}
			Log.d("test", "previousTime : " + previousLikertTime);

			if ((currTime - previousLikertTime) > 60){
				updateTimeValue(partnerNumber, currTime);
				return true;
			} else {
				return false;
			}
		}
	}
      public static void addPartner(String partnerNumber, Long currTime){
    	  ContentValues newValues = new ContentValues();
    	  newValues.put(KEY_NUMBER_COLUMN, partnerNumber);
    	  newValues.put(KEY_TIME_COLUMN, currTime);
    	  
    	  SQLiteDatabase db = dbh.getWritableDatabase();
    	  db.insert(DATABASE_TABLE, null, newValues);
      }
	  public static void updateTimeValue(String partnerNumber, Long newTime) {
		    /**
		     * Listing 8-6: Updating a database row
		     */
		    // Create the updated row Content Values.
		    ContentValues updatedValues = new ContentValues();
		  
		    // Assign values for each row.
		    updatedValues.put(KEY_TIME_COLUMN, newTime);
		    // [ ... Repeat for each column to update ... ]
		  
		    // Specify a where clause the defines which rows should be
		    // updated. Specify where arguments as necessary.
		    String where = KEY_NUMBER_COLUMN + "='" + partnerNumber+"'";
		    String whereArgs[] = null;
		  
		    // Update the row with the specified index with the new values.
		    SQLiteDatabase db = dbh.getWritableDatabase();
		    db.update(DATABASE_TABLE, updatedValues, 
		              where, whereArgs);
		  }
	  public class dbHelper extends SQLiteOpenHelper{

		
		//SQL statement to create new DB
		private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" +
				KEY_NUMBER_COLUMN + " TEXT primary key, " + KEY_TIME_COLUMN + " INTEGER not null " + ");";
		
		public dbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		      // Log the version upgrade.
		      Log.w("TaskDBAdapter", "Upgrading from version " +
		        oldVersion + " to " +
		        newVersion + ", which will destroy all old data");

		      // Upgrade the existing database to conform to the new 
		      // version. Multiple previous versions can be handled by 
		      // comparing oldVersion and newVersion values.

		      // The simplest case is to drop the old table and create a new one.
		      db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
		      // Create a new one.
		      onCreate(db);
		}
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
   private static String convertToHex(byte[] data) {
       StringBuilder buf = new StringBuilder();
       for (byte b : data) {
           int halfbyte = (b >>> 4) & 0x0F;
           int two_halfs = 0;
           do {
               buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
               halfbyte = b & 0x0F;
           } while (two_halfs++ < 1);
       }
       return buf.toString();
   }

   public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
       MessageDigest md = MessageDigest.getInstance("SHA-1");
       md.update(text.getBytes("iso-8859-1"), 0, text.length());
       byte[] sha1hash = md.digest();
       return convertToHex(sha1hash);
   }

}
