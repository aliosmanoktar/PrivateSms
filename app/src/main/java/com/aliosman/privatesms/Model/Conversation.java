/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

public class Conversation {

    private Contact contact;
    private String message;
    private long date;
    private boolean read;
    private int count =0;
    private int type;

    public Conversation setType(int type) {
        this.type = type;
        return this;
    }

    public Conversation setContact(Contact contact) {
        this.contact = contact;
        return this;
    }

    public Conversation setMessage(String message) {
        this.message = message;
        return this;
    }

    public Conversation setDate(long date) {
        this.date = date;
        return this;
    }

    public Conversation setRead(boolean read) {
        this.read = read;
        return this;
    }

    public Conversation setCount(int count) {
        this.count = count;
        return this;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return type!=1;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean isRead() {
        return read;
    }

    public int getCount() {
        return count;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}