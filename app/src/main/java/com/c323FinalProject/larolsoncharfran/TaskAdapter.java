package com.c323FinalProject.larolsoncharfran;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> list;
    Activity activity;
    Task currentTask;

    public TaskAdapter(Activity activity, ArrayList<Task> list) {
        this.list = list;
        this.activity = activity;
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<Task> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView title;
        TextView description;
        TextView dueDateTime;
        CircleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            dueDateTime = itemView.findViewById(R.id.dueDateTime);
            image = itemView.findViewById(R.id.image);

            itemView.setOnLongClickListener(this);
        }

        public void bindTask(Task task) {
            title.setText(task.getTitle());
            description.setText(task.getDescription());
            dueDateTime.setText(task.getDueDateTime().toString());

            if (task.getImage() != null) {
                image.setImageBitmap(task.getImage());
            } else {
               image.setImageResource(R.drawable.ic_task);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Are you sure do you want to delete this task?");

            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Delete from tasks list (and from pending/completed task list)
                            //Also update list adapter
                            Task deletedTask = LoginActivity.tasks.get(getAdapterPosition());

                            LoginActivity.tasks.remove(getAdapterPosition());
                            HomeFragment.getPendingAndCompleteTasks();
                            updateList(LoginActivity.tasks);

                            //Delete from DB
                            NavigationDrawer.myDB.execSQL("DELETE FROM " + NavigationDrawer.taskTableName + " WHERE Task_id='" + deletedTask.getId() + "'");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Do nothing
                        }
                    });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }

    }
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        holder.bindTask(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}