package com.csce4623.ahnelson.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.csce4623.ahnelson.todolist.Database.TaskContract;
import com.csce4623.ahnelson.todolist.Database.TaskDbHelper;

import java.util.ArrayList;
import java.util.Calendar;

//Create HomeActivity and implement the OnClick listener
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listItems;
    private ArrayAdapter<String> mAdapter;

    private TaskDbHelper mHelper;





    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    private ConstraintLayout editItemsConstraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mHelper = new TaskDbHelper(this);

        initializeComponents();

        updateUI();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        editItemsConstraintLayout = findViewById(R.id.mainLayout);


        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                Log.i("ON CLICK","Position:" + position);
                modifyTask(position);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todolist_menu, menu);
        return true;
    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnAddNew).setOnClickListener(this);
        listItems = (ListView) findViewById(R.id.listView);
        //findViewById(R.id.btnDeleteNote).setOnClickListener(this);


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            //If new Note, call createNewNote()
            case R.id.btnAddNew:
                editNode("","","","");
                break;
            //If delete note, call deleteNewestNote()
            //case R.id.btnDeleteNote:
            //    deleteNewestNote();
            //    break;
            //This shouldn't happen
            default:
                break;
        }
    }



    void editNode(String title, String content, String date, String time){



        final EditText calText;
        final EditText timeText;
        Button save;

        LayoutInflater layoutInflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customView = layoutInflater.inflate(R.layout.note_activity,null);



        save = (Button) customView.findViewById(R.id.btnSave);
        final PopupWindow editText;

        //instantiate popup window
        editText = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        editText.setFocusable(true);
        //display the popup window
        editText.showAtLocation(editItemsConstraintLayout, Gravity.CENTER, 0, 0);

        calText = (EditText)customView.findViewById(R.id.etDatePicker);
        calText.setInputType(InputType.TYPE_NULL);
        calText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePicker = new DatePickerDialog(HomeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        timeText=(EditText) customView.findViewById(R.id.etTimePicker);
        timeText.setInputType(InputType.TYPE_NULL);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                timePicker = new TimePickerDialog(HomeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                timeText.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                timePicker.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isError = false;
                EditText titleET = customView.findViewById(R.id.etNoteTitle);
                EditText contentET = customView.findViewById(R.id.etNoteContent);
                EditText dateET = customView.findViewById(R.id.etDatePicker);
                EditText timeEt = customView.findViewById(R.id.etTimePicker);
                String titleText = titleET.getText().toString();
                if (titleText.isEmpty()) {
                    toastDisplay(1);
                    isError = true;
                }
                String contentText = contentET.getText().toString();
                if (contentText.isEmpty()&& !isError){
                    toastDisplay(2);
                    isError = true;
                }
                String dateText = dateET.getText().toString();
                if (dateText.isEmpty()&& !isError){
                    toastDisplay(3);
                    isError = true;
                }
                String timeText = timeEt.getText().toString();
                if (timeText.isEmpty()&& !isError){
                    toastDisplay(4);
                    isError = true;
                }
                if(!isError) {
                    createNewNote(titleText, contentText, dateText, timeText);
                    updateUI();
                    editText.dismiss();
                }
            }
        });
    }

    void toastDisplay(int error){

        switch (error)
        {
            case 1:
            {
                Log.i("toastDisplayer: ","Error 1");
                Toast.makeText(getApplicationContext(),"You must have a title.",Toast.LENGTH_LONG).show();
                break;
            }
            case 2:
            {
                Log.i("toastDisplayer: ","Error 2");
                Toast.makeText(getApplicationContext(),"You must have a description.",Toast.LENGTH_LONG).show();
                break;
            }
            case 3:
            {
                Log.i("toastDisplayer: ","Error 3");
                Toast.makeText(getApplicationContext(),"You must set a due date.",Toast.LENGTH_LONG).show();
                break;
            }
            case 4:
            {
                Log.i("toastDisplayer: ","Error 4");
                Toast.makeText(getApplicationContext(),"You must set a due time.",Toast.LENGTH_LONG).show();
                break;
            }
            default:
            {
                break;
            }
        }
    }


    void createNewNote(String Title, String Content, String Date, String Time)
    {

        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, Title);
        values.put(TaskContract.TaskEntry.COL_TASK_CONTENT, Content);
        values.put(TaskContract.TaskEntry.COL_TASK_DATE, Date);
        values.put(TaskContract.TaskEntry.COL_TASK_TIME, Time);
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    private void updateUI() {

        ArrayList<String> taskList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_CONTENT, TaskContract.TaskEntry.COL_TASK_DATE, TaskContract.TaskEntry.COL_TASK_TIME},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int content = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_CONTENT);
            taskList.add(cursor.getString(idx));
            //dataModels.add(new DataModel(cursor.getString(idx),cursor.getString(content)));

        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            listItems.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
            Log.i("TASKLIST", "cleared and replaced all");
        }




        cursor.close();
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

    private void modifyTask(int position){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_CONTENT, TaskContract.TaskEntry.COL_TASK_DATE, TaskContract.TaskEntry.COL_TASK_TIME},
                null, null, null, null, null);

        cursor.moveToPosition(position);

        Log.i("MODIFY TASK","cursor position: " + cursor.getPosition());

        int titleIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
        int contentIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_CONTENT);
        int dateIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
        int timeIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TIME);

        String title = cursor.getString(titleIndex);
        Log.i("MODIFY TASK", "Title: "+ title);
        String content = cursor.getString(contentIndex);
        Log.i("MODIFY TASK", "Content: "+ content);
        String date = cursor.getString(dateIndex);
        Log.i("MODIFY TASK", "Date: "+ date);
        String time = cursor.getString(timeIndex);
        Log.i("MODIFY TASK", "Time: "+ time);

        final EditText titleText = findViewById(R.id.etNoteTitle);
        final EditText contentText = findViewById(R.id.etNoteContent);
        final EditText calText = findViewById(R.id.etDatePicker);
        final EditText timeText = findViewById(R.id.etTimePicker);
        Button save;

        LayoutInflater layoutInflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customView = layoutInflater.inflate(R.layout.note_activity,null);



        save = (Button) customView.findViewById(R.id.btnSave);
        final PopupWindow editText;

        //instantiate popup window
        editText = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        editText.setFocusable(true);
        //display the popup window
        editText.showAtLocation(editItemsConstraintLayout, Gravity.CENTER, 0, 0);

        titleText.setText(title);
        contentText.setText(content);
        calText.setText(date);
        timeText.setText(time);



        cursor.close();
        db.close();

    }


}
