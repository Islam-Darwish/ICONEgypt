package com.mixapplications.iconegypt.models;

import androidx.annotation.Nullable;

public class Employee {
    private String email;
    private String name;
    private String phone;
    private String image;
    private String status;
    private String type;
    private long lastNews;
    private long lastTask;
    private long lastEvent;
    private long lastForm;

    public Employee() {
        email = "";
        name = "";
        phone = "";
        image = "";
        type = "";
        status = "Holiday";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getLastNews() {
        return lastNews;
    }

    public void setLastNews(long lastNews) {
        this.lastNews = lastNews;
    }

    public long getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(long lastEvent) {
        this.lastEvent = lastEvent;
    }

    public long getLastForm() {
        return lastForm;
    }

    public void setLastForm(long lastForm) {
        this.lastForm = lastForm;
    }

    public long getLastTask() {
        return lastTask;
    }

    public void setLastTask(long lastTask) {
        this.lastTask = lastTask;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this.getEmail().equalsIgnoreCase(((Employee)obj).getEmail());
    }
}
