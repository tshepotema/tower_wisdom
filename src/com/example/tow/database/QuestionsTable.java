package com.example.tow.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuestionsTable {
	//Database Table
	public static final String DATABASE_TOW = "tow.db";
	public static final String TABLE_QUESTIONS = "questions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_QUESTION = "question";
	public static final String COLUMN_GROUP = "group_id";
	public static final String COLUMN_A = "a";
	public static final String COLUMN_B = "b";
	public static final String COLUMN_C = "c";
	public static final String COLUMN_D = "d";
	public static final String COLUMN_E = "e";
	
	//Logging tag
	private static final String TAG = QuestionsTable.class.getSimpleName();
	
	//Database Creation SQL
	public static final String DATABASE_CREATE = "" +
			"CREATE TABLE " + TABLE_QUESTIONS + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_QUESTION + " TEXT NOT NULL, " + 
			COLUMN_GROUP + " INTEGER NOT NULL, " + 
			COLUMN_A + " TEXT NOT NULL, " + 
			COLUMN_B + " TEXT NOT NULL, " + 
			COLUMN_C + " TEXT NOT NULL, " + 
			COLUMN_D + " TEXT NOT NULL, " + 
			COLUMN_E + " TEXT NOT NULL);";
	
	public static void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "Try to create table with -> " + DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE);
		Log.d(TAG, "Questions table has been created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVer, int newVer) {
		Log.d(TAG, "Upgrading database from ver " + oldVer + " to ver " + newVer + " for table - " + TABLE_QUESTIONS);
		database.execSQL("DROP IF EXISTS TABLE " + TABLE_QUESTIONS);
		onCreate(database);
	}

}
