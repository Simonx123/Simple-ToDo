package com.example.sadedira.simpletodo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sadedira.simpletodo.data.ToDoContract.ToDoEntry;

/**
 * Created by sadedira on 8/3/2017.
 */

public class ToDoDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = ToDoDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "todo.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ToDoDbHelper}.
     *
     * @param context of the app
     */
    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + ToDoEntry.TABLE_NAME + " ("
                + ToDoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDoEntry.COLUMN_TODO_NAME + " TEXT NOT NULL, "
                + ToDoEntry.COLUMN_TODO_DATE+ " TEXT, "
                + ToDoEntry.COLUMN_TODO_NOTES+ " TEXT, "
                + ToDoEntry.COLUMN_TODO_PLEVEL+ " INTEGER NOT NULL, "
                + ToDoEntry.COLUMN_TODO_STATUS+ " INTEGER NOT NULL)";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
