package com.example.taskmate.Activities;

import android.app.ProgressDialog;
import android.content.Context;

public class Constants {

    public static ProgressDialog progressDialog(Context context , String title , String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }
}
