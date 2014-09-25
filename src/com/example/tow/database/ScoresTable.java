package com.example.tow.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScoresTable {
	//Database Table
	public static final String DATABASE_TOW = "tow.db";
	public static final String TABLE_SCORES = "scores";
	public static final String COLUMN_ID = "_id";	//user's profile ID
	public static final String COLUMN_SCORE = "score";
	public static final String COLUMN_GROUPID = "groupid";
	
	//Logging tag
	private static final String TAG = ScoresTable.class.getSimpleName();
	
	//Database Creation SQL
	public static final String DATABASE_CREATE = "" +
			"CREATE TABLE " + TABLE_SCORES + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY, " +
			COLUMN_SCORE + " INTEGER NOT NULL, " + 
			COLUMN_GROUPID + " INTEGER NOT NULL);";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.d(TAG, TABLE_SCORES + " table has been created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVer, int newVer) {
		Log.d(TAG, "Upgrading database from ver " + oldVer + " to ver " + newVer +  " TABLE " + TABLE_SCORES);
		database.execSQL("DROP IF EXISTS TABLE " + TABLE_SCORES);
		onCreate(database);
	}

}