/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import android.text.format.DateUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message implements Serializable {
    private int ID;
    private Contact contact;
    private String message;
    private long sendDate;
    private int type;
    private boolean read;
    private long threadID;
    private long deliveredDate;

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

    public Message setSendDate(long sendDate) {
        this.sendDate = sendDate;
        return this;
    }

    public Message setDeliveredDate(long deliveredDate) {
        this.deliveredDate = deliveredDate;
        return this;
    }

    /**
     * @param type MESSAGE_TYPE_ALL    = 0;
     *             MESSAGE_TYPE_INBOX  = 1;
     *             MESSAGE_TYPE_SENT   = 2;
     *             MESSAGE_TYPE_DRAFT  = 3;
     *             MESSAGE_TYPE_OUTBOX = 4;
     *             MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages
     *             MESSAGE_TYPE_QUEUED = 6; // for messages to send later
     * @return
     */
    public Message setType(int type) {
        this.type = type;
        return this;
    }

    public Message setRead(boolean read) {
        this.read = read;
        return this;
    }

    public Message setThreadID(long threadID) {
        this.threadID = threadID;
        return this;
    }

    public Contact getContact() {
        return contact;
    }

    public String getMessage() {
        return message;
    }

    public long getSendDate() {
        return sendDate;
    }

    public boolean isSent() {
        return type != 1;
    }

    public int getID() {
        return ID;
    }

    public int getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public long getThreadID() {
        return threadID;
    }

    public String getSendTimeString() {
        return getString(sendDate);
    }

    public long getDeliveredDate() {
        return deliveredDate;
    }

    public String getDeliveredTimeString() {
        return getString(deliveredDate);
    }

    private String getString(long time) {
        DateFormat dateFormat =
                DateUtils.isToday(time) ?
                        new SimpleDateFormat("HH:mm") :
                        (IsYear(time) ?
                                new SimpleDateFormat("dd/MM\nHH:mm") :
                                new SimpleDateFormat("dd/MM/yyyy\nHH:mm"));
        return dateFormat.format(new Date(time));
    }

    private boolean IsYear(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
    }


}