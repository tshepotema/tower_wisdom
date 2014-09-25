package com.example.tow.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TowDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "tow.db";
	private static final int DATABASE_VERSION = 1;

	public TowDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("DBHelper", "DB in the TowDatabaseHelper constructor");
	}

	//called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		ScoresTable.onCreate(db);
		QuestionsTable.onCreate(db);
	}

	//called during the upgrade of the database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ScoresTable.onUpgrade(db, oldVersion, newVersion);
		QuestionsTable.onUpgrade(db, oldVersion, newVersion);
	}

}