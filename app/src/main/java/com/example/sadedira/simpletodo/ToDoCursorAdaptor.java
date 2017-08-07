package com.example.sadedira.simpletodo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.sadedira.simpletodo.data.ToDoContract.ToDoEntry;
import org.w3c.dom.Text;



/**
 * Created by sadedira on 8/3/2017.
 */

public class ToDoCursorAdaptor extends CursorAdapter {

    /**
     * Constructs a new {@link TodoCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ToDoCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds thedata (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current tododata can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView ToDoNameTextView = (TextView) view.findViewById(R.id.title);
        TextView ToDoDateTextview = (TextView) view.findViewById(R.id.date);
        TextView ToDoNoteTextview = (TextView) view.findViewById(R.id.notes);
        TextView ToDoPriorityTextView = (TextView) view.findViewById(R.id.priority);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ToDoEntry.COLUMN_TODO_NAME));
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(ToDoEntry.COLUMN_TODO_NOTES));
        int priority = cursor.getInt(cursor.getColumnIndexOrThrow(ToDoEntry.COLUMN_TODO_PLEVEL));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(ToDoEntry.COLUMN_TODO_DATE));


        switch (priority) {
            case ToDoEntry.PRIORITY_HIGH:

                ToDoPriorityTextView.setText("HIGH");
                ToDoPriorityTextView.setTextColor(Color.parseColor("#E13A20"));
                break;
            case ToDoEntry.PRIORITY_LOW:
                ToDoPriorityTextView.setText("LOW");
                ToDoPriorityTextView.setTextColor(Color.parseColor("#10CAC9"));
                break;
            case ToDoEntry.PRIORITY_MEDIUM:
                ToDoPriorityTextView.setText("MEDIUM");
                ToDoPriorityTextView.setTextColor(Color.parseColor("#F5A623"));
                break;
        }


        // Populate fields with extracted properties
        ToDoNameTextView.setText(name);
        ToDoNoteTextview.setText(notes);
        ToDoDateTextview.setText(date);
        //PetWeightTextview.setText(weight);
    }

}
