package com.c323FinalProject.larolsoncharfran;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.c323FinalProject.larolsoncharfran.LoginActivity.tasks;

public class CalendarFragment extends Fragment {
    static ArrayList<Task> loadedTasks = new ArrayList<>();
    CalendarView calendarView;
    CalenderTaskAdapter calendarTaskAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Set title
        getActivity().setTitle("Task Calendar");

        loadDaysSelected();

        loadedTasks.clear();
        calendarView = root.findViewById(R.id.calendar);
        RecyclerView historyList = root.findViewById(R.id.historyList);
        calendarTaskAdapter = new CalenderTaskAdapter(getActivity(), loadedTasks);
        historyList.setAdapter(calendarTaskAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity());
        historyList.setLayoutManager(layoutManager);
        historyList.setHasFixedSize(true);

        //TODO -  "similarly if you have events on any other days those days will be marked with circle or dots as well.
        // (you can check it out by adding events in your emulators calendar to get an idea of the UI)"

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar calendar = eventDay.getCalendar();
                getTasksFromDate(calendar);
            }
        });

        return root;
    }

    void loadDaysSelected() {
        ArrayList<Calendar> calendars = new ArrayList<>();

        for(Task task : tasks) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getDueDateTime());
            calendars.add(calendar);
        }

        calendarView.setSelectedDates(calendars);
    }

    void getTasksFromDate(Calendar date) {
        loadedTasks.clear();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for(Task task : tasks) {
            if(task.getDueDateString().equals(simpleDateFormat.format(date.getTime()))) {
                loadedTasks.add(task);
            }
        }

        calendarTaskAdapter.updateList(loadedTasks);
    }
}