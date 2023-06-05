package com.example.taskmate.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskModel {

    String title , description , date , time , key , category , Note , Image_URL , Attachment_URL,Attachment_name  ;

    public TaskModel(String title, String description, String date, String time, String key, String category) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.key = key;
        this.category = category;
    }


    public TaskModel(String title, String description, String date, String time, String key, String category, String note, String image_URL, String attachment_URL, String attachment_name) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.key = key;
        this.category = category;
        Note = note;
        Image_URL = image_URL;
        Attachment_URL = attachment_URL;
        Attachment_name = attachment_name;
    }

    public TaskModel(String title, String description, String date, String time, String key, String category, String note) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.key = key;
        this.category = category;
        Note = note;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }

    public String getAttachment_URL() {
        return Attachment_URL;
    }

    public void setAttachment_URL(String attachment_URL) {
        Attachment_URL = attachment_URL;
    }

    public String getAttachment_name() {
        return Attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        Attachment_name = attachment_name;
    }
}
