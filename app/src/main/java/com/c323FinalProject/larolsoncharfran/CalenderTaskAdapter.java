package com.c323FinalProject.larolsoncharfran;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalenderTaskAdapter extends RecyclerView.Adapter<CalenderTaskAdapter.ViewHolder> {

    private ArrayList<Task> list;
    Activity activity;
    Task currentTask;

    public CalenderTaskAdapter(Activity activity, ArrayList<Task> list) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CircleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        }

        public void bindTask(Task task) {
            title.setText(task.getTitle() + " on " + task.getDueDateString());

            if (task.getImage() != null) {
                image.setImageBitmap(task.getImage());
            } else {
               image.setImageResource(R.drawable.ic_task);
            }

            System.out.println(task.getLocation().toString());
        }

    }
    @Override
    public CalenderTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendartask, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CalenderTaskAdapter.ViewHolder holder, int position) {
        holder.bindTask(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}