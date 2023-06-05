package com.example.taskmate.Activities;

import static com.example.taskmate.Activities.Constants.ErrorToast;
import static com.example.taskmate.Activities.Constants.SuccessToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    com.example.taskmate.databinding.ActivityEditTaskBinding binding ;
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

    FirebaseDatabase database ;
    DatabaseReference reference ;
    FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.taskmate.databinding.ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database  = FirebaseDatabase.getInstance();
        reference = database.getReference("Tasks");
        auth = FirebaseAuth.getInstance();

        ProgressDialog progressDialog = Constants.progressDialog(this,"Loading Data","Please Wait...");
        progressDialog.show();

        ArrayAdapter<CharSequence> sub = ArrayAdapter.createFromResource(binding.getRoot().getContext(), R.array.Category, android.R.layout.simple_spinner_item);
        sub.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item);
        binding.category.setAdapter(sub);



        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select a date");

                builder.setSelection(Calendar.getInstance().getTimeInMillis());

                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                constraintsBuilder.setValidator(DateValidatorPointForward.now());

                builder.setCalendarConstraints(constraintsBuilder.build());

                MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selectedDate) {
                        // Do something with the selected date
                        Date date = new Date(selectedDate);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(date);
                        binding.etDate.setText(dateString);
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            }
        });

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        binding.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.show(getSupportFragmentManager(), "time_picker");
            }
        });

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected hour and minute
                int selectedHour = timePicker.getHour();
                int selectedMinute = timePicker.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                // Format the selected time using the timeFormatter and display it in the EditText
                String formattedTime = timeFormatter.format(calendar.getTime());
                binding.etTime.setText(formattedTime);
            }
        });

        Intent intent = getIntent();

        String key = intent.getStringExtra("key");

        reference.child(auth.getCurrentUser().getUid()).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TaskModel model = snapshot.getValue(TaskModel.class);
                    progressDialog.dismiss();
                if (model!=null)
                {
                    binding.etTitle.setText(model.getTitle());
                    binding.etDescription.setText(model.getDescription());
                    binding.etTime.setText(model.getTime());
                    binding.etDate.setText(model.getDate());

                    if (model.getCategory().equals("Work"))
                    {
                        binding.category.setSelection(1);
                        return;
                    }
                    if (model.getCategory().equals("Personal"))
                    {
                        binding.category.setSelection(2);
                        return;
                    }
                    if (model.getCategory().equals("Shopping"))
                    {
                        binding.category.setSelection(3);
                        return;
                    }
                    if (model.getCategory().equals("Education"))
                    {
                        binding.category.setSelection(4);
                        return;
                    }
                    if (model.getCategory().equals("Finance"))
                    {
                        binding.category.setSelection(5);
                        return;
                    }
                    if (model.getCategory().equals("Health"))
                    {
                        binding.category.setSelection(6);
                        return;
                    }
                    if (model.getCategory().equals("Home"))
                    {
                        binding.category.setSelection(7);
                        return;
                    }
                    if (model.getCategory().equals("Leisure"))
                    {
                        binding.category.setSelection(8);
                        return;
                    }
                    if (model.getCategory().equals("Social"))
                    {
                        binding.category.setSelection(9);
                        return;
                    }
                    if (model.getCategory().equals("Travel"))
                    {
                        binding.category.setSelection(10);
                        return;
                    }
                    if (model.getCategory().equals("Other"))
                    {
                        binding.category.setSelection(11);
                        return;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ErrorToast(getApplicationContext(),"Error : "+error.getMessage());
                progressDialog.dismiss();
            }
        });

            binding.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ProgressDialog pd = Constants.progressDialog(binding.edit.getContext(),"Updating Task","Please Wait...");

                    String title  = binding.etTitle.getText().toString();
                    String description  = binding.etDescription.getText().toString();
                    String date  = binding.etDate.getText().toString();
                    String time  = binding.etTime.getText().toString();
                    String category = binding.category.getSelectedItem().toString();

                    if (TextUtils.isEmpty(title))
                    {
                        binding.etTitle.setError("Title Required");
                        return;
                    }

                    if (TextUtils.isEmpty(description))
                    {
                        binding.etDescription.setError("Description Required");
                        return;
                    }
                    if (TextUtils.isEmpty(date))
                    {
                        binding.etDate.setError("Date Required");
                        return;
                    }
                    if (TextUtils.isEmpty(time))
                    {
                        binding.etTime.setError("Time Required");
                        return;
                    }
                    if (category.equals("Select Your Category"))
                    {
                        ErrorToast(getApplicationContext(),"Please Select the Category");
                        return;
                    }

                    pd.show();

                    TaskModel model = new TaskModel(title,description,date,time,key ,category);
                    reference.child(auth.getCurrentUser().getUid()).child(key).setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    SuccessToast(getApplicationContext(),"Task Has SuccessFully Updated");
                                    pd.dismiss();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ErrorToast(getApplicationContext(),"Failed To Update Task : "+e.getMessage());
                                    pd.dismiss();
                                }
                            });
                }
            });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}