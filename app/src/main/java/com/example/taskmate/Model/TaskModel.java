package com.example.taskmate.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskModel {

    String title , description , date , time , key ;

    public TaskModel(String title, String description, String date, String time, String key) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.key = key;
    }

    public TaskModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
