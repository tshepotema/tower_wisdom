package com.example.tow.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import com.example.tow.database.TowDatabaseHelper;
import com.example.tow.database.QuestionsTable;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class QuestionsContentProvider extends ContentProvider {

	//the database to be used
	private TowDatabaseHelper database;
	
	//used for the URI match
	private static final int QUESTIONS = 10;
	private static final int QUESTION_ID = 20;
	private static final String AUTHORITY = "com.example.tow.contentprovider";
	private static final String BASE_PATH = "questions";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/questions";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/question";
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, QUESTIONS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", QUESTION_ID);
	}

	@Override
	public boolean onCreate() {
		database = new TowDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// Check if the caller has requested a column which does not exists
		checkColumns(projection);
		// Set the table
		queryBuilder.setTables(QuestionsTable.TABLE_QUESTIONS);
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case QUESTIONS:
			break;
		case QUESTION_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(QuestionsTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case QUESTIONS:
			id = sqlDB.insert(QuestionsTable.TABLE_QUESTIONS, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case QUESTIONS:
			rowsDeleted = sqlDB.delete(QuestionsTable.TABLE_QUESTIONS, selection,
					selectionArgs);
			break;
		case QUESTION_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(QuestionsTable.TABLE_QUESTIONS,
						QuestionsTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(QuestionsTable.TABLE_QUESTIONS,
						QuestionsTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case QUESTIONS:
			rowsUpdated = sqlDB.update(QuestionsTable.TABLE_QUESTIONS, values, selection,
					selectionArgs);
			break;
		case QUESTION_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(QuestionsTable.TABLE_QUESTIONS, values,
						QuestionsTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(QuestionsTable.TABLE_QUESTIONS, values,
						QuestionsTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { QuestionsTable.COLUMN_QUESTION, QuestionsTable.COLUMN_GROUP,
				QuestionsTable.COLUMN_A, QuestionsTable.COLUMN_B, QuestionsTable.COLUMN_C,
				QuestionsTable.COLUMN_D, QuestionsTable.COLUMN_E,
				QuestionsTable.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}
}
