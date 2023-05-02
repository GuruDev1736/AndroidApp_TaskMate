package com.example.taskmate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.zip.Inflater;

public class TaskAdapter extends FirebaseRecyclerAdapter<TaskModel,TaskAdapter.onviewholder> {

    public TaskAdapter(@NonNull FirebaseRecyclerOptions<TaskModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull onviewholder holder, int position, @NonNull TaskModel model) {

        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        holder.description.setText(model.getDescription());
        holder.time.setText(model.getTime());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("Tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(model.getKey()).removeValue();

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

    @NonNull
    @Override
    public onviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row , parent,false);
        return new onviewholder(view);

    }

    public class onviewholder extends RecyclerView.ViewHolder {

        TextView title , description , date ,time ;
        Button edit , delete ;

        public onviewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);

        }
    }
}
