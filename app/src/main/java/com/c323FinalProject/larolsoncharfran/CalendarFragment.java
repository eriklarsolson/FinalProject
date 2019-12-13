package com.c323FinalProject.larolsoncharfran;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.c323FinalProject.larolsoncharfran.LoginActivity.tasks;

public class CalendarFragment extends Fragment {
    static ArrayList<Task> loadedTasks = new ArrayList<>();
    MaterialCalendarView calendarView;
    CalenderTaskAdapter calendarTaskAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Set title
        getActivity().setTitle("Task Calendar");

        loadedTasks.clear();
        calendarView = root.findViewById(R.id.calendar);
        loadDaysSelected();

        RecyclerView historyList = root.findViewById(R.id.historyList);
        calendarTaskAdapter = new CalenderTaskAdapter(getActivity(), loadedTasks);
        historyList.setAdapter(calendarTaskAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity());
        historyList.setLayoutManager(layoutManager);
        historyList.setHasFixedSize(true);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String dateString = (date.getMonth()+1) + "/" + date.getDay() + "/" + date.getYear();
                getTasksFromDate(dateString);

                loadDaysSelected();
            }
        });

        return root;
    }

    void loadDaysSelected() {

        for(Task task : tasks) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getDueDateTime());

            calendarView.setDateSelected(calendar.getTime(), true);
        }

        //calendarView.setS(calendars);
    }

    void getTasksFromDate(String date) {
        loadedTasks.clear();

        for(Task task : tasks) {
            if(task.getDueDateString().equals(date)) {
                loadedTasks.add(task);
            }
        }

        calendarTaskAdapter.updateList(loadedTasks);
    }
}