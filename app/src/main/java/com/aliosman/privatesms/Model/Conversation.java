/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Conversation {

    private Contact contact;
    private String message;
    private long date;
    private boolean read;
    private int count = 0;
    private int type;
    private boolean pinned = false;

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

    public Conversation setPinned(boolean pinned) {
        this.pinned = pinned;
        return this;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return type != 1;
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

    public boolean isPinned() {
        return pinned;
    }

    public String getTimeString() {
        DateFormat dateFormat =
                DateUtils.isToday(date) ?
                        new SimpleDateFormat("HH:mm") :
                        (IsYear() ?
                                new SimpleDateFormat("dd MMM") :
                                new SimpleDateFormat("dd MM yyyy"));
        return dateFormat.format(new Date(date));
    }

    private boolean IsYear() {
        Date date = new Date(getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
    }
    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}