package com.c323FinalProject.larolsoncharfran;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView dueDateTime;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            dueDateTime = itemView.findViewById(R.id.dueDateTime);
            image = itemView.findViewById(R.id.image);
        }

        public void bindTask(Task task) {
            title.setText(task.getTitle());
            description.setText(task.getDescription());
            dueDateTime.setText(task.getDueDateTime().toString());
            if (task.getImage() != null) {
                image.setImageBitmap(task.getImage());
            }
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