package com.mixapplications.iconegypt.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.dialogs.EditNewsCustomDialog;
import com.mixapplications.iconegypt.models.News;
import com.mixapplications.iconegypt.models.Util;

import java.util.ArrayList;

public class SentNewsAdapter extends RecyclerView.Adapter<SentNewsAdapter.MyViewHolder> {

    Context context;
    AppCompatActivity activity;
    DatabaseReference ref;
    private ArrayList<News> arrayNews;
    private View.OnClickListener mOnItemClickListener;

    public SentNewsAdapter(Context context, DatabaseReference ref, AppCompatActivity activity, ArrayList<News> arrayNews) {
        this.arrayNews = arrayNews;
        this.context = context;
        this.activity = activity;
        this.ref = ref;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    @Override
    public SentNewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_news_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLayoutParams(lp);
        return new SentNewsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SentNewsAdapter.MyViewHolder holder, final int position) {
        holder.tvTitle.setText(arrayNews.get(position).getTitle());
        holder.tvNews.setText(arrayNews.get(position).getTextNews());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditNewsCustomDialog(ref, activity, context, arrayNews.get(position)).show();
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Util.removeNewsDB(activity, context, ref, arrayNews.get(position));
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayNews ? arrayNews.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvNews;
        private Button btnEdit, btnRemove;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvNews = itemView.findViewById(R.id.tvNews);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnRemove = itemView.findViewById(R.id.btn_remove);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}