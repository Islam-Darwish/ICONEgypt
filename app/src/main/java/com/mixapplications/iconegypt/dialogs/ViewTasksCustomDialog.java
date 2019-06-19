package com.mixapplications.iconegypt.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Tasks;
import com.mixapplications.iconegypt.models.Util;

public class ViewTasksCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button close;
    public TextView tv_title;
    private TextView tv_from;
    private TextView tv_to;
    private TextView tv_fromDate;
    private TextView tv_toDate;
    private TextView tv_task;
    private Tasks tasks;


    public ViewTasksCustomDialog(Activity activity, Tasks tasks) {
        super(activity);
        this.tasks = tasks;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.view_tasks_custom_dialog);

        close = findViewById(R.id.btn_close);
        close.setOnClickListener(this);
        tv_title = findViewById(R.id.tvTitle);
        tv_from = findViewById(R.id.tvFrom);
        tv_to = findViewById(R.id.tvTo);
        tv_fromDate = findViewById(R.id.tvFromDate);
        tv_toDate = findViewById(R.id.tvToDate);
        tv_task = findViewById(R.id.tvTask);

        tv_title.setText(tasks.getTitle());
        tv_from.setText(tasks.getFromEmployee().getName().equals("") ? tasks.getFromEmployee().getEmail() : tasks.getFromEmployee().getName());
        tv_to.setText(tasks.getToEmployee().getName().equals("") ? tasks.getToEmployee().getEmail() : tasks.getToEmployee().getName());
        tv_fromDate.setText(Util.getDateCurrentTimeZone(tasks.getFromDate()));
        tv_toDate.setText(Util.getDateCurrentTimeZone(tasks.getToDate()));
        tv_task.setText(tasks.getDetails());
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
