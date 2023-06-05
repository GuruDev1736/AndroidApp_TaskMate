package com.example.taskmate.Adapter;

import static com.example.taskmate.Activities.Constants.SuccessToast;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.Activities.Constants;
import com.example.taskmate.Activities.EditTaskActivity;
import com.example.taskmate.Activities.MainActivity;
import com.example.taskmate.Activities.Notes_Attachment;
import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class TaskAdapter extends FirebaseRecyclerAdapter<TaskModel,TaskAdapter.onviewholder> {

    private FragmentManager fragmentManager;

    public TaskAdapter(@NonNull FirebaseRecyclerOptions<TaskModel> options ) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull onviewholder holder, int position, @NonNull TaskModel model) {

//        if (model!=null)
//        {
            holder.title.setText(model.getTitle());
            holder.date.setText(model.getDate());
            holder.description.setText(model.getDescription());
            holder.time.setText(model.getTime());
            holder.category.setText(model.getCategory());


            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(holder.itemView.getContext() , R.style.AlertDialogTheme);
                    alertDialog.setTitle("Delete Task");
                    alertDialog.setMessage("Are You Sure ?");
                    alertDialog.setIcon(R.drawable.taskmate);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference("Tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(model.getKey()).removeValue();
                            SuccessToast(holder.itemView.getContext(),"Task Successfully Deleted ");

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
                    alertDialog.show();

                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.getContext().startActivity(new Intent(view.getContext(), EditTaskActivity.class).putExtra("key",model.getKey()));
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.getContext().startActivity(new Intent(view.getContext(), Notes_Attachment.class).putExtra("key",model.getKey()));
                }
            });

//        }

    }



    @NonNull
    @Override
    public onviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row , parent,false);
        return new onviewholder(view);

    }

    public class onviewholder extends RecyclerView.ViewHolder {

        TextView title , description , date ,time , category ;
        Button edit , delete ;
        CardView cardView ;

        public onviewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            category = itemView.findViewById(R.id.category);
            cardView = itemView.findViewById(R.id.cardview);

        }
    }
}
