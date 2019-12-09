package com.c323FinalProject.larolsoncharfran;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

public class Task {
    String id;
    String title;
    String description;
    Date dueDateTime;
    String addressName;
    LatLng location;
    Bitmap image;
    boolean isComplete = false;

    Task(String id, String title, String description, Date dueDateTime, String addressName, LatLng location, Bitmap image) {
        this.title = title;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.addressName = addressName;
        this.location = location;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDateTime() {
        return dueDateTime;
    }

    public String getAddressName() {
        return addressName;
    }

    public LatLng getLocation() {
        return location;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isComplete() {
        return isComplete;
    }

    @NonNull
    @Override
    public String toString() {
        return "Title: " + title + " | Description: " + description + " | Due Date Time: " + dueDateTime + " | Address Name: " + addressName + " Location: " + location;
    }
}
