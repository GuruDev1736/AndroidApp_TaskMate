package com.example.taskmate.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.Activities.Constants;
import com.example.taskmate.Activities.DisplayDocument;
import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class AttachmentAdapter extends FirebaseRecyclerAdapter<TaskModel,AttachmentAdapter.onviewholder> {

    public AttachmentAdapter(@NonNull FirebaseRecyclerOptions<TaskModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull onviewholder holder, int position, @NonNull TaskModel model) {

        holder.title.setText(model.getAttachment_name());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri path = Uri.parse(model.getAttachment_URL());
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    holder.itemView.getContext().startActivity(pdfIntent);
                    Constants.SuccessToast(view.getContext(),"Pleases Wait....File is Loading");
                } catch (ActivityNotFoundException e) {
                    Constants.ErrorToast(view.getContext(),"No Application available to view PDF");
                }
            }
        });

    }

    @NonNull
    @Override
    public onviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_view,parent,false);
        return new onviewholder(view);
    }

    public class onviewholder extends RecyclerView.ViewHolder {
            TextView title ;
            CardView cardView ;
        public onviewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
