package com.mixapplications.iconegypt.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.Util;

public class ViewEventCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button close;
    public TextView tv_title;
    private TextView tv_from;
    private TextView tv_Date;
    private TextView tv_details;
    private Events event;


    public ViewEventCustomDialog(Activity activity, Events event) {
        super(activity);
        this.event = event;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.view_event_custom_dialog);

        close = findViewById(R.id.btn_close);
        close.setOnClickListener(this);
        tv_title = findViewById(R.id.tvTitle);
        tv_from = findViewById(R.id.tvFrom);
        tv_Date = findViewById(R.id.tvDate);
        tv_details = findViewById(R.id.tvDetails);

        tv_title.setText(event.getTitle());
        tv_from.setText(event.getFromEmployee().getName().equals("") ? event.getFromEmployee().getEmail() : event.getFromEmployee().getName());
        tv_Date.setText(Util.getDateCurrentTimeZone(event.getDate()));
        tv_details.setText(event.getDetails());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
