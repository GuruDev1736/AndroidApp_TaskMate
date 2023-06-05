package com.example.taskmate.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;

import androidx.core.content.ContextCompat;

import com.example.taskmate.R;
import com.mailgun.client.MailgunClient;

import es.dmoral.toasty.Toasty;

public class Constants {

    public static final int PROGRESS_DIALOG_COLOR = R.color.main;





    public static ProgressDialog progressDialog(Context context , String title , String message) {
        ProgressDialog progressDialog = new ProgressDialog(context ,R.style.CustomProgressDialog);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void SuccessToast(Context context , String message)
    {
        Toasty.success(context,message,Toasty.LENGTH_SHORT,true).show();
    }

    public static void ErrorToast(Context context , String message)
    {
        Toasty.error(context,message,Toasty.LENGTH_SHORT,true).show();
    }

    public static void InfoToast(Context context , String message)
    {
        Toasty.info(context,message,Toasty.LENGTH_SHORT,true).show();
    }

    public static void WarningToast(Context context , String message)
    {
        Toasty.warning(context,message,Toasty.LENGTH_SHORT,true).show();
    }

}
