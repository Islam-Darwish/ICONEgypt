package com.mixapplications.iconegypt.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.Util;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    private ArrayList<Events> arrayEvents;
    private View.OnClickListener mOnItemClickListener;

    public EventsAdapter(Context context, Activity activity, ArrayList<Events> arrayEvents) {
        this.arrayEvents = arrayEvents;
        this.context = context;
        this.activity = activity;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_events_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLayoutParams(lp);
        return new EventsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsAdapter.MyViewHolder holder, int position) {
        holder.tvTitle.setText(arrayEvents.get(position).getTitle());
        holder.tvFrom.setText(arrayEvents.get(position).getFromEmployee().getName().equals("") ? arrayEvents.get(position).getFromEmployee().getEmail() : arrayEvents.get(position).getFromEmployee().getName());
        holder.tvDate.setText(Util.getDateCurrentTimeZone(arrayEvents.get(position).getDate()));
        holder.tvDetails.setText(arrayEvents.get(position).getDetails());
    }

    @Override
    public int getItemCount() {
        return (null != arrayEvents ? arrayEvents.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvFrom, tvDate, tvDetails;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
