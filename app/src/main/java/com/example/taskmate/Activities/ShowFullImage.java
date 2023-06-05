package com.example.taskmate.Activities;

import static com.example.taskmate.Activities.Constants.ErrorToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.example.taskmate.databinding.ActivityShowFullImageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowFullImage extends AppCompatActivity {

    ActivityShowFullImageBinding binding ;
    FirebaseDatabase database ;
    DatabaseReference reference;
    FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowFullImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Tasks");
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        ProgressDialog pd = Constants.progressDialog(ShowFullImage.this,"Fetching Image","Please Wait...");
        pd.show();
        reference.child(auth.getCurrentUser().getUid()).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TaskModel model = snapshot.getValue(TaskModel.class);
                if (model!=null)
                {
                    if (!isDestroyed())
                    {
                        Glide.with(ShowFullImage.this).load(model.getImage_URL()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
                                .transition(DrawableTransitionOptions.withCrossFade()).into(binding.imageView);
                    }

                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            ErrorToast(ShowFullImage.this,"Error : "+error.getMessage());
            pd.dismiss();
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFullImage.super.onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}