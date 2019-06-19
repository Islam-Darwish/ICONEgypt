package com.mixapplications.iconegypt.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.News;
import com.mixapplications.iconegypt.models.Util;

public class EditNewsCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {


    public AppCompatActivity activity;
    public Button cancel;
    public Button share;
    public EditText tvTitle;
    private EditText tvNews;
    private News news;
    private DatabaseReference ref;
    private Context context;


    public EditNewsCustomDialog(DatabaseReference ref, AppCompatActivity activity, Context context, News news) {
        super(activity);
        this.news = news;
        this.activity = activity;
        this.ref = ref;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.create_news_custom_dialog);

        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        share = findViewById(R.id.btn_share);
        share.setOnClickListener(this);
        tvTitle = findViewById(R.id.tvTitle);
        tvNews = findViewById(R.id.tvNews);
        tvTitle.setText(news.getTitle());
        tvNews.setText(news.getTextNews());
        setCancelable(false);
        tvTitle.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
                    Util.editNewsDB(activity, context, ref, news, tvTitle.getText().toString(), tvNews.getText().toString());
                    dismiss();
                }
                break;
            default:
                break;
        }
    }
}