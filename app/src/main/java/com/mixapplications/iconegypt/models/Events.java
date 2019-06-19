package com.mixapplications.iconegypt.models;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Events {
    private String title;
    private Employee fromEmployee;
    private List<Employee> toEmployee;
    private long date;
    private String details;
    private long timestamp;

    public Events() {
        this.title = "";
        this.date = 0;
        this.details = "";
        this.fromEmployee = null;
        this.toEmployee = null;
        this.timestamp = 0;
    }

    public Events(String title, Employee fromEmployee, List<Employee> toEmployee, long date, String details) {
        this.title = title;
        this.fromEmployee = fromEmployee;
        this.toEmployee = toEmployee;
        this.details = details;
        this.date = date;
    }

    public Employee getFromEmployee() {
        return fromEmployee;
    }

    public void setFromEmployee(Employee fromEmployee) {
        this.fromEmployee = fromEmployee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Employee> getToEmployee() {
        return toEmployee;
    }

    public void setToEmployee(List<Employee> toEmployee) {
        this.toEmployee = toEmployee;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Exclude
    public long timestamp() {
        return timestamp;
    }
}
