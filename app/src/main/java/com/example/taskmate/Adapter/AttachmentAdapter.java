package com.example.taskmate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AttachmentAdapter extends FirebaseRecyclerAdapter<TaskModel,AttachmentAdapter.onviewholder> {

    public AttachmentAdapter(@NonNull FirebaseRecyclerOptions<TaskModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull onviewholder holder, int position, @NonNull TaskModel model) {

        holder.title.setText(model.getAttachment_name());

    }

    @NonNull
    @Override
    public onviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_view,parent,false);
        return new onviewholder(view);
    }

    public class onviewholder extends RecyclerView.ViewHolder {
            TextView title ;
        public onviewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);

        }
    }
}
