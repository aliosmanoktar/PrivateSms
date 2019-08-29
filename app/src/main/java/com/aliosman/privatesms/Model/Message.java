/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

public class Message {
    private Contact contact;
    private String message;
    private long time;
    private boolean sent;

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
}
