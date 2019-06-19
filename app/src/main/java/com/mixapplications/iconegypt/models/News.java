package com.mixapplications.iconegypt.models;

import com.google.firebase.database.Exclude;

public class News {
    String fromEmail;
    String title;
    String textNews;
    long timestamp;


    public News() {
        timestamp = 0;
        fromEmail = "";
        title = "";
        textNews = "";
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getTextNews() {
        return textNews;
    }

    public void setTextNews(String textNews) {
        this.textNews = textNews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public long timestamp() {
        return timestamp;
    }

}
