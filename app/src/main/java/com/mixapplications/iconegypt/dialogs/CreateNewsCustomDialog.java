package com.mixapplications.iconegypt.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.Util;

public class CreateNewsCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {

    public AppCompatActivity activity;
    public Button cancel;
    public Button share;
    Context context;
    private EditText tvNews;
    private EditText tvTitle;
    private Employee employee;
    private DatabaseReference ref;


    public CreateNewsCustomDialog(DatabaseReference ref, AppCompatActivity activity, Context context, Employee employee) {
        super(activity);
        this.employee = employee;
        this.activity = activity;
        this.ref = ref;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.create_news_custom_dialog);

        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        share = findViewById(R.id.btn_share);
        share.setOnClickListener(this);
        tvTitle = findViewById(R.id.tvTitle);
        tvNews = findViewById(R.id.tvNews);

        tvTitle.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_share:
                if (tvTitle.getText().toString().equals("")) {
                    Toast.makeText(context, "Title must not be empty", Toast.LENGTH_LONG).show();
                } else if (tvNews.getText().toString().equals("")) {
                    Toast.makeText(context, "News must not be empty", Toast.LENGTH_LONG).show();
                } else {
                    Util.createNewsDB(activity, context, ref, employee.getEmail(), tvTitle.getText().toString(), tvNews.getText().toString());
                    dismiss();
                }
                break;
            default:
                break;
        }

    }


}