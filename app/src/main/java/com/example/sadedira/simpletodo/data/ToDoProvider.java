package com.example.sadedira.simpletodo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.sadedira.simpletodo.data.ToDoContract.ToDoEntry;

import static android.R.attr.level;
import static android.R.attr.name;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by sadedira on 8/3/2017.
 */

public class ToDoProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ToDoProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the todo_data table
     */
    private static final int TODO = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int TODO_ID = 101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher

           /*
         * Sets the integer value for multiple rows in the data to 100. Notice that no wildcard is used
         * in the path
         */
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY, ToDoContract.PATH_TODO, TODO);

          /*
         * Sets the code for a single row to 2. In this case, the "#" wildcard is
         * used. "content://com.example.app.provider/table3/3" matches, but
         * "content://com.example.app.provider/table3 doesn't.
         */
        //sUriMatcher.addURI("com.example.app.provider", "table3/#", 2);

        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY, ToDoContract.PATH_TODO + "/#", TODO_ID);


    }

    /**
     * Database helper object
     */
    private ToDoDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new ToDoDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

// Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(ToDoContract.ToDoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TODO_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ToDoContract.ToDoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ToDoContract.ToDoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;


    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return insertTask(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertTask(Uri uri, ContentValues values) {
        // Check that the Title is not null
        String name = values.getAsString(ToDoEntry.COLUMN_TODO_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Task title required");
        }

        // Check that the priority level is valid
        Integer plevel = values.getAsInteger(ToDoEntry.COLUMN_TODO_PLEVEL);
        if (plevel == null || !ToDoEntry.isValidPlevel(plevel)) {
            throw new IllegalArgumentException("Task requires valid priority level");
        }

        // Check that the task status is valid
        Integer status = values.getAsInteger(ToDoEntry.COLUMN_TODO_STATUS);
        if (status == null || !ToDoEntry.isValidStatus(status)) {
            throw new IllegalArgumentException("Task requires valid status");
        }

        // Check that the Title is not null
        String date = values.getAsString(ToDoEntry.COLUMN_TODO_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Task requires a valid due date");
        }


        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(ToDoEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TODO_ID:
                // For the TODO_ code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ToDoEntry.COLUMN_TODO_NAME)) {
            String name = values.getAsString(ToDoEntry.COLUMN_TODO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Task requires a name");
            }
        }

        if (values.containsKey(ToDoEntry.COLUMN_TODO_DATE)) {
            String dueDate = values.getAsString(ToDoEntry.COLUMN_TODO_DATE);
            if (dueDate == null) {
                throw new IllegalArgumentException("Task requires a due date");
            }
        }


        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(ToDoEntry.COLUMN_TODO_PLEVEL)) {
            Integer plevel = values.getAsInteger(ToDoEntry.COLUMN_TODO_PLEVEL);
            if (plevel == null || !ToDoEntry.isValidPlevel(plevel)) {
                throw new IllegalArgumentException("Task requires valid priority level");
            }
        }


        if (values.containsKey(ToDoEntry.COLUMN_TODO_STATUS)) {
            Integer status = values.getAsInteger(ToDoEntry.COLUMN_TODO_STATUS);
            if (status == null || !ToDoEntry.isValidStatus(status)) {
                throw new IllegalArgumentException("PTask requires valid status");
            }
        }


        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(ToDoEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                // Delete a single row given by the ID in the URI
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:

                return ToDoEntry.CONTENT_LIST_TYPE;
            case TODO_ID:
                return ToDoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
