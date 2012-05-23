package com.github.tomchatting.todo.database;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TodoDbAdapter {

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_SUMMARY = "summary";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE = "taskdate";
	public static final String KEY_COMPLETED = "completed";
	private static final String DB_TABLE = "todo";
	private Context context;
	private SQLiteDatabase db;
	private TodoDatabaseHelper dbHelper;

	public TodoDbAdapter(Context context) {
		this.context = context;
	}

	public TodoDbAdapter open() throws SQLException {
		dbHelper = new TodoDatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createTodo(String summary, String description, Long date, Integer completed) {
		ContentValues values = createContentValues(summary,	description, date, completed);

		return db.insert(DB_TABLE, null, values);
	}

	/**
	 * Update the todo
	 */
	public boolean updateTodo(long rowId, String summary, String description, Long date, Integer completed) {
		ContentValues values = createContentValues(summary, description, date, completed);

		return db.update(DB_TABLE, values, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Deletes todo
	 */
	public boolean deleteTodo(long rowId) {
		return db.delete(DB_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Deletes the nth todo in the database (for use by the CAB)
	 */
	public boolean deleteNthTodo(long n) throws IOException {
		try {
			db.execSQL("DELETE FROM " + DB_TABLE + " ORDER BY _id ASC LIMIT 1,"+n+";");
			return true;
		} catch (Exception e){
			throw new IOException(e.toString());
		}
		
	}

	/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllTodos() {
		return db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_SUMMARY, KEY_DESCRIPTION, KEY_DATE, KEY_COMPLETED }, null, null, null, null, KEY_DATE + " DESC");
	}

	/**
	 * Return a Cursor positioned at the defined todo
	 */
	public Cursor fetchTodo(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DB_TABLE, new String[] { KEY_ROWID, KEY_SUMMARY, KEY_DESCRIPTION, KEY_DATE, KEY_COMPLETED }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String summary, String description, Long date, Integer completed) {
		ContentValues values = new ContentValues();
		values.put(KEY_SUMMARY, summary);
		values.put(KEY_DESCRIPTION, description);
		values.put(KEY_DATE, date);
		values.put(KEY_COMPLETED, completed);
		return values;
	}

}
