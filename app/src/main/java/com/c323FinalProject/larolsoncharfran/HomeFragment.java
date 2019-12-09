package com.c323FinalProject.larolsoncharfran;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    RecyclerView taskView;
    Button addTaskButton;
    TaskAdapter taskAdapter;

    ArrayList<Task> pendingTasks = new ArrayList<>();
    ArrayList<Task> completedTasks = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        BottomNavigationView bottomNavigation = root.findViewById(R.id.nav_view);
        bottomNavigation.inflateMenu(R.menu.home);

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
                    //taskAdapter.removeItem(position);
                    //TODO - Don't delete but instead move to complete task list
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(taskView);

        //TODO - Set for long press hold of a task

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - Popup add task here

            }
        });

        return root;
    }

    void setUpBottomNav(BottomNavigationView navView) {
        navView.setSelectedItemId(R.id.nav_pending);
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_pending:
                        //TODO - make add task button visible and change list of adapter to pending
                        addTaskButton.setVisibility(View.VISIBLE);
                        taskAdapter = new TaskAdapter(getActivity(), pendingTasks);
                        taskView.setAdapter(taskAdapter);
                        return true;
                    case R.id.nav_completed:
                        //TODO - make add task button invisible and change list of adapter to complete
                        addTaskButton.setVisibility(View.INVISIBLE);
                        taskAdapter = new TaskAdapter(getActivity(), completedTasks);
                        taskView.setAdapter(taskAdapter);
                        return true;
                }
                return false;
            }
        };

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    void getPendingAndCompleteTasks() {
        for(Task task : LoginActivity.tasks) {
            if(task.isComplete()) {
                completedTasks.add(task);
            } else {
                pendingTasks.add(task);
            }
        }
    }
}