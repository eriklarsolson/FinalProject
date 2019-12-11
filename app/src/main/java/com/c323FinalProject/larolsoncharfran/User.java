package com.c323FinalProject.larolsoncharfran;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class User {
    String name;
    String email;
    String id = "";
    Bitmap icon;

    User(String id, String name, String email, Bitmap icon) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.icon = icon;
    }

    User(String name, String email) {
        this.name = name;
        this.email = email;
        this.icon = null;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name : " + name + " | Email: " + email + " | ID: " + id + " | Icon: " + icon;
    }
}
