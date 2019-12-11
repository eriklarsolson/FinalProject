package com.c323FinalProject.larolsoncharfran;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.c323FinalProject.larolsoncharfran.LoginActivity.currentUser;
import static com.c323FinalProject.larolsoncharfran.LoginActivity.tasks;
import static com.c323FinalProject.larolsoncharfran.LoginActivity.users;

public class NavigationDrawer extends AppCompatActivity {
    DrawerLayout drawer;

    //Session Manager Class
    SessionManager session;

    static SQLiteDatabase myDB;
    static String userTableName = "USERS";
    static String taskTableName = "TASKS";

    static Bitmap newTaskIcon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        //Start Service
        Intent alarm = new Intent(this, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MINUTE, 1);
            calendar.set(Calendar.SECOND, 0);

            long triggerAt = calendar.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, 18000, pendingIntent);
            //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800000, pendingIntent);
        }

        //Check login
        //Session class instance
        session = new SessionManager(getApplicationContext());

        //Set up DB
        setUpDatabase();

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        //Load in task objects
        //loadTmpTasks();
        loadTasks();

        //Load users
        loadUsers();

        //get user data from session
        HashMap<String, String> user = session.getUserDetails();

        //name
        String name = user.get(SessionManager.KEY_NAME);

        //email
        String email = user.get(SessionManager.KEY_EMAIL);

        //Find current user from shared preferences by comparing DB user list
        if(currentUser == null) {
            for (User tmpUser : users) {
                System.out.println(tmpUser);
                System.out.println(tmpUser);
                if (tmpUser.getName().equals(name) && tmpUser.getEmail().equals(email)) {
                    currentUser = tmpUser;
                }
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        configureNavigationDrawer();

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                      /* host Activity */
                drawer,                           /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        );

        drawer.addDrawerListener(mDrawerToggle);

        //Open home fragment on app load
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, new HomeFragment());
        transaction.commit();

        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigationDrawer.this, LoginActivity.class);
                startActivity(intent);

                //Reset current user
                LoginActivity.currentUser = null;

                session.logoutUser();
            }
        });
    }

    private void configureNavigationDrawer() {
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment f = null;
                int itemId = menuItem.getItemId();
                if (itemId == R.id.nav_home) {
                    f = new HomeFragment();
                } else if (itemId == R.id.nav_map) {
                    f = new MapFragment();
                } else if (itemId == R.id.nav_calendar) {
                    f = new CalendarFragment();
                }

                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, f);
                    transaction.commit();
                    drawer.closeDrawers();
                    return true;
                }
                return false;
            }
        });

        View headerLayout = navView.getHeaderView(0); // 0-index header

        CircleImageView image = headerLayout.findViewById(R.id.image);
        TextView name = headerLayout.findViewById(R.id.name);
        TextView email = headerLayout.findViewById(R.id.email);

        if(currentUser != null) {
            name.setText(currentUser.getName());
            email.setText(currentUser.getEmail());

            if(currentUser.getIcon() != null) {
                image.setImageBitmap(currentUser.getIcon());
            } else {
                image.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            // Android home
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            // manage other entries if you have it ...
        }
        return true;
    }

    //Make tables if they don't exist
    void setUpDatabase() {
        myDB = this.openOrCreateDatabase("TASKSDB", MODE_PRIVATE, null);

        /* Create a Table in the Database. */
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + userTableName
                + " (User_id TEXT, User_name TEXT, User_email TEXT, User_icon BLOB);");

        /* Create a Table in the Database. */
        //myDB.execSQL("DROP TABLE IF EXISTS " + taskTableName);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + taskTableName
                + " (Task_id TEXT, Task_title TEXT, Task_description TEXT, Task_duedate TEXT, Task_duetime TEXT, " +
                "Task_image BLOB, Task_latitude TEXT, Task_longitude TEXT, User_id TEXT, Task_isComplete INT, Task_addressName TEXT);");
    }

    //Load in users from DB & check if there is a user in shared preferences session
    void loadUsers() {
        users.clear();
        Cursor c = myDB.rawQuery("SELECT * FROM " + userTableName, null);
        if (c.moveToFirst()){
            do {
                Bitmap icon = null;
                byte[] imgByte = c.getBlob(3);

                if(imgByte != null) {
                    icon = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                }

                users.add(new User(c.getString(0), c.getString(1), c.getString(2), icon));
            } while(c.moveToNext());
        }
        c.close();
    }

    //Load tasks
    void loadTasks() {
        tasks.clear();
        Cursor c = myDB.rawQuery("SELECT * FROM " + taskTableName, null);
        if (c.moveToFirst()){
            do {
                Bitmap image = null;
                byte[] imgByte = c.getBlob(5);

                if(imgByte != null) {
                    image = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                }


                String dateInString = c.getString(3) + " " + c.getString(4);
                Date date = null;
                try {
                    date = new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(dateInString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                LatLng latLng = new LatLng(Double.valueOf(c.getString(6)), Double.valueOf(c.getString(7)));

                //Task_isComplete BOOLEAN, Task_addressName TEXT
                //Task(String id, String title, String description, Date dueDateTime, String addressName, LatLng location, Bitmap image, String userID) {
                Task newTask = new Task(c.getString(0), c.getString(1), c.getString(2), date, c.getString(10), latLng, image, c.getString(8));
                newTask.setDueDateString(new SimpleDateFormat("MM/dd/yyyy").format(newTask.dueDateTime));
                newTask.setDueTimeString(new SimpleDateFormat("hh:mm").format(newTask.dueDateTime));
                newTask.setComplete((c.getInt(c.getColumnIndex("Task_isComplete")) == 1));
                tasks.add(newTask);
            } while(c.moveToNext());
        }
        c.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
    }

    static double haversineDistance(LatLng location1, LatLng location2) {
        // distance between latitudes
        // and longitudes
        double lat1, lat2, lng1, lng2;
        lat1 = location1.latitude;
        lat2 = location2.latitude;
        lng1 = location1.longitude;
        lng2 = location2.longitude;

        double dLat = (lat2 - lat1) *
                Math.PI / 180.0;
        double dLon = (lng2 - lng1) *
                Math.PI / 180.0;

        // convert to radians
        lat1 = (lat1) * Math.PI / 180.0;
        lat2 = (lat2) * Math.PI / 180.0;

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
}
