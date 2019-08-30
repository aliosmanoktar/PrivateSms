/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import android.text.format.DateUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    private int ID;
    private Contact contact;
    private String message;
    private long time;
    private boolean sent;

    public Message setID(int ID) {
        this.ID = ID;
        return this;
    }

    public Message setContact(Contact contact) {
        this.contact = contact;
        return this;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public Message setTime(long time) {
        this.time = time;
        return this;
    }

    public Message setSent(boolean sent) {
        this.sent = sent;
        return this;
    }

    public Contact getContact() {
        return contact;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public boolean isSent() {
        return sent;
    }

    public int getID() {
        return ID;
    }

    public String getTimeString(){
        DateFormat dateFormat =
                DateUtils.isToday(time) ?
                        new SimpleDateFormat("HH:mm") :
                        (IsYear() ?
                                new SimpleDateFormat("dd/MM\nHH:mm") :
                                new SimpleDateFormat("dd/MM/yyyy\nHH:mm") );
        return dateFormat.format(new Date(time));
    }

    private boolean IsYear(){
        Date date = new Date(time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
    }
}