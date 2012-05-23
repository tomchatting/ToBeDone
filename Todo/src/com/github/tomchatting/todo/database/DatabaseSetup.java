package com.github.tomchatting.todo.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseSetup {
	// Create the database
	private static final String DATABASE_CREATE = "create table todo "
			+ "(_id integer primary key autoincrement, "
			+ "summary text not null, description text not null, "
			+ "taskdate integer not null, "
			+ "completed integer not null);";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(DatabaseSetup.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + " which will destroy all data");
		database.execSQL("DROP TABLE IF EXISTS todo");
		onCreate(database);
	}
}
