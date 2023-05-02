package com.example.taskmate.Authentication;

import static com.example.taskmate.Activities.Constants.ErrorToast;
import static com.example.taskmate.Activities.Constants.SuccessToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.taskmate.Activities.Constants;
import com.example.taskmate.Model.SignupModel;
import com.example.taskmate.R;
import com.example.taskmate.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding ;
    FirebaseAuth auth ;
    FirebaseDatabase database ;
    DatabaseReference reference ;

    ProgressDialog pd ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("USERS");

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String first_name = binding.etFirstname.getText().toString();
                String last_name = binding.etLastname.getText().toString();
                String phone = binding.etPhone.getText().toString();
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();

                 pd = Constants.progressDialog(SignUpActivity.this,"Creating Account","Please Wait...");

                if (TextUtils.isEmpty(first_name))
                {
                    binding.etFirstname.setError("First Name Required");
                    return;
                }
                if (TextUtils.isEmpty(last_name))
                {
                    binding.etLastname.setError("Last Name Required");
                    return;
                }
                if (TextUtils.isEmpty(phone))
                {
                    binding.etPhone.setError("Phone No is Required");
                    return;
                }
                if (TextUtils.isEmpty(email))
                {
                    binding.etEmail.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    binding.etPassword.setError("Password Required");
                    return;
                }

                pd.show();

                auth.createUserWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                SignupModel model = new SignupModel(first_name,last_name,phone,email,password);
                                reference.child(reference.push().getKey()).setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SuccessToast(getApplicationContext(),"User Has Been Successfully Created");
                                                pd.dismiss();
                                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ErrorToast(getApplicationContext(),"Failed To Create User : "+e.getMessage());
                                                pd.dismiss();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ErrorToast(getApplicationContext(),"Failed To Create User : "+e.getMessage());
                                pd.dismiss();
                            }
                        });
            }
        });


        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}