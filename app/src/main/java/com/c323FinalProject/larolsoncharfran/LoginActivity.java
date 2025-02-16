package com.c323FinalProject.larolsoncharfran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.myDB;
import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.userTableName;

public class LoginActivity extends AppCompatActivity {
    static ArrayList<Task> tasks = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
    static ArrayList<BroadcastReceiver> broadcastReceivers = new ArrayList<>();
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

        MaterialButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameTextBox.getText().toString().equals("") && !emailTextBox.getText().toString().equals("")) {
                    //Set new currentUser object and add to user list
                    Cursor c = myDB.rawQuery("SELECT * FROM " + userTableName + " WHERE User_name='" + nameTextBox.getText().toString() + "' AND User_email='" + emailTextBox.getText().toString() + "';", null);
                    if (c.getCount() == 0) {
                        String uniqueID = UUID.randomUUID().toString();
                        currentUser = new User(uniqueID, nameTextBox.getText().toString(), emailTextBox.getText().toString(), icon);
                        users.add(currentUser);

                        //Insert into DB
                        ContentValues values = new ContentValues();
                        values.put("User_id", currentUser.getId());
                        values.put("User_name", currentUser.getName());
                        values.put("User_email", currentUser.getEmail());
                        values.put("User_icon", HomeFragment.getBitmapAsByteArray(currentUser.getIcon()));
                        myDB.insert(userTableName, null, values);
                    } else {
                        if (c.moveToFirst()){
                            Bitmap icon = null;
                            byte[] imgByte = c.getBlob(3);

                            if(imgByte != null) {
                                icon = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                            }
                            currentUser = new User(c.getString(0), c.getString(1), c.getString(2), icon);

                            users.add(currentUser);
                        }
                    }
                    c.close();

                    //Creating user login session
                    session.createLoginSession(currentUser.getName(), currentUser.getEmail());

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Login", "OnResume()");
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 123);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, 123);
        }
    }


    public void showCameraOptionDialog() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.option_dialog, null);
        builder.setView(customLayout);

        MaterialButton galleryButton = customLayout.findViewById(R.id.galleryButton);
        MaterialButton cameraButton = customLayout.findViewById(R.id.cameraButton);
        MaterialButton cancelButton = customLayout.findViewById(R.id.cancelButton);

        //Create and show the alert dialog
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
