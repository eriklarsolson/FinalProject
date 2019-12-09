package com.c323FinalProject.larolsoncharfran;

import androidx.annotation.NonNull;

public class User {
    String name;
    String email;
    String id;

    User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
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

    @NonNull
    @Override
    public String toString() {
        return "Name : " + name + " | Email: " + email;
    }
}
