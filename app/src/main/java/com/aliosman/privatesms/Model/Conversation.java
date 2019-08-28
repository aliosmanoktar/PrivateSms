package com.aliosman.privatesms.Model;

public class Conversation {
    private String name;
    private String message;
    private String number;

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

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getNumber() {
        return number;
    }
}
