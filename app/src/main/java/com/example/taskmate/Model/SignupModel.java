package com.example.taskmate.Model;

public class SignupModel {

    String First_Name , Last_Name , Phone , Email , Password ;

    public SignupModel(String first_Name, String last_Name, String phone, String email, String password) {
        First_Name = first_Name;
        Last_Name = last_Name;
        Phone = phone;
        Email = email;
        Password = password;
    }

    public SignupModel() {
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
