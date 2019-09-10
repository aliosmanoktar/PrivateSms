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
    private boolean isSent;
    private long date;
    private boolean read;
    public Conversation setContact(Contact contact) {
        this.contact = contact;
        return this;
    }

    public Conversation setMessage(String message) {
        this.message = message;
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

    public Conversation setRead(boolean read) {
        this.read = read;
        return this;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean isRead() {
        return read;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}