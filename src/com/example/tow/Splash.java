package com.example.tow;

import com.example.tow.database.ScoresTable;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		SQLiteDatabase db;
		db = openOrCreateDatabase(ScoresTable.DATABASE_TOW, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT name FROM sqlite_master WHERE type='table' AND name='scores';"); 
		Cursor c = db.rawQuery(sb.toString(), null);
       		
		Boolean moveToFirst; 
		if (c.moveToFirst()) {
			moveToFirst = true;
		} else {
			moveToFirst = false;			
		}
		Log.d("playLog", "playLog splash move2first = " + moveToFirst);
		
		if (moveToFirst) {		
			//TODO: update the rankings			
			//TODO: get next set of QnAs
			
			//Log.d("playLog", "playLog splash - scores table already Exists so start game");
			//play the game
			Intent playIntent = new Intent("com.example.tow.PLAY");
			this.startActivity(playIntent);					
		} else {
			//setup the database
			Thread appStatus = new Thread() {
				public void run() {
					try {
						//Call the db installer service
						InstallAppService installApp = new InstallAppService(Splash.this);
						installApp.installAppDB();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			appStatus.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}	

}