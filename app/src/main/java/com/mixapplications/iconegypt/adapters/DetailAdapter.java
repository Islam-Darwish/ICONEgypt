package com.mixapplications.iconegypt.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Employee;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import agency.tango.android.avatarview.views.AvatarView;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.CustomViewHolder> {

    private static final int REQUEST_PHONE_CALL = 1;
    Context context;
    Activity activity;
    private ArrayList<Employee> arrayDetails;

33333333333333
    public DetailAdapter(Context context, Activity activity, ArrayList<Employee> arrayDetails) {
        this.arrayDetails = arrayDetails;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_employee_recycleview, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        customViewHolder.tvName.setText(arrayDetails.get(i).getName());
        customViewHolder.tvEmail.setText(arrayDetails.get(i).getEmail());
        customViewHolder.tvPhone.setText(arrayDetails.get(i).getPhone());
        customViewHolder.tvType.setText((arrayDetails.get(i).getType().substring(0, 1).toUpperCase() + arrayDetails.get(i).getType().substring(1)));
        /*//customViewHolder.tvStatus.setText(arrayDetails.get(i).getStatus());
        if (customViewHolder.tvStatus.getText().toString().equalsIgnoreCase("available")) {
            customViewHolder.tvStatus.setTextColor(0xff00ff00);
        } else if (customViewHolder.tvStatus.getText().toString().equalsIgnoreCase("busy")) {
            customViewHolder.tvStatus.setTextColor(0xffffb400);
        } else if (customViewHolder.tvStatus.getText().toString().equalsIgnoreCase("errand")) {
            customViewHolder.tvStatus.setTextColor(0xffeb0000);
        } else if (customViewHolder.tvStatus.getText().toString().equalsIgnoreCase("holiday")) {
            customViewHolder.tvStatus.setTextColor(0xffeb0000);
        }*/

        Picasso.get().load(arrayDetails.get(i).getImage().equals("") ? null : arrayDetails.get(i).getImage())
                .placeholder(R.drawable.ic_employee).into(customViewHolder.imgView);
        customViewHolder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + arrayDetails.get(i).getPhone()));
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayDetails ? arrayDetails.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvEmail, tvPhone, tvType, tvStatus;
        private AvatarView imgView;
        private Button callBtn;

        public CustomViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvType = view.findViewById(R.id.tvType);
            tvPhone = view.findViewById(R.id.tvPhone);
            imgView = view.findViewById(R.id.imgView);
            callBtn = view.findViewById(R.id.btn_call);
            // tvStatus = view.findViewById(R.id.tvStatus);
        }
    }

}