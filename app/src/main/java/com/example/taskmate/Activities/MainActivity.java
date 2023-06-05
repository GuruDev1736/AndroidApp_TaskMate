package com.example.taskmate.Activities;

import static com.example.taskmate.Activities.Constants.ErrorToast;
import static com.example.taskmate.Activities.Constants.SuccessToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.taskmate.Adapter.ReminderBroadcast;
import com.example.taskmate.Adapter.TaskAdapter;
import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskmate.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mailgun.client.MailgunClient;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    TaskAdapter adapter ;
    FirebaseAuth auth ;

    int requestCode = (int) System.currentTimeMillis();




    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();


        ProgressDialog pd = Constants.progressDialog(MainActivity.this,"Loading Task","Please Wait...");
        pd.show();


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddTaskActivity.class));
            }
        });

        binding.showTaskRec.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(FirebaseDatabase.getInstance().
                getReference("Tasks").child(auth.getCurrentUser().getUid()), TaskModel.class).build();
        adapter = new TaskAdapter(options)
        {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pd.dismiss();

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    TaskModel task = adapter.getItem(i);
                    setReminder(MainActivity.this, task.getTitle(),task.getDescription(), task.getDate(), task.getTime());
                }
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                ErrorToast(getApplicationContext(),"Failed To Load Task : "+error.getMessage());
            }
        };
        adapter.startListening();
        binding.showTaskRec.setAdapter(adapter);


    }

    public void setReminder(Context context , String title, String description , String date, String time) {
        // Parse date and time strings to obtain a calendar object
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date timeObj;
        try {
            Date dateObj = dateFormat.parse(date);
            timeObj = timeFormat.parse(time);
            calendar.setTime(dateObj);
            calendar.set(Calendar.HOUR_OF_DAY, timeObj.getHours());
            calendar.set(Calendar.MINUTE, timeObj.getMinutes());
            calendar.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            // The alarm time is in the past, so add a day to the calendar
            calendar.add(Calendar.DATE, 1);
        }

        if (timeObj.getHours() >= 12) {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }


        // Create a new intent to trigger a broadcast receiver
        Intent intent = new Intent(context, ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);

        // Create a new pending intent to be triggered by the alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get an instance of the AlarmManager class and set the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Enter Category");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                process_search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                process_search(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }




    private void process_search(String query) {

        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(FirebaseDatabase.getInstance().
                getReference("Tasks").child(auth.getCurrentUser().getUid()).orderByChild("category").startAt(query).endAt(query+"\uf8ff"), TaskModel.class).build();
        adapter = new TaskAdapter(options);
        adapter.startListening();
        binding.showTaskRec.setAdapter(adapter);

    }




}