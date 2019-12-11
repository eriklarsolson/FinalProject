package com.c323FinalProject.larolsoncharfran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.myDB;
import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.userTableName;

public class LoginActivity extends AppCompatActivity {
    static ArrayList<Task> tasks = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static User currentUser = null;
    EditText nameTextBox;
    EditText emailTextBox;
    CircleImageView profile_image;
    Dialog dialog;
    Bitmap icon = null;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission();

        //Set title
        setTitle("Login");

        //Set up session
        //Session Manager
        session = new SessionManager(getApplicationContext());

        nameTextBox = findViewById(R.id.nameTextBox);
        emailTextBox = findViewById(R.id.emailTextBox);
        profile_image = findViewById(R.id.profile_image);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - Validate input of email format
                if (!nameTextBox.getText().toString().equals("") && !emailTextBox.getText().toString().equals("")) {
                    //Set new currentUser object and add to user list
                    String uniqueID = UUID.randomUUID().toString();
                    currentUser = new User(nameTextBox.getText().toString(), emailTextBox.getText().toString(), uniqueID, icon);
                    users.add(currentUser);

                    //Creating user login session
                    session.createLoginSession(currentUser.getName(), currentUser.getEmail());

                    //Insert into DB
                    ContentValues values = new ContentValues();
                    values.put("User_id", currentUser.getId());
                    values.put("User_name", currentUser.getName());
                    values.put("User_email", currentUser.getEmail());

                    values.put("User_icon", HomeFragment.getBitmapAsByteArray(currentUser.getIcon()));
                    myDB.insert(userTableName, null, values);

                    //Go to main activity
                    Intent intent = new Intent(LoginActivity.this, NavigationDrawer.class);
                    startActivity(intent);
                    finish();
                } else {
                    if(nameTextBox.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter your name in the name box", Toast.LENGTH_SHORT).show();
                    } else if(emailTextBox.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter your email in the email box", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraOptionDialog();
            }
        });
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
        }
    }


    public void showCameraOptionDialog() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.option_dialog, null);
        builder.setView(customLayout);

        Button galleryButton = customLayout.findViewById(R.id.galleryButton);
        Button cameraButton = customLayout.findViewById(R.id.cameraButton);
        Button cancelButton = customLayout.findViewById(R.id.cancelButton);

        //Create and show the alert dialog
        dialog = builder.create();
        dialog.show();

        checkPermission();

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load from gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

                dialog.hide();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load camera intent
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 200);

                dialog.hide();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 100:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri selectedImage = data.getData();
                        try {
                            icon = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            System.out.println(icon);
                            profile_image.setImageBitmap(icon);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    break;
                case 200:
                    if (resultCode == Activity.RESULT_OK) {
                        //data gives you the image uri. Try to convert that to bitmap
                        icon = (Bitmap) data.getExtras().get("data");
                        System.out.println(icon);
                        profile_image.setImageBitmap(icon);
                        break;
                    }
                    break;
            }
        } catch (Exception e) {
            //Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }
}
