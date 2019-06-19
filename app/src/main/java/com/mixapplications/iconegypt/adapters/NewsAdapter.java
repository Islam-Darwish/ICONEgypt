package com.mixapplications.iconegypt.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.News;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import agency.tango.android.avatarview.views.AvatarView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    private ArrayList<News> arrayNews;
    private ArrayList<Employee> arrayEmployee;
    private View.OnClickListener mOnItemClickListener;

    public NewsAdapter(Context context, Activity activity, ArrayList<News> arrayNews, ArrayList<Employee> arrayEmployee) {
        this.arrayNews = arrayNews;
        this.arrayEmployee = arrayEmployee;
        this.context = context;
        this.activity = activity;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLayoutParams(lp);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvTitle.setText(arrayNews.get(position).getTitle());
        holder.tvFrom.setText(arrayEmployee.get(position).getName());
        holder.tvNews.setText(arrayNews.get(position).getTextNews());
        Picasso.get().load(arrayEmployee.get(position).getImage().equals("") ? null : arrayEmployee.get(position).getImage())
                .placeholder(R.drawable.ic_employee).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return (null != arrayEmployee ? arrayEmployee.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvFrom, tvNews;
        private AvatarView imgView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvNews = itemView.findViewById(R.id.tvNews);
            imgView = itemView.findViewById(R.id.imgView);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}