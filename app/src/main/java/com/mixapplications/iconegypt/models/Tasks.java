package com.mixapplications.iconegypt.models;

import com.google.firebase.database.Exclude;

public class Tasks {
    private String title;
    private Employee fromEmployee;
    private Employee toEmployee;
    private long fromDate;
    private long toDate;
    private String details;
    private long timestamp;

    public Tasks(String title, Employee fromEmployee, Employee toEmployee, long fromDate, long toDate, String details) {
        this.title = title;
        this.fromEmployee = fromEmployee;
        this.toEmployee = toEmployee;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.details = details;
        this.timestamp = 0;
    }

    public Tasks() {
        this.title = "";
        this.fromEmployee = null;
        this.toEmployee = null;
        this.fromDate = 0;
        this.toDate = 0;
        this.details = "";
        this.timestamp = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Employee getFromEmployee() {
        return fromEmployee;
    }

    public void setFromEmployee(Employee fromEmployee) {
        this.fromEmployee = fromEmployee;
    }

    public Employee getToEmployee() {
        return toEmployee;
    }

    public void setToEmployee(Employee toEmployee) {
        this.toEmployee = toEmployee;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Exclude
    public long timestamp() {
        return timestamp;
    }

}
