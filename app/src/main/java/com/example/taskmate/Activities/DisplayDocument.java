package com.example.taskmate.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.taskmate.R;
import com.example.taskmate.databinding.ActivityDisplayDocumentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.util.List;


public class DisplayDocument extends AppCompatActivity {

    ActivityDisplayDocumentBinding binding ;
    FirebaseDatabase database ;
    DatabaseReference reference ;
    FirebaseAuth auth ;

    @SuppressLint("IntentReset")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Tasks");
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");










    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}