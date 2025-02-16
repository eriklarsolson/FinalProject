package com.c323FinalProject.larolsoncharfran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Bundle bundle = getIntent().getBundleExtra("ALARM_BUNDLE");
        int taskIndex = bundle.getInt("TASK_INDEX");

        Task task = LoginActivity.tasks.get(taskIndex);

        setTitle("Alarm Going Off!");

        //Reload task list
        HomeFragment.getPendingAndCompleteTasks();

        CircleImageView icon = findViewById(R.id.icon);
        TextView title = findViewById(R.id.title);
        TextView addressName = findViewById(R.id.addressName);
        TextView dueDate = findViewById(R.id.dueDate);
        TextView description = findViewById(R.id.description);
        MaterialButton goBackButton = findViewById(R.id.goBackButton);

        if(task.getImage() != null) {
            icon.setImageBitmap(task.getImage());
        } else {
            icon.setImageResource(R.drawable.ic_task);
        }

        title.setText(task.getTitle());
        addressName.setText(task.getAddressName());
        dueDate.setText(task.dueDateTime.toString());
        description.setText(task.getDescription());

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmActivity.this, NavigationDrawer.class);
                startActivity(intent);
            }
        });
    }
}
