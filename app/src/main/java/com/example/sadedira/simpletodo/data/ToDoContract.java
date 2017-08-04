package com.example.sadedira.simpletodo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.text.style.TtsSpan.GENDER_FEMALE;
import static android.text.style.TtsSpan.GENDER_MALE;


/**
 * Created by sadedira on 8/3/2017.
 */

public class ToDoContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ToDoContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.sadedira.simpletodo";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_TODO = "todo";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ToDoEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODO);

        /** Name of database table for pets */
        public final static String TABLE_NAME = "todo";

        /**
         * Unique ID number for the todo (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Title of the post.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TODO_NAME ="name";

        /**
         * Due date of the task.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TODO_DATE = "date";


        public final static String COLUMN_TODO_NOTES = "notes";

        /**
         * Priority level and status of the pet.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_TODO_PLEVEL = "plevel";

        public final static String COLUMN_TODO_STATUS = "status";

        /**
         * Possible values for the priority of todo_data.
         */
        public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_MEDIUM = 1;
        public static final int PRIORITY_HIGH = 2;

        public static boolean isValidPlevel(int Plevel) {
            if (Plevel == PRIORITY_HIGH || Plevel == PRIORITY_MEDIUM || Plevel == PRIORITY_LOW) {
                return true;
            }
            return false;
        }

        /**
         * Possible values for the status of todo_data.
         */
        public static final int STATUS_TODO = 0;
        public static final int STATUS_DONE = 1;


        public static boolean isValidStatus(int Status) {
            if (Status == STATUS_TODO || Status == STATUS_DONE) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

    }

}
