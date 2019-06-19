package com.mixapplications.iconegypt.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Tasks;
import com.mixapplications.iconegypt.models.Util;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    private ArrayList<Tasks> arrayTasks;
    private View.OnClickListener mOnItemClickListener;

    public TasksAdapter(Context context, Activity activity, ArrayList<Tasks> arrayTasks) {
        this.arrayTasks = arrayTasks;
        this.context = context;
        this.activity = activity;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public TasksAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tasks_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLayoutParams(lp);
        return new TasksAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksAdapter.MyViewHolder holder, int position) {
        holder.tvTitle.setText(arrayTasks.get(position).getTitle());
        holder.tvFrom.setText(arrayTasks.get(position).getFromEmployee().getName().equals("") ? arrayTasks.get(position).getFromEmployee().getEmail() : arrayTasks.get(position).getFromEmployee().getName());
        holder.tvFromDate.setText(Util.getDateCurrentTimeZone(arrayTasks.get(position).getFromDate()));
        holder.tvToDate.setText(Util.getDateCurrentTimeZone(arrayTasks.get(position).getToDate()));
        holder.tvTo.setText(arrayTasks.get(position).getToEmployee().getName().equals("") ? arrayTasks.get(position).getToEmployee().getEmail() : arrayTasks.get(position).getToEmployee().getName());
        holder.tvDetails.setText(arrayTasks.get(position).getDetails());
    }

    @Override
    public int getItemCount() {
        return (null != arrayTasks ? arrayTasks.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvFrom, tvTo, tvFromDate, tvToDate, tvDetails;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvFromDate = itemView.findViewById(R.id.tvFromDate);
            tvToDate = itemView.findViewById(R.id.tvToDate);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
