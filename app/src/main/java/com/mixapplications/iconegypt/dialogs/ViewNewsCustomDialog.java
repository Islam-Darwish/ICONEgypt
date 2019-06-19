package com.mixapplications.iconegypt.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.News;
import com.squareup.picasso.Picasso;

import agency.tango.android.avatarview.views.AvatarView;

public class ViewNewsCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button close;
    public TextView tv_title;
    private TextView tv_from;
    private TextView tv_news;
    private AvatarView avatarView;
    private News news;
    private Employee employee;


    public ViewNewsCustomDialog(Activity activity, News news, Employee employee) {
        super(activity);
        this.news = news;
        this.employee = employee;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.view_news_custom_dialog);

        close = findViewById(R.id.btn_close);
        close.setOnClickListener(this);
        tv_title = findViewById(R.id.tvTitle);
        tv_from = findViewById(R.id.tvFrom);
        tv_news = findViewById(R.id.tvNews);
        avatarView = findViewById(R.id.imgView);

        tv_news.setText(news.getTextNews());
        tv_title.setText(news.getTitle());
        tv_from.setText(employee.getName());
        Picasso.get().load(employee.getImage().equals("") ? null : employee.getImage()).placeholder(R.drawable.profile_placeholder).into(avatarView);
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