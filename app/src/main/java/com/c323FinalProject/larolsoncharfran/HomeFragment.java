package com.c323FinalProject.larolsoncharfran;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.c323FinalProject.larolsoncharfran.LoginActivity.tasks;
import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.myDB;
import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.newTaskIcon;
import static com.c323FinalProject.larolsoncharfran.NavigationDrawer.taskTableName;

public class HomeFragment extends Fragment {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    RecyclerView taskView;
    Button addTaskButton;
    static TaskAdapter taskAdapter;

    static ArrayList<Task> pendingTasks = new ArrayList<>();
    static ArrayList<Task> completedTasks = new ArrayList<>();

    boolean completePageActive = false;
    AlertDialog dialog;
    static Place selectedAddress = null;

    ItemTouchHelper itemTouchHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        BottomNavigationView bottomNavigation = root.findViewById(R.id.nav_view);
        bottomNavigation.inflateMenu(R.menu.home);

        //Set title
        getActivity().setTitle("Home");

        //Set up bottom nav
        setUpBottomNav(bottomNavigation);

        //Load in pending and completed tasks
        getPendingAndCompleteTasks();

        taskView = root.findViewById(R.id.taskView);
        addTaskButton = root.findViewById(R.id.addTaskButton);

        taskAdapter = new TaskAdapter(getActivity(), pendingTasks);
        taskView.setAdapter(taskAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity());
        taskView.setLayoutManager(layoutManager);
        taskView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Toast.makeText(getApplicationContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Toast.makeText(getApplicationContext(), "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    final int position = viewHolder.getAdapterPosition();

                    Task pendingTaskSwiped = pendingTasks.get(position);
                    Task selectedTask = null;
                    for(Task task : tasks) {
                        if(pendingTaskSwiped.equals(task)) {
                            task.setComplete(true);
                            selectedTask = task;
                            System.out.println("Right swipe worked");
                        }
                    }

                    //Update pending and complete lists
                    getPendingAndCompleteTasks();

                    //Notify adapter of data change
                    taskAdapter.notifyDataSetChanged();

                    //Update db with new completion status of this task
                    ContentValues values = new ContentValues();
                    if(selectedTask.isComplete()) {
                        values.put("Task_isComplete", 1);
                    } else {
                        values.put("Task_isComplete", 0);
                    }

                    myDB.update(taskTableName, values, "Task_id = ?", new String[]{pendingTaskSwiped.getId()});
                }
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(taskView);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraOptionDialog();
            }
        });

        return root;
    }

    public void showCameraOptionDialog() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.option_dialog, null);
        builder.setView(customLayout);

        Button galleryButton = customLayout.findViewById(R.id.galleryButton);
        Button cameraButton = customLayout.findViewById(R.id.cameraButton);
        Button cancelButton = customLayout.findViewById(R.id.cancelButton);

        //Create and show the alert dialog
        dialog = builder.create();
        dialog.show();

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load from gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

                dialog.hide();
                showNewTaskDialog();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load camera intent
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 200);

                dialog.hide();
                showNewTaskDialog();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    public void showNewTaskDialog() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.add_task_dialog, null);

        //Set up address bar
        setUpAddressBar();

        builder.setView(customLayout);

        Button saveButton = customLayout.findViewById(R.id.saveButton);
        Button cancelButton = customLayout.findViewById(R.id.cancelButton);

        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText title = customLayout.findViewById(R.id.title);
                EditText description = customLayout.findViewById(R.id.description);
                EditText dueDate = customLayout.findViewById(R.id.dueDate);
                EditText dueTime = customLayout.findViewById(R.id.dueTime);

                //TODO - Check input validation
                String titleText = title.getText().toString();
                String descriptionText = description.getText().toString();

                //Get date object from entered input
                String dueDateText = dueDate.getText().toString();
                String dueTimeText = dueTime.getText().toString();

                if (dueDateText.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
                    if (dueTimeText.matches("([0-9]{2}):([0-9]{2})")) {
                        String dateInString = dueDateText + " " + dueTimeText;
                        Date date = null;

                        try {
                            date = new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(dateInString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //Generate unique ID
                        String uniqueID = UUID.randomUUID().toString();

                        //Create new task and add it to global list
                        Task newTask = new Task(uniqueID, titleText, descriptionText, date, selectedAddress.getName(),
                                selectedAddress.getLatLng(), newTaskIcon, LoginActivity.currentUser.getId());
                        newTask.setDueDateString(dueDateText);
                        newTask.setDueTimeString(dueTimeText);
                        tasks.add(newTask);

                        //Reload task list
                        getPendingAndCompleteTasks();

                        if (!completePageActive) {
                            taskAdapter = new TaskAdapter(getActivity(), pendingTasks);
                            taskView.setAdapter(taskAdapter);
                        } else {
                            taskAdapter = new TaskAdapter(getActivity(), completedTasks);
                            taskView.setAdapter(taskAdapter);
                        }

                        //Build values for insert to DB
                        //+ " (Task_id TEXT, Task_title TEXT, Task_description TEXT, Task_duedate TEXT, Task_duetime TEXT, " +
                        //                "Task_image BLOB, Task_latitude TEXT, Task_longitude TEXT, User_id TEXT, Task_isComplete INT, Task_addressName TEXT);");
                        ContentValues values = new ContentValues();
                        values.put("Task_id", newTask.getId());
                        values.put("Task_title", newTask.getTitle());
                        values.put("Task_description", newTask.getDescription());
                        values.put("Task_duedate", newTask.getDueDateString());
                        values.put("Task_duetime", newTask.getDueTimeString());
                        values.put("Task_image", getBitmapAsByteArray(newTask.getImage()));
                        values.put("Task_latitude", newTask.getLocation().latitude);
                        values.put("Task_longitude", newTask.getLocation().longitude);
                        values.put("User_id", newTask.getUserID());
                        if(newTask.isComplete()) {
                            values.put("Task_isComplete", 1);
                        } else {
                            values.put("Task_isComplete", 0);
                        }
                        values.put("Task_addressName", newTask.getAddressName());

                        //Insert into DB
                        myDB.insert(taskTableName, null, values);


                        //Add new alarm to go off one minute before due date
                        Bundle bundle = new Bundle();


                        //TODO - This doesn't work at all
                        bundle.putLong("TIME", date.getTime());
                        bundle.putInt("INDEX", LoginActivity.broadcastReceivers.size());
                        //bundle.putString("PHONE_NUM", phoneNumberTxt);
                        //bundle.putString("MESSAGE", messageTxt);
                        //bundle.putString("SOUND_URL", ringtones.get(selectedRingtone).getUrl());
                        bundle.putInt("TASK_INDEX", tasks.indexOf(newTask));

                        //Create new alarm receiver instance using alarm manager
                        LoginActivity.broadcastReceivers.add(new AlarmReceiver(getContext(), bundle, 30));

                        dialog.hide();
                    } else {
                        Toast.makeText(getContext(), "Please enter only numbers & ':' in the due date time box", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter only numbers & '/' in the due date date box", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    void setUpAddressBar() {
        // Initialize Places.
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyDdB1lTLSpRzNe3zyQg45m-saH4MTK4Yno");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.addressBar);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // Add a marker in Sydney and move the camera
                selectedAddress = place;
            }

            @Override
            public void onError(Status status) {
                //Handle the error.
            }
        });
    }


    void setUpBottomNav(BottomNavigationView navView) {
        navView.setSelectedItemId(R.id.nav_pending);
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_pending:
                        addTaskButton.setVisibility(View.VISIBLE);
                        taskAdapter = new TaskAdapter(getActivity(), pendingTasks);
                        taskView.setAdapter(taskAdapter);
                        itemTouchHelper.attachToRecyclerView(taskView);

                        completePageActive = false;
                        return true;
                    case R.id.nav_completed:
                        addTaskButton.setVisibility(View.INVISIBLE);
                        taskAdapter = new TaskAdapter(getActivity(), completedTasks);
                        taskView.setAdapter(taskAdapter);
                        itemTouchHelper.attachToRecyclerView(null);

                        completePageActive = true;
                        return true;
                }
                return false;
            }
        };

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    static void getPendingAndCompleteTasks() {
        completedTasks.clear();
        pendingTasks.clear();

        for(Task task : tasks) {
            if(task.isComplete()) {
                completedTasks.add(task);
            } else {
                pendingTasks.add(task);
            }
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        //Check if the user took an image for this note or not
        if(bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            return outputStream.toByteArray();
        } else {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 100:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri selectedImage = data.getData();
                        try {
                            newTaskIcon = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    break;
                case 200:
                    if (resultCode == Activity.RESULT_OK) {
                        //data gives you the image uri. Try to convert that to bitmap
                        newTaskIcon = (Bitmap) data.getExtras().get("data");
                        break;
                    }
                    break;
            }
        } catch (Exception e) {
            //Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }
}