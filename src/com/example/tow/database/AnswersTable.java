package com.example.tow.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AnswersTable {
	//Database Table
	public static final String DATABASE_TOW = "tow.db";
	public static final String TABLE_ANSWERS = "answers";
	public static final String COLUMN_ID = "_id";	//question_group_id
	public static final String COLUMN_USER = "user";
	public static final String COLUMN_STATUS = "status";
	
	//Logging tag
	private static final String TAG = AnswersTable.class.getSimpleName();
	
	//Database Creation SQL
	public static final String DATABASE_CREATE = "" +
			"CREATE TABLE " + TABLE_ANSWERS + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_USER + " TEXT NOT NULL, " + 
			COLUMN_STATUS + " TEXT NOT NULL);";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.d(TAG, "answers table has been created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVer, int newVer) {
		Log.d(TAG, "Upgrading database from ver " + oldVer + " to ver " + newVer +  " TABLE " + TABLE_ANSWERS);
		database.execSQL("DROP IF EXISTS TABLE " + TABLE_ANSWERS);
		onCreate(database);
	}

}
