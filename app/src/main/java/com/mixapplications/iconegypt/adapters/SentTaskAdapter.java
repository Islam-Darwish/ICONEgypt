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
import com.mixapplications.iconegypt.dialogs.CreateTaskCustomDialog;
import com.mixapplications.iconegypt.models.Tasks;
import com.mixapplications.iconegypt.models.Util;

import java.util.ArrayList;

public class SentTaskAdapter extends RecyclerView.Adapter<SentTaskAdapter.MyViewHolder> {

    Context context;
    AppCompatActivity activity;
    DatabaseReference ref;
    private ArrayList<Tasks> arrayTasks;
    private View.OnClickListener mOnItemClickListener;

    public SentTaskAdapter(Context context, DatabaseReference ref, AppCompatActivity activity, ArrayList<Tasks> arrayTasks) {
        this.arrayTasks = arrayTasks;
        this.context = context;
        this.activity = activity;
        this.ref = ref;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public SentTaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send_task_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLayoutParams(lp);
        return new SentTaskAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SentTaskAdapter.MyViewHolder holder, final int position) {
        holder.tvTitle.setText(arrayTasks.get(position).getTitle());
        holder.tvTo.setText(arrayTasks.get(position).getToEmployee().getName().equals("") ? arrayTasks.get(position).getToEmployee().getEmail() : arrayTasks.get(position).getToEmployee().getName());
        holder.tvFromDate.setText(Util.getDateCurrentTimeZone(arrayTasks.get(position).getFromDate()));
        holder.tvToDate.setText(Util.getDateCurrentTimeZone(arrayTasks.get(position).getToDate()));
        holder.tvDetails.setText(arrayTasks.get(position).getDetails());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateTaskCustomDialog(ref, activity, context, arrayTasks.get(position)).show();
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
                                Util.removeTaskDB(activity, context, ref, arrayTasks.get(position));
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
        return (null != arrayTasks ? arrayTasks.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvTo, tvFromDate, tvToDate, tvDetails;
        private Button btnEdit, btnRemove;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvFromDate = itemView.findViewById(R.id.tvFromDate);
            tvToDate = itemView.findViewById(R.id.tvToDate);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}