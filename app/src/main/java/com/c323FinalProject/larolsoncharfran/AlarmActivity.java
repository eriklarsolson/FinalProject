package com.c323FinalProject.larolsoncharfran;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Bundle bundle = getIntent().getBundleExtra("ALARM_BUNDLE");
        int taskIndex = bundle.getInt("TASK_INT");

        Task task = LoginActivity.tasks.get(taskIndex);

        CircleImageView icon = findViewById(R.id.icon);
        TextView title = findViewById(R.id.title);
        TextView addressName = findViewById(R.id.addressName);
        TextView dueDate = findViewById(R.id.dueDate);
        TextView description = findViewById(R.id.description);

        if(task.getImage() != null) {
            icon.setImageBitmap(task.getImage());
        } else {
            icon.setImageResource(R.drawable.ic_task);
        }

        title.setText(task.getTitle());
        addressName.setText(task.getAddressName());
        dueDate.setText(task.dueDateTime.toString());
        description.setText(task.getDescription());
    }
}
