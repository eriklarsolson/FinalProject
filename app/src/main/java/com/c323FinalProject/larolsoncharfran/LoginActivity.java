package com.c323FinalProject.larolsoncharfran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {
    static ArrayList<Task> tasks = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static User currentUser = null;
    EditText nameTextBox;
    EditText emailTextBox;

    static SQLiteDatabase myDB;
    static String userTableName = "USERS";
    static String taskTableName = "TASKS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpDatabase();

        //Load in task objects
        //TODO - Just loading temporary tasks for now
        loadTmpTasks();

        //Check for currentUser and if found, skip login page and go to NavigationDrawer activity
        loadUsers();

        nameTextBox = findViewById(R.id.nameTextBox);
        emailTextBox = findViewById(R.id.emailTextBox);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - Validate inputs of boxes

                String uniqueID = UUID.randomUUID().toString();
                currentUser = new User(nameTextBox.getText().toString(), emailTextBox.getText().toString(), uniqueID);

                Intent intent = new Intent(LoginActivity.this, NavigationDrawer.class);
                view.getContext().startActivity(intent);

                //TODO - Update shared preferences session here to show that a user has been logged in
            }
        });
    }

    //Make tables if they don't exist
    void setUpDatabase() {
        myDB = this.openOrCreateDatabase("TASKSDB", MODE_PRIVATE, null);

        /* Create a Table in the Database. */
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + userTableName
                + " (User_id TEXT, User_name TEXT, User_email TEXT);");

        /* Create a Table in the Database. */
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + taskTableName
                + " (Task_id TEXT, Task_title TEXT, Task_description TEXT, Task_duedate TEXT, Task_duetime TEXT, " +
                "Task_image BLOB, Task_latitude TEXT, Task_longitude TEXT, User_id TEXT);");
    }

    //Loading in fake tasks to test
    void loadTmpTasks() {

        String startDateString = "12/19/2019";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;

        try {
            date = df.parse(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 10; i++) {
            tasks.add(new Task(i + "", "Task " + i, "Description " + i, date,
                    "Address " + i + ", Bloomington, IN", null, null));
        }

        for(int i = 10; i < 15; i++) {
            Task task = new Task(i + "","Task " + i, "Description " + i, date,
                    "Address " + i + ", Bloomington, IN", null, null);
            task.setComplete(true);
            tasks.add(task);
        }
    }

    //Load in users from DB & check if there is a user in shared preferences session
    void loadUsers() {
        users.clear();
        Cursor c = myDB.rawQuery("SELECT * FROM " + userTableName, null);
        if (c.moveToFirst()){
            do {
                users.add(new User(c.getString(0), c.getString(1), c.getString(2)));
            } while(c.moveToNext());
        }
        c.close();

        //TODO - This will check if there is a live session for the app in the shared preferences
        // 1. If so, load currentUser in the static object above
        // 2. And, skip this page and go straight to NavigationDrawer activity

    }

    /**
     + " (Task_id TEXT, Task_title TEXT, Task_description TEXT, Task_duedate TEXT, Task_duetime TEXT, " +
     "Task_image BLOB, Task_latitude TEXT, Task_longitude TEXT, User_id TEXT);");
     */
    //TODO
    void loadTasks() {
        tasks.clear();
        Cursor c = myDB.rawQuery("SELECT * FROM " + taskTableName, null);
        if (c.moveToFirst()){
            do {
                Bitmap image = null;
                byte[] imgByte = c.getBlob(4);

                if(imgByte != null) {
                    image = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                }


                //tasks.add(new Task(c.getString(0), c.getString(1), c.getString(2), c.getString(3), image));
            } while(c.moveToNext());
        }
        c.close();
    }
}
