package com.aliosman.privatesms.Model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

public class Conversation {

    private String name;
    private String message;
    private String number;
    private boolean isSent;
    private long date;

    public Conversation setName(String name) {
        this.name = name;
        return this;
    }

    public Conversation setMessage(String message) {
        this.message = message;
        return this;
    }

    public Conversation setNumber(String number) {
        this.number = number;
        return this;
    }

    public Conversation setSent(boolean sent) {
        isSent = sent;
        return this;
    }

    public Conversation setDate(long date) {
        this.date = date;
        return this;
    }

    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getNameText() {
        return (name==null || name.isEmpty() )? number : name;
    }

    public String getMessage() {
        return message;
    }

    public String getNumber() {
        return number;
    }

    public boolean isSent() {
        return isSent;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}