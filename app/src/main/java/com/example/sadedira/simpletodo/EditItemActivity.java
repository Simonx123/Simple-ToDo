package com.example.sadedira.simpletodo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.example.sadedira.simpletodo.data.ToDoContract.ToDoEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class EditItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean mToDoHasChanged = false;

    /** Identifier for the todo_ data loader */
    private static final int EXISTING_TODO_LOADER = 0;

    /** EditText field to enter the task's name */
    private EditText mNameEditText;

    /** EditText field to enter the due date */
    private EditText mDateEditText;

    /** Using it as static field to update the date field
     * in @DatePickerFragment method
     * */
    private static EditText mtDateEditText;

    /** EditText field to enter the note */
    private EditText mNoteEditText;

    /** EditText field to enter for priority */
    private Spinner mPrioritySpinner;

    private Spinner mstatusSpinner;

    /** Content URI for the existing task (null if it's a new task) */
    private Uri mCurrentToDoUri;

  /*i int variable for both mPlevel & mStatus spinner items */
    private int mPlevel = ToDoEntry.PRIORITY_HIGH;
    private int mStatus = ToDoEntry.STATUS_TODO;

    private static SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        dateFormatter = new SimpleDateFormat("MMM d, ''yy", Locale.US);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new task or editing an existing one.
        Intent intent = getIntent();
        mCurrentToDoUri = intent.getData();

        // If the intent DOES NOT contain a task content URI, then we know that we are
        // creating a new task.
        if (mCurrentToDoUri == null) {
            // This is a new task, so change the app bar to say "Add Task"
            setTitle("Add Task");

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // This method will trigger onPrepareOptionsMenu method
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing task, so change app bar to say "Edit Task"
            setTitle("Edit Task");

            // Initialize a loader to read the task data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_TODO_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.editText);
        mDateEditText = (EditText) findViewById(R.id.editText2);
        mNoteEditText = (EditText) findViewById(R.id.editText3);
        mPrioritySpinner = (Spinner) findViewById(R.id.spinner);
        mstatusSpinner = (Spinner) findViewById(R.id.spinner2);


        mtDateEditText = (EditText) findViewById(R.id.editText2);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);
        mPrioritySpinner.setOnTouchListener(mTouchListener);
        mstatusSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
        setupStatusSpinner();

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });

        mDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                }
            }
        });

    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        Calendar newDate = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            newDate.set(year, month, day);
            mtDateEditText.setText(dateFormatter.format(newDate.getTime()));
        }
    }
    // Check if there is a change in any of the field when in Edit mode
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mToDoHasChanged = true;
            return false;
        }
    };


    //Buiding an alert dialogue box
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the task.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // When back button is pressed
    @Override
    public void onBackPressed() {
        // If the task hasn't changed, continue with handling back button press
        if (!mToDoHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }



    /**
     * Setup the dropdown spinner that allows the user to select the gender of the task
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_priority_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mPrioritySpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("HIGH")) {
                        mPlevel = ToDoEntry.PRIORITY_HIGH; // Male
                    } else if (selection.equals("MEDIUM")) {
                        mPlevel = ToDoEntry.PRIORITY_MEDIUM; // Female
                    } else {
                        mPlevel = ToDoEntry.PRIORITY_LOW; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPlevel = ToDoEntry.PRIORITY_HIGH; // Unknown
            }
        });
    }

    private void setupStatusSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mstatusSpinner.setAdapter(statusSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mstatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("TO-DO")) {
                        mStatus = ToDoEntry.STATUS_TODO; // Male
                    } else if (selection.equals("DONE")) {
                        mStatus = ToDoEntry.STATUS_DONE; // Female
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = ToDoEntry.STATUS_TODO; // Unknown
            }
        });
    }

    /**
     * Get user input from editor and save new task into database.
     */
    private void saveTask(){
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String notesString = mNoteEditText.getText().toString().trim();



        // Checking for empty field name
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(dateString)) {

            if (mCurrentToDoUri == null) {

                Toast.makeText(this, "Task cannot be added. Please provide values for all the required fields",
                        Toast.LENGTH_LONG).show();
                return;
                //TaskDbHelper mDbHelper = new TaskDbHelper(this);
                // Gets the data repository in write mode
                // SQLiteDatabase db = mDbHelper.getWritableDatabase();
            }else {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Task cannot be updated. Invalid field",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ToDoEntry.COLUMN_TODO_NAME, nameString);
        values.put(ToDoEntry.COLUMN_TODO_DATE, dateString);
        values.put(ToDoEntry.COLUMN_TODO_NOTES, notesString);
        values.put(ToDoEntry.COLUMN_TODO_PLEVEL, mPlevel);
        values.put(ToDoEntry.COLUMN_TODO_STATUS, mStatus);

/*Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TaskEntry.TABLE_NAME, null, values);
               // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving task", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Task saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
*/
        if (mCurrentToDoUri == null) {

            Uri newuri = getContentResolver().insert(ToDoEntry.CONTENT_URI, values);
            if (newuri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error with saving task",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Task Saved Successfully",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING task, so update the task with content URI: mCurrentTaskUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentTaskUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error updating Task",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Task updated Successfully",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void deleteTask(){
        // Defines a variable to contain the number of rows deleted
        int mRowsDeleted = 0;

        // Deletes the words that match the selection criteria
        mRowsDeleted = getContentResolver().delete(
                mCurrentToDoUri,   // the user dictionary content URI
                null,                   // the column to select on
                null                    // the value to compare to
        );

        // Show a toast message depending on whether or not the update was successful.
        if (mRowsDeleted == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, "No Task Deleted",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, "Task deleted Successfully",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new task, hide the "Delete" menu item.
        if (mCurrentToDoUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                saveTask();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                deleteTask();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                //NavUtils.navigateUpFromSameTask(this);
                // If the task hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mToDoHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditItemActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditItemActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Since the editor shows all task attributes, define a projection that contains
        // all columns from the task table
        String[] projection = {
                ToDoEntry._ID,
                ToDoEntry.COLUMN_TODO_NAME,
                ToDoEntry.COLUMN_TODO_DATE,
                ToDoEntry.COLUMN_TODO_NOTES,
                ToDoEntry.COLUMN_TODO_PLEVEL,
                ToDoEntry.COLUMN_TODO_STATUS};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentToDoUri,         // Query the content URI for the current task
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null); // Default sort order
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of task attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_NAME);
            int dateColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_DATE);
            int noteColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_NOTES);
            int priorityColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_PLEVEL);
            int statusColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_STATUS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String note = cursor.getString(noteColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);
            int status = cursor.getInt(statusColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mDateEditText.setText(date);
            mNoteEditText.setText(note);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (priority) {
                case ToDoEntry.PRIORITY_HIGH:
                  mPrioritySpinner.setSelection(2);
                    break;
                case ToDoEntry.PRIORITY_LOW:
                    mPrioritySpinner.setSelection(0);
                    break;
                case ToDoEntry.PRIORITY_MEDIUM:
                    mPrioritySpinner.setSelection(1);
                    break;
            }

            switch (status) {
                case ToDoEntry.STATUS_DONE:
                    mstatusSpinner.setSelection(1);
                    break;
                case ToDoEntry.STATUS_TODO:
                    mstatusSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mDateEditText.setText("");
        mNoteEditText.setText("");
        mPrioritySpinner.setSelection(0);
        mstatusSpinner.setSelection(0);// Select "Unknown" gender

    }

}
