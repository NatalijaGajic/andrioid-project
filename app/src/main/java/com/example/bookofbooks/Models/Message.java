package com.example.bookofbooks.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message {
    private String from;
    private String message;
    private String date;
    private String time;

    public Message() {}


    public Message(String from, String message) {
        this.from = from;
        this.message = message;
        Calendar dateToCall = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        this.date = currentDateFormat.format(dateToCall.getTime());
        Calendar timeToCall = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        this.time = currentTimeFormat.format(timeToCall.getTime());
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
