package com.mixapplications.iconegypt.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.dialogs.CreateEventCustomDialog;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class SentEventsAdapter extends RecyclerView.Adapter<SentEventsAdapter.MyViewHolder> {

    Context context;
    AppCompatActivity activity;
    DatabaseReference ref;
    private ArrayList<Events> arrayEvents;
    private View.OnClickListener mOnItemClickListener;

    public SentEventsAdapter(Context context, DatabaseReference ref, AppCompatActivity activity, ArrayList<Events> arrayEvents) {
        this.arrayEvents = arrayEvents;
        this.context = context;
        this.activity = activity;
        this.ref = ref;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public SentEventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send_event_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLayoutParams(lp);
        return new SentEventsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SentEventsAdapter.MyViewHolder holder, final int position) {
        holder.tvTitle.setText(arrayEvents.get(position).getTitle());
        if (arrayEvents.get(position).getToEmployee().size() == AppData.myEmployeeNode.returnAllNodes(AppData.myEmployeeNode).size() - 1) {
            holder.tvTo.setText("All Employees");
        } else {
            String[] names = new String[arrayEvents.get(position).getToEmployee().size()];
            for (int i = 0; i < arrayEvents.get(position).getToEmployee().size(); i++) {
                if (!arrayEvents.get(position).getToEmployee().get(i).getName().equalsIgnoreCase(""))
                    names[i] = arrayEvents.get(position).getToEmployee().get(i).getName();
                else
                    names[i] = arrayEvents.get(position).getToEmployee().get(i).getEmail();
            }
            String strNames = Arrays.toString(names).replace(",", " , ");
            holder.tvTo.setText(strNames);
        }
        holder.tvDate.setText(Util.getDateCurrentTimeZone(arrayEvents.get(position).getDate()));
        holder.tvDetails.setText(arrayEvents.get(position).getDetails());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppData.myEmployeeNode.getChildren().size()> 0)
                    new CreateEventCustomDialog(ref, activity, context, arrayEvents.get(position)).show();
                else
                    Toast.makeText(context,"Sorry you can not create Event" , Toast.LENGTH_LONG).show();
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
                                Util.removeEventDB(activity, context, ref, arrayEvents.get(position));
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
        return (null != arrayEvents ? arrayEvents.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvTo, tvDate, tvDetails;
        private Button btnEdit, btnRemove;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
